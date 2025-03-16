package com.snake_war.backend.service.ranklist;

import com.alibaba.fastjson2.JSONObject;

public interface GetRankListService {
    public JSONObject getList(Integer page);
}
