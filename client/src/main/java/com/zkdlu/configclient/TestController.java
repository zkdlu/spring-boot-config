package com.zkdlu.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Value("${hello.world}")
    private String test;

    @GetMapping
    public String hello() {
        return test;
    }
}
