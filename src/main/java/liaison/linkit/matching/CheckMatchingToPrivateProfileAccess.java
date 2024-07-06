package liaison.linkit.matching;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// 내 이력서로 매칭 요청
@Target(METHOD)
@Retention(RUNTIME)
public @interface CheckMatchingToPrivateProfileAccess {
    boolean checkPrivateMatchingAccess() default false;
}
