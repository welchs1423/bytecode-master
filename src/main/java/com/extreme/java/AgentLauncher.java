package com.extreme.java;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class AgentLauncher {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("에이전트가 JVM에 침투했습니다.");

        AgentBuilder.Listener listener = AgentBuilder.Listener.StreamWriting.toSystemOut();

        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.extreme.java"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder
                                .method(isAnnotatedWith(Timer.class))
                                .intercept(MethodDelegation.to(TimingInterceptor.class))
                                .method(isAnnotatedWith(LogData.class))
                                .intercept(MethodDelegation.to(DataLogInterceptor.class))
                                .method(isAnnotatedWith(CatchError.class))
                                .intercept(MethodDelegation.to(ExceptionIntercepter.class))
                )
                .installOn(inst);
    }

    public static class TimingInterceptor {
        @RuntimeType
        public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
            long start = System.nanoTime();
            try {
                return callable.call();
            } finally {
                long duration = System.nanoTime() - start;
                System.out.printf("[%s] 실행 시간: %.4fms%n", method.getName(), duration / 1_000_000.0);
            }
        }
    }

    public static class DataLogInterceptor {
        @RuntimeType
        public static Object intercept(@Origin Method method,
                                       @AllArguments Object[] args,
                                       @SuperCall Callable<?> callable) throws Exception {
            System.out.printf("[%s] 입력값: %s%n", method.getName(), java.util.Arrays.toString(args));
            Object result = callable.call();
            System.out.printf("[%s] 반환값: %s%n", method.getName(), result);
            return result;
        }
    }

    public static class ExceptionIntercepter {
        @RuntimeType
        public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
            try {
                return callable.call();
            } catch (Exception e){
                System.err.printf("[%s] 실행 중 예외 감지! 타입: %s, 메시지: %s%n",
                        method.getName(), e.getClass().getSimpleName(), e.getMessage());
                throw e;
            }
        }
    }
}