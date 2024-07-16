package liaison.linkit.profile.browse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// 내 이력서 열람 요청
@Target(METHOD)
@Retention(RUNTIME)
public @interface CheckBrowseToPrivateProfileAccess {
    boolean checkPrivateBrowseAccess() default false;
}
