package liaison.linkit.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// 이 애노테이션이 메소드에만 적용될 수 있다.
@Target(METHOD)
// 이 애노테이션이 언제까지 유지되는지를 나타낸다.
// 이 애노테이션이 런타임 동안 유지되어 리플렉션(reflection)을 통해 접근 가능한 것을 알린다.
@Retention(RUNTIME)
public @interface MemberOnly {
}
