package com.lvzhu.spring.spring.annotation;

import java.lang.annotation.*;

/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-27 0:00
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default  "";
}
