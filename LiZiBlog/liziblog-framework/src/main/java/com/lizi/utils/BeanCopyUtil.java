package com.lizi.utils;

import com.lizi.domain.entity.Article;
import com.lizi.domain.vo.HotArticleVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean拷贝工具类
 * */
public class BeanCopyUtil {
    private BeanCopyUtil(){

    }
    //单个bean拷贝  利用泛型
    public static <T> T copyBean(Object source,Class<T> clazz) {
        //创建目标对象  通过反射
        T result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回结果
        return result;
    }

    //多个bean拷贝
    public static <T> List<T> copyBeanList(List<?> list,Class<T> clazz){
        //使用stream流 和for循环效果相同
        return list.stream()
                .map(o -> copyBean(o,clazz))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("asdf");
        HotArticleVo hotArticleVo = copyBean(article, HotArticleVo.class);
        System.out.println(hotArticleVo);
    }
}
