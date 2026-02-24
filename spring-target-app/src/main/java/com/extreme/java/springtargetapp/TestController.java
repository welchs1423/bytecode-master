package com.extreme.java.springtargetapp;

import com.extreme.java.Timer;
import com.extreme.java.LogData;
import com.extreme.java.CatchError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    @Timer
    @LogData
    public String testApi(@RequestParam String name, @RequestParam int age) throws InterruptedException {
        Thread.sleep(300);
        return "Hello, " + name + "! You are " + age + " years old.";
    }

    @GetMapping("/api/error")
    @CatchError
    public String errorApi(){
        throw new RuntimeException("스프링 부트 컨트롤러에서 의도적인 에러 발생!");
    }
}
