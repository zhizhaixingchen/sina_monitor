package com.sina.interceptor;

import com.sina.pojo.Loginuser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
        Loginuser loginuser = (Loginuser) request.getSession().getAttribute("loginuser");
        if (loginuser == null || loginuser.equals(""))  {
            response.sendRedirect("/index");
            return false;
        }
        return true;
    }
}