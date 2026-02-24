package com.extreme.java;

import java.util.UUID;

public class TraceContext {
    private static final ThreadLocal<String> traceldHolder = new ThreadLocal<>();

    public static void startTrace(){
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        traceldHolder.set(uuid);
    }

    public static String getTraceId(){
        String id = traceldHolder.get();
        return (id != null) ? id : "NO-TRACE";
    }

    public static void clear(){
        traceldHolder.remove();
    }
}
