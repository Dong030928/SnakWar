package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.config.WebSocketConfig;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    private User user;
    // 匹配池，维护对战的两个用户
    private static final CopyOnWriteArrayList<User> matchPool = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private Session session = null;  // 每个连接用 Session 来维护，这个 Session 不是 Http 的 Session，是 WebSocket 包中的
    private static UserMapper userMapper;

    @Autowired      // 通过这种方式，可以保证每次注入的实例不会覆盖之前的实例，并且允许WebSocketServer实例通过静态字段userMapper访问被注入的实例。
    private void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("Connected!");

        // 通过解析前端访问 PK页面传过来的 token 拿到相应的 user
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(user.getId());
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        if (!users.isEmpty()) {
            users.remove(this.user.getId());
            matchPool.remove(this.user);
        }

        System.out.println("Disconnected!");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从 Client(前端) 接收消息
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        }
    }



    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 发送消息给前端
    public void sendMessage(String message) {
        synchronized(this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMatching() {
        matchPool.add(this.user);   // 开始匹配时将当前用户加进去
        System.out.println(this.user.getId() + " Start Matching!");

        while (matchPool.size() >= 2) {
            Iterator<User> it = matchPool.iterator();
            User a = it.next(), b = it.next();
            matchPool.remove(b);
            matchPool.remove(a);

            Game game = new Game(13, 14, 20);
            game.createGameMap();

            // 匹配成功，向前端返回成功信息
            JSONObject respA = new JSONObject();
            respA.put("event", "match success");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game_map", game.getG());
            users.get(a.getId()).sendMessage(respA.toJSONString());     // 向A通知匹配成功，同时也要向B通知匹配成功

            JSONObject respB = new JSONObject();
            respB.put("event", "match success");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game_map", game.getG());
            users.get(b.getId()).sendMessage(respB.toJSONString());

        }
    }

    private void stopMatching() {
        matchPool.remove(this.user);   // 开始匹配时将当前用户加进去
        System.out.println(this.user.getId() + " Stop Matching!");
    }
}
