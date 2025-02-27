package com.kob.matchingsystem.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final String startGameUrl = "http://127.0.0.1:3000/pk/start/game";
    public static RestTemplate restTemplate;
 
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }


    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();

        try {
            for (Player player: players) {
                if (player.getUserId().equals(userId)) {
                    players.remove(player);
                    break;
                }
            }
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();

        try {
            // 直接用 remove方法 的话，其余元素会按 O(n) 的时间复杂度重排，效率低
            List<Player> newPlayers = new ArrayList<>();
            for (Player player: players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    // 未匹配成功的话，将所有的玩家等待时间 +1
    private void increaseWaitingTime() {
        for (Player player: players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    // 判断两名玩家是否匹配
    private boolean checkMatch(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());

        return ratingDelta <= waitingTime * 10;
    }

    // 匹配成功，返回玩家 a，b 作为匹配结果（前面的 backend 模块也要写一个相应的方法用来接收返回的信息）
    private void sendResult(Player a, Player b) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());

        String info = restTemplate.postForObject(startGameUrl, data, String.class);

        System.out.println(startGameUrl + "返回的消息：" + info);
    }

    // 尝试匹配线程池里的所有的玩家
    private void match() {
        boolean[] isMatched = new boolean[players.size()];

        for (int i = 0; i < players.size() - 1; i ++ ) {
            if (isMatched[i]) continue;

            for (int j = i + 1; j < players.size(); j ++ ) {
                if (isMatched[j]) continue;

                Player a = players.get(i), b = players.get(j);

                if (checkMatch(a, b)) {
                    isMatched[i] = isMatched[j] = true;
                    sendResult(a, b);
                    break;  // 成功匹配第 i 个后，跳出内层循环，匹配第 i + 1 个
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++ ) {
            if (!isMatched[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        // 一个死循环，每次等待一秒（为了更新线程池里的用户），然后进行匹配
        while (true) {
            try {
                Thread.sleep(1000);

                lock.lock();
                try {
                    increaseWaitingTime();
                    match();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
