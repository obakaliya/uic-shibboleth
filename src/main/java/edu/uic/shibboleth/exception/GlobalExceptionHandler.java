package edu.uic.shibboleth.exception;

import edu.uic.shibboleth.exception.auth.UnauthenticatedException;
import edu.uic.shibboleth.exception.auth.UnauthorizedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public String handleUnauthenticated(UnauthenticatedException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
        model.addAttribute("error", "Unauthenticated");
        model.addAttribute("message", ex.getMessage());
        return "error/401"; 
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); 
        model.addAttribute("error", "Unauthorized");
        model.addAttribute("message", ex.getMessage());
        return "error/403";  
    }
}
