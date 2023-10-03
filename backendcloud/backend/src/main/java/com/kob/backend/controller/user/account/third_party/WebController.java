package com.kob.backend.controller.user.account.third_party;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.account.third_party.AppService;
import com.kob.backend.service.user.account.third_party.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WebController {

    @Autowired
    private WebService webService;

    @GetMapping("/api/user/account/third_party/web/apply_code")
    public JSONObject applyCode() {
        return webService.applyCode();
    }

    @GetMapping("/api/user/account/third_party/web/receive_code")
    public JSONObject receiveCode(@RequestParam Map<String, String> data) {
        String code = data.get("code");
        String state = data.get("state");
        return webService.receiveCode(code, state);
    }
}
