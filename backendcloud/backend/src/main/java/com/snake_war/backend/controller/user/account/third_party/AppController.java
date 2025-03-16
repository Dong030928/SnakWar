package com.snake_war.backend.controller.user.account.third_party;

import com.alibaba.fastjson2.JSONObject;
import com.snake_war.backend.service.user.account.third_party.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AppController {

    @Autowired
    private AppService appService;

    @GetMapping("/api/user/account/third_party/app/apply_code")
    public JSONObject applyCode() {
        return appService.applyCode();
    }

    @GetMapping("/api/user/account/third_party/app/receive_code")
    public JSONObject receiveCode(@RequestParam Map<String, String> data) {
        String code = data.get("code");
        String state = data.get("state");
        return appService.receiveCode(code, state);
    }
}
