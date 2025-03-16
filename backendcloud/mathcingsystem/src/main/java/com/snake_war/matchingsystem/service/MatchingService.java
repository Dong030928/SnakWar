package com.snake_war.matchingsystem.service;

public interface MatchingService {
    public String addPlayer(Integer userId, Integer rating, Integer botId);    // rating 为分值，匹配分数相近的
    public String removePlayer(Integer userId);
}
