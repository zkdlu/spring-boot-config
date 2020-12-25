package com.zkdlu.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Value("${hello.world}")
    private String identity;

    @GetMapping("/test")
    public String test() {
        return identity;
    }
}
