package com.lvzhu.spring.spring.annotation;

import java.lang.annotation.*;

/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-26 23:58
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default  "";
}
