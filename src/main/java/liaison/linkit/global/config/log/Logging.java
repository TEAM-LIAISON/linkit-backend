package liaison.linkit.global.config.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    String item();

    String action();

    LogLevel level() default LogLevel.INFO;

    boolean includeParams() default false;

    boolean includeResult() default false;

    String[] excludeParams() default {};
}
