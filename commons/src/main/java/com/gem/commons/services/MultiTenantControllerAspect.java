package com.gem.commons.services;

import com.gem.commons.InvalidOperationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;

@Aspect
public class MultiTenantControllerAspect {

    @Around("@target(com.gem.commons.services.MultiTenantController)")
    public void multiTenantController(ProceedingJoinPoint pjp)throws Throwable{

        String name = pjp.getTarget().getClass().getName();

        RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();
        if(attrs == null){
            throw new InvalidOperationException("Could not obtain " +
                    "RequestAttributes from the RequestContextHolder. Maybe " +
                    "this call is not web based. Target class: " + name +".");
        }

        if(attrs instanceof ServletRequestAttributes == false){
            throw new InvalidOperationException("The request is not from a " +
                    "servlet. Maybe this call is not web based. Target class: " + name +".");
        }

        HttpServletRequest req = ((ServletRequestAttributes)attrs).getRequest();
        if(req == null){
            throw new InvalidOperationException("Could not obtain " +
                    "HttpServletRequest from the attributes.");
        }

        String val = req.getHeader("Tenant-Id");

        if(val == null){
            throw new BadRequestException("The Tenant-Id header is required.");
        }

    }
}
