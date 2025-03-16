package com.snake_war.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.snake_war.backend.mapper.UserMapper;
import com.snake_war.backend.pojo.User;
import com.snake_war.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 这是一个实现了 Spring Security中的 UserDetailsService 接口的服务类，用来从数据库中加载用户的认证信息和权限信息。
 * 在用户成功登录之后，Spring Security 会创建一个 Authentication 对象，该对象保存了用户的认证信息和权限信息，并将其存储到 SecurityContextHolder 中。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return new UserDetailsImpl(user);
    }
}
