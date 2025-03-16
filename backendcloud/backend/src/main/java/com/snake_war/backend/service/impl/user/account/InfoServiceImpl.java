package com.snake_war.backend.service.impl.user.account;

import com.snake_war.backend.pojo.User;
import com.snake_war.backend.service.impl.utils.UserDetailsImpl;
import com.snake_war.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getinfo() {
        /**
         * 在 Spring Security 中，已认证的用户信息会被存储在 Authentication 对象中，
         * Spring Security 会自动将该对象存储在 SecurityContextHolder 中，
         * 可以通过 SecurityContextHolder.getContext().getAuthentication() 方法获取该对象。
         */
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        map.put("rating", user.getRating().toString());

        return map;
    }
}
