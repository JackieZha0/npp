package cn.zjk.npp.anotation;

import java.lang.annotation.*;

/**
 * @author zjk
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MySecurity {

    boolean encrypt() default true;

    boolean decrypt() default true;
}
