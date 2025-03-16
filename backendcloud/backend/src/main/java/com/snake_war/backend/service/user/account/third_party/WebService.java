package com.snake_war.backend.service.user.account.third_party;

import com.alibaba.fastjson2.JSONObject;

public interface WebService {
    JSONObject applyCode();
    JSONObject receiveCode(String code, String state);
}
