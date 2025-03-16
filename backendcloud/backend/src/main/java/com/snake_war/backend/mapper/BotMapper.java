package com.snake_war.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.snake_war.backend.pojo.Bot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BotMapper extends BaseMapper<Bot> {
}
