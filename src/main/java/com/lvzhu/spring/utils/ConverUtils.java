package com.lvzhu.spring.utils;

import java.util.Arrays;
import java.util.List;


/**
 * @author lvzhu.
 * Time 2019/7/10 10:06 AM
 * Desc 文件描述
 */
public class ConverUtils {

    /**
     * 根据传入的参数类型参数转换为数组
     * @param values
     * @param <T>
     * @return
     */
    private static <T> T[] of(T... values) {
        // 便利 API ，减少 new T[] 代码
        return values;
    }


    /**
     * 根据传入的类型参数转化为集合
     * @param values values
     * @param <T>  <T>
     * @return List<T>
     */
    private static <T> List<T> of2(T... values) {
        // 便利 API ，减少 new T[] 代码
        return Arrays.asList(of(values));
    }


}
