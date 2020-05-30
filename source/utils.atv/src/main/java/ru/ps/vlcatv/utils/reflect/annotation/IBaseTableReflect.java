package ru.ps.vlcatv.utils.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IBaseTableReflect {
    public static final int CASCADE = 1000;
    public static final int DEFAULT = 0;
    public static final int NONE = -1;

    String IThisTableName() default "";
    String IParentTable() default "";
    String IParentKey() default "";
    int IParentSyncDelete() default DEFAULT;
    int IParentSyncUpdate() default DEFAULT;
    String[] IIndexOne() default {};
    IUniqueReflect[] IIndexUnique() default {};
}
