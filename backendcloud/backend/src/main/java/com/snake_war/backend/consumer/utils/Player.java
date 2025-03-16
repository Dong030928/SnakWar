package com.snake_war.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;      // 1 人打 2 AI打
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    public boolean checkTailIncreasing(int step) {   // 判断蛇在某回合是否变长
        if (step < 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = this.sx, y = this.sy;
        int step = 0;
        res.add(new Cell(x, y));

        for (int d: steps) {
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

    public String getStepsString() {
        StringBuilder sb = new StringBuilder();
        for (int d: steps) {
            sb.append(d);
        }
        return sb.toString();
    }
}
