package ru.ps.vlcatv.utils.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import androidx.annotation.Keep;

@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IDbIndexReflect {
    String value();
}
