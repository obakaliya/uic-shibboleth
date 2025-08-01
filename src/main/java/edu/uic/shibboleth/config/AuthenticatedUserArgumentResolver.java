package edu.uic.shibboleth.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import edu.uic.shibboleth.annotation.AuthenticatedUser;
import edu.uic.shibboleth.model.User;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class) 
               && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, 
                                WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return request.getAttribute("authUser");
    }
}