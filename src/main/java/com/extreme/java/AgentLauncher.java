package com.extreme.java;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

// ⭐ 이 줄이 가장 중요합니다!
import static net.bytebuddy.matcher.ElementMatchers.*;

public class AgentLauncher {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("에이전트가 JVM에 침투했습니다.");

        // 리스너 설정 (변환 과정을 로그로 출력)
        AgentBuilder.Listener listener = AgentBuilder.Listener.StreamWriting.toSystemOut();

        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.extreme.java"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(isAnnotatedWith(Timer.class)) // 여기서 에러가 났던 부분!
                                .intercept(MethodDelegation.to(TimingInterceptor.class))
                )
                //.with(listener)
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
}