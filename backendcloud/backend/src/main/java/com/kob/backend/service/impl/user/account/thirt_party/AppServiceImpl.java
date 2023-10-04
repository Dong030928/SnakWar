package com.kob.backend.service.impl.user.account.thirt_party;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.third_party.AppService;
import com.kob.backend.utils.HttpClientUtil;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class AppServiceImpl implements AppService {
    private static final String appId = "6039";
    private static final String appSecret = "d0202de1b7af4ac3b10da8cbb7437125";
    private static final String redirectUri = "https://app6039.acapp.acwing.com.cn/api/user/account/third_party/app/receive_code";     // 回调链接，第三方服务器接收到用户确认授权后告诉 app 端的接口地址
    private static final String applyAccessTokenURL = "https://www.acwing.com/third_party/api/oauth2/access_token/";
    private static final String applyUserInfoURL = "https://www.acwing.com/third_party/api/meta/identity/getinfo/";

    private static final Random random = new Random();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject applyCode() {
        // 第一步 请求授权码
        JSONObject resp = new JSONObject();
        resp.put("appid", appId);
        resp.put("redirect_uri", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
        resp.put("scope", "userinfo");

        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i ++ ) {
            state.append((char)(random.nextInt(10) + '0'));
        }
        resp.put("state", state.toString());

        // 将 state 存到 redis 中，并设置 10min 的有效期
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        resp.put("result", "success");

        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        // 第二步 申请授权令牌access_token和用户的openid
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");

        if (code == null || state == null) return resp;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);    // 用完就删掉，防止被攻击

        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", appId));
        nameValuePairs.add(new BasicNameValuePair("secret", appSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        String getString = HttpClientUtil.get(applyAccessTokenURL, nameValuePairs);
        if (getString == null) return resp;

        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");
        String openid = getResp.getString("openid");
        if (accessToken == null || openid == null) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        List<User> users = userMapper.selectList(queryWrapper);
        // 如果存在 openid 对应的用户，直接返回即可
        if (!users.isEmpty()) {
            User user = users.get(0);
            String jwtToken = JwtUtil.createJWT(user.getId().toString());

            resp.put("result", "success");
            resp.put("jwt_token", jwtToken);

            return resp;
        }

        // 如果不存在，则申请
        nameValuePairs.clear();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));

        getString = HttpClientUtil.get(applyUserInfoURL, nameValuePairs);
        if (getString == null) return resp;

        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("username");
        String photo = getResp.getString("photo");
        if (username == null || photo == null) return resp;

        for (int i = 0; i < 100; i ++ ) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }

        User user = new User(null, username, null, photo, 1500, openid);
        userMapper.insert(user);

        String jwtToken = JwtUtil.createJWT(user.getId().toString());

        resp.put("result", "success");
        resp.put("jwt_token", jwtToken);

        return resp;
    }
}
