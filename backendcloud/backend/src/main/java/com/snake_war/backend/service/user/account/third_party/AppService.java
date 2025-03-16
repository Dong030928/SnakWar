package com.snake_war.backend.service.user.account.third_party;

import com.alibaba.fastjson2.JSONObject;

public interface AppService {
    JSONObject applyCode();     // 一个封装接口，返回一个链接，可以更方便的访问第三方的各种接口和API
    JSONObject receiveCode(String code, String state);  // 接收第三方发送的结果
}
