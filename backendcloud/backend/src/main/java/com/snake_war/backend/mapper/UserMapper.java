package com.snake_war.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.snake_war.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
