package com.kob.backend.consumer.utils;

import java.util.Arrays;
import java.util.Random;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer innerWallsCount;
    private final int[][] g;
    private final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer innerWallsCount) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.g = new int[rows][cols];   // g[i][j] = 0 代表空地，= 1 代表墙
    }

    public int[][] getG() {
        return g;
    }

    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {      // 判断图的连通性(左下右上是否连通) --- Floyd Fill 算法
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
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - r] == 1) continue;
                if ((r == this.rows - 2 && c == 1) || (c == this.cols - 2 && r == 1)) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;

                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createGameMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }
}
