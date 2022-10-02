package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.LoginUser;
import com.lizi.domain.entity.User;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.mapper.UserMapper;
import com.lizi.service.UploadService;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.PathUtils;
import com.lizi.utils.RedisCache;
import com.lizi.utils.SecurityUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class UploadServiceimpl implements UploadService {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String url;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //TODO 判断文件类型和文件大小
        String fileType= img.getOriginalFilename();
        if(!fileType.endsWith(".jpg")&&!fileType.endsWith(".png")){
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //判断通过上传oss

        String filePath= PathUtils.generateFilePath(fileType);
        String url=uploadOss(img,filePath);
        if(url==null){
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }else{
            //修改用户头像
            Long id= null;
            try {
                id = SecurityUtils.getUserId();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
            }
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getId, id).set(User::getAvatar, url);
            Integer rows = userMapper.update(null, lambdaUpdateWrapper);
            if(rows>0){
                //TODO 删除redis
//                redisCache.deleteObject(SystemConstant.BLOG_LOGIN_REDIS_KEY+String.valueOf(id));
                User user = userMapper.selectById(id);
                LoginUser loginUser=new LoginUser();
                loginUser.setUser(user);
                redisCache.setCacheObject(SystemConstant.BLOG_LOGIN_REDIS_KEY+String.valueOf(id),
                       loginUser );
                return ResponseResult.okResult(url);
            }
            else{
                return ResponseResult.errorResult(AppHttpCodeEnum.FILE_TYPE_ERROR);
            }
        }
    }
    private String uploadOss(MultipartFile imgFile,String filePath){
        System.out.println("================");
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);

//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;

        try {
            InputStream inputStream=imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);


                return url+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }
}
