package com.sina.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginConfig implements WebMvcConfigurer{

    private LoginInterceptor loginInterceptor;
    public LoginConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**","/index/**","/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg",
                "/**/*.jpeg", "/**/*.gif", "/**/fonts/*", "/**/*.svg","/test2");
        //super.addInterceptors(registry);    //较新Spring Boot的版本中这里可以直接去掉，否则会报错
}
}
