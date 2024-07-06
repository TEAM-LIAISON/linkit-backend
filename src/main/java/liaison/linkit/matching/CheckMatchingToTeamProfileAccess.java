package liaison.linkit.matching;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// 팀 소개서로 매칭 요청
@Target(METHOD)
@Retention(RUNTIME)
public @interface CheckMatchingToTeamProfileAccess {
    boolean checkTeam() default false;
}
