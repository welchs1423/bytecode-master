package com.extreme.java;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AgentLauncher {

    // JVM이 시작될 때 가장 먼저 실행되는 메서드
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("에이전트가 JVM에 침투했습니다.");

        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.extreme.java"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.any())
                                .intercept(MethodDelegation.to(TimingInterceptor.class))
                )
                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .installOn(inst);
    }

    // 메서드 실행 시간을 측정할 인터셉터 (내부 클래스로 간단히 구현)
    public static class TimingInterceptor {
        @RuntimeType
        public static Object intercept(@Origin Method method,
                                       @SuperCall Callable<?> callable) throws Exception {
            long start = System.nanoTime();
            try {
                return callable.call(); // 원래 메서드 실행
            } finally {
                long duration = System.nanoTime() - start;
                System.out.println("[" + method.getName() + "] 실행 시간: " + (duration / 1000000.0) + "ms");
            }
        }
    }
}