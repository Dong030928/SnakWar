package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.config.WebSocketConfig;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();  // 维护两个用户对应的链接
    private Session session = null;  // 每个连接用 Session 来维护，这个 Session 不是 Http 的 Session，是 WebSocket 包中的
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;
    public static RestTemplate restTemplate;
    public Game game = null;
    private static final String addPlayerUrl = "http://127.0.0.1:3001/player/add";
    private static final String removePlayerUrl = "http://127.0.0.1:3001/player/remove";


    @Autowired      // 通过这种方式，可以保证每次注入的实例不会覆盖之前的实例，并且允许 WebSocketServer 实例通过静态字段userMapper访问被注入的实例。
    private void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    private void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    private void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    private void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {   // 建立连接
        this.session = session;

        // 通过解析前端访问 PK页面传过来的 token 拿到相应的 user
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(user.getId() + " Connected!");
    }

    @OnClose
    public void onClose() {     // 关闭链接
        if (!users.isEmpty()) {
            users.remove(this.user.getId());
        }

        System.out.println(this.user.getId() + " Disconnected!");
    }

    @OnMessage
    public void onMessage(String message, Session session) {    // 从 Client(前端) 接收消息

        JSONObject data = JSONObject.parseObject(message);

        String event = data.getString("event");

        System.out.println("receive message from client: " + event);

        switch (event) {
            case "start-matching" -> startMatching(data.getInteger("bot_id"));
            case "stop-matching" -> stopMatching();
            case "move" -> move(data.getInteger("direction"));
            default -> {}
        }
    }

    // 分别设置玩家 A 和 B 的移动
    private void move(int direction) {
        // 只有当选择的 Bot id 为 -1 的时候，才执行人工操作
        if (game.getPlayerA().getId().equals(user.getId())) {
            if (game.getPlayerA().getBotId().equals(-1)) {
                game.setNextStepA(direction);
            }
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            if (game.getPlayerB().getBotId().equals(-1)) {
                game.setNextStepB(direction);
            }
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

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(13, 14, 20, a.getId(), botA, b.getId(), botB);
        game.createGameMap();

        if (users.get(a.getId()) != null) users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null) users.get(b.getId()).game = game;

        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        // 匹配成功，向前端返回成功信息
        JSONObject respA = new JSONObject();
        respA.put("event", "match success");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("opponent_rating", b.getRating());
        respA.put("game", respGame);
        if (users.get(a.getId()) != null) users.get(a.getId()).sendMessage(respA.toJSONString());     // 向A通知匹配成功，同时也要向B通知匹配成功

        JSONObject respB = new JSONObject();
        respB.put("event", "match success");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("opponent_rating", a.getRating());
        respB.put("game", respGame);
        if (users.get(b.getId()) != null) users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());

        String info = restTemplate.postForObject(WebSocketServer.addPlayerUrl, data, String.class);

        System.out.println(info);
    }

    private void stopMatching() {
        System.out.println(this.user.getId() + " Stop Matching!");

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());

        restTemplate.postForObject(WebSocketServer.removePlayerUrl, data, String.class);
    }
}
