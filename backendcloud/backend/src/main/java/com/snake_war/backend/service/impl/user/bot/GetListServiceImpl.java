package com.snake_war.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.snake_war.backend.mapper.BotMapper;
import com.snake_war.backend.pojo.Bot;
import com.snake_war.backend.pojo.User;
import com.snake_war.backend.service.impl.utils.UserDetailsImpl;
import com.snake_war.backend.service.user.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetListServiceImpl implements GetListService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public List<Bot> getList() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl logUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = logUser.getUser();

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        // 数据库中字段列表为 “user_id” 为 user.getId() 的记录
        queryWrapper.eq("user_id", user.getId());

        return botMapper.selectList(queryWrapper);
    }
}
