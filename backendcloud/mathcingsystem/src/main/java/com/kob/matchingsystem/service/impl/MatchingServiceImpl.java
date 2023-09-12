package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public static final MatchingPool matchingPool = new MatchingPool();     // 全局只有一个匹配线程，定义为静态

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        matchingPool.addPlayer(userId, rating);
        System.out.println("Add Player " + userId + " " + rating);
        return "Add Player Success!";
    }

    @Override
    public String removePlayer(Integer userId) {
        matchingPool.removePlayer(userId);
        System.out.println("Remove Player Success!");
        return "Remove Player Success!";
    }
}
