package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer innerWallsCount;
    private final int[][] g;
    private final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();       // 加锁
    private String status = "playing";      // 2 values: playing, finished
    private String loser = "";      // 3 values: all, A, B
    private static final String addBotUrl = "http://127.0.0.1:3002/bot/add";

    public Game(Integer rows, Integer cols, Integer innerWallsCount, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.g = new int[rows][cols];   // g[i][j] = 0 代表空地，= 1 代表墙

        Integer aBotId = -1, bBotId = -1;
        String aBotCode = "", bBotCode = "";

        if (botA != null) {
            aBotId = botA.getId();
            aBotCode = botA.getContent();
        }
        if (botB != null) {
            bBotId = botB.getId();
            bBotCode = botB.getContent();
        }

        playerA = new Player(idA, aBotId, aBotCode, this.rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, bBotId, bBotCode, 1, this.cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return this.playerA;
    }

    public Player getPlayerB() {
        return this.playerB;
    }

    public int[][] getG() {
        return g;
    }

    // 设置 A 的下一步操作
    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    // 设置 B 的下一步操作
    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    // 判断图的连通性(左下右上是否连通) --- Floyd Fill 算法
    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;

        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y <= this.cols && g[x][y] == 0) {
                if (checkConnectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;

        return false;
    }

    // 绘制地图
    private boolean draw() {
        for (int i = 0; i < this.rows; i ++ ) {
            Arrays.fill(g[i], 0);
        }

        // 四周加墙
        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        // 创建随机墙
        Random random = new Random();
        for (int i = 0; i < innerWallsCount / 2; i ++ ) {
            for (int j = 0; j < 100; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                // 中心对称
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;
                if ((r == this.rows - 2 && c == 1) || (c == this.cols - 2 && r == 1)) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;

                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    // 创建地图
    public void createGameMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }

    // 拿到当前局面（将当前的局面信息编码成字符串）
    public String getInput(Player player) {
        Player me, oppose;
        if (player.getId().equals(playerA.getId())) {
            me = playerA;
            oppose = playerB;
        } else {
            me = playerB;
            oppose = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                oppose.getSx() + "#" +
                oppose.getSy() + "#(" +
                oppose.getStepsString() + ")";  // 括号括起来是避免操作数为空 split 分割错误的情况
    }

    public void sendBotCode(Player player) {
        if (player.getId().equals(-1)) return;

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();

        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    // 等待玩家下一步操作
    public boolean nextStep() {
        try {
            Thread.sleep(500);  // 先沉睡走一步需要的最小时间，避免重复操作覆盖
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++ ) {
            try {
                Thread.sleep(100);

                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // 开启新的线程来处理游戏
    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++ ) {
            if (nextStep()) {   // to judge whether it has next step
                judge();

                if (("playing").equals(status)) {
                    sendMoveInfoToClient();
                } else {
                    sendResultInfoToClient();
                    break;
                }
            } else {
                this.status = "finished";

                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        this.loser = "all";
                    } else if (nextStepA == null) {
                        this.loser = "A";
                    } else {
                        this.loser = "B";
                    }
                } finally {
                    lock.unlock();
                }

                sendResultInfoToClient();
                break;
            }
        }
    }

    private void sendAllMessageToClient(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);

        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendResultInfoToClient() {  // 向两个客户端发送游戏结果
        JSONObject resp = new JSONObject();

        resp.put("event", "result");
        resp.put("loser", this.loser);

        saveRecordToDB();

        sendAllMessageToClient(resp.toJSONString());
    }

    private void sendMoveInfoToClient() {    // 向两个客户端发送移动信息
        lock.lock();

        try {
            JSONObject resp = new JSONObject();

            resp.put("event", "move");
            resp.put("a_direction", this.nextStepA);
            resp.put("b_direction", this.nextStepB);

            sendAllMessageToClient(resp.toJSONString());

            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    // 检查操作是否合法
    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();  // 两条蛇同步走，所以长度一样
        Cell cell = cellsA.get(n - 1);

        if (g[cell.getX()][cell.getY()] == 1) return false;

        // 是否与A蛇的身体重合
        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsA.get(i).getX() == cell.getX() && cellsA.get(i).getY() == cell.getY())
                return false;
        }

        // 是否与B蛇的身体重合
        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsB.get(i).getX() == cell.getX() && cellsB.get(i).getY() == cell.getY())
                return false;
        }

        return true;
    }

    // 判断两名玩家的下一步操作是否合法
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);

        if (!validA || !validB) {
            this.status = "finished";

            if (!validA && !validB) {
                this.loser = "all";
            } else if (!validA) {
                this.loser = "A";
            } else {
                this.loser = "B";
            }
        }
    }

    private void saveRecordToDB() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        int deltaRatings = Math.abs(ratingA - ratingB);

        if ("A".equals(this.loser)) {
            ratingA -= 2;
            ratingB += 5;
        } else if ("B".equals(this.loser)) {
            ratingB -= 2;
            ratingA += 5;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                this.loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private String getMapString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                sb.append(g[i][j]);
            }
        }

        return sb.toString();
    }
}
