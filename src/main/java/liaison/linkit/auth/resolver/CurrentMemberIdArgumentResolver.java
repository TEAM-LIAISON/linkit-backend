package liaison.linkit.auth.resolver;

import java.util.Optional;

import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.auth.domain.Accessor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMemberId.class)
                && parameter.getParameterType().equals(Optional.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        Accessor accessor =
                (Accessor) webRequest.getAttribute("accessor", RequestAttributes.SCOPE_REQUEST);

        if (accessor != null && accessor.isMember()) {
            return Optional.of(accessor.getMemberId());
        }
        return Optional.empty();
    }
}
