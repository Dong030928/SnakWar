package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {

    public static final BotPool botPool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        botPool.addBot(userId, botCode, input);
        System.out.println("Add Bot: " + userId + " " + botCode + " " + input);

        return "Add Bot Success! ";
    }
}
