package com.kob.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;

public class Bot implements com.kob.botrunningsystem.utils.BotInterface {   // 因为要通过 Consumer 里的Reflect.compile()方法来编译，所以包名要加全来查找

    static class Cell {
        public int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public boolean checkTailIncreasing(int step) {   // 判断蛇在某回合是否变长
        if (step < 10) return true;
        return step % 3 == 1;
    }

    // 将蛇的身体取出来
    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));

        for (int i = 0; i < steps.length(); i ++ ) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            step ++;
            if (!checkTailIncreasing(step)) {
                res.remove(0);
            }
        }

        return res;
    }

    @Override
    public Integer nextMove(String input) {
        String[] info = input.split("#");

        // 将编码的地图还原成一个二维数组
        int[][] g = new int[13][14];
        for (int i = 0, k = 0; i < 13; i ++ ) {
            for (int j = 0; j < 14; j ++, k ++ ) {
                if (info[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(info[1]), aSy = Integer.parseInt(info[2]);
        int bSx = Integer.parseInt(info[4]), bSy = Integer.parseInt(info[5]);

        List<Cell> snake1 = getCells(aSx, aSy, info[3]);
        List<Cell> snake2 = getCells(bSx, bSy, info[6]);

        for (Cell cell: snake1) g[cell.x][cell.y] = 1;
        for (Cell cell: snake2) g[cell.x][cell.y] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i ++ ) {
            int x = snake1.get(snake1.size() - 1).x + dx[i];
            int y = snake1.get(snake1.size() - 1).y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }

        return 0;
    }
}
