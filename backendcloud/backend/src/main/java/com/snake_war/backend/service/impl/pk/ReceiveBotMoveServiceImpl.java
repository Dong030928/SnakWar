package com.snake_war.backend.service.impl.pk;

import com.snake_war.backend.consumer.WebSocketServer;
import com.snake_war.backend.consumer.utils.Game;
import com.snake_war.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;

            if (game.getPlayerA().getId().equals(userId)) {
                game.setNextStepA(direction);
            } else if (game.getPlayerB().getId().equals(userId)) {
                game.setNextStepB(direction);
            }
        }

        System.out.println("Receive Bot Move Direction: " + direction + " from user: " + userId);
        return "Receive Bot Move Success! ";
    }
}
