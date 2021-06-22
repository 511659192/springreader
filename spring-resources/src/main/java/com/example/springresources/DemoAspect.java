//// Copyright (C) 2021 Meituan
//// All rights reserved
//package com.example.springresources;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.SourceLocation;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
///**
// * @author Administrator
// * @version 1.0
// * @created 2021/6/5 22:00
// **/
//@Component
//@Aspect
//public class DemoAspect {
//
//    @Pointcut("execution(* com.example.springresources.DemoService.*(..))")
//    public void aspectPointcut() {
//    }
//
//    @Before(value = "aspectPointcut()")
//    public void aspectBefore(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        Signature signature = joinPoint.getSignature();
//        Object target = joinPoint.getTarget();
//        Object aThis = joinPoint.getThis();
//        JoinPoint.StaticPart staticPart = joinPoint.getStaticPart();
//        SourceLocation sourceLocation = joinPoint.getSourceLocation();
//        String longString = joinPoint.toLongString();
//        String shortString = joinPoint.toShortString();
//
//        System.out.println("【前置通知】");
//        System.out.println("\targs=" + Arrays.asList(args));
//        System.out.println("\tsignature=" + signature);
//        System.out.println("\ttarget=" + target);
//        System.out.println("\taThis=" + aThis);
//        System.out.println("\tstaticPart=" + staticPart);
//        System.out.println("\tsourceLocation=" + sourceLocation);
//        System.out.println("\tlongString=" + longString);
//        System.out.println("\tshortString=" + shortString);
//    }
//
//    /**
//     * 后置通知：目标方法执行之后执行以下方法体的内容，不管目标方法是否发生异常。
//     * value：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
//     */
//    @After(value = "aspectPointcut()")
//    public void aspectAfter(JoinPoint joinPoint) {
//        System.out.println("【后置通知】");
//        System.out.println("\tkind=" + joinPoint.getKind());
//    }
//
//    @Around(value="aspectPointcut()")
//    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
//        System.out.println("@Around：切点方法环绕start.....");
//
//        Object[] args = pjp.getArgs();
//        String kind = pjp.getKind();
//        Signature signature = pjp.getSignature();
//        Object target = pjp.getTarget();
//        SourceLocation sourceLocation = pjp.getSourceLocation();
//        JoinPoint.StaticPart staticPart = pjp.getStaticPart();
//        Object aThis = pjp.getThis();
//
//        System.out.println("\targs=" + Arrays.asList(args));
//        System.out.println("\tsignature=" + signature);
//        System.out.println("\ttarget=" + target);
//        System.out.println("\taThis=" + aThis);
//        System.out.println("\tstaticPart=" + staticPart);
//        System.out.println("\tsourceLocation=" + sourceLocation);
//        System.out.println("\tkind=" + kind);
//        Object o = pjp.proceed(args);
//        System.out.println("@Around：切点方法环绕end.....");
//        return o;
//    }
//}