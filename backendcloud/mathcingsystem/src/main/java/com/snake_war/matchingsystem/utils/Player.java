package com.snake_war.matchingsystem.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer userId;
    private Integer rating;
    private Integer botId;
    private Integer waitingTime;    // 等待时间
}
