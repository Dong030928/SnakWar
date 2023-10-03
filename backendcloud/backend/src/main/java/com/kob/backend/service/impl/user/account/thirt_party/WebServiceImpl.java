package com.kob.backend.service.impl.user.account.thirt_party;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.account.third_party.WebService;
import org.springframework.stereotype.Service;

@Service
public class WebServiceImpl implements WebService {
    @Override
    public JSONObject applyCode() {
        return null;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        return null;
    }
}
