package com.hmall.common.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;

public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("authorization","2");
    }
}