package com.snake_war.botrunningsystem.utils;

import com.snake_war.botrunningsystem.pojo.Bot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 手动实现了个消息队列
 */
public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();    // 条件变量：用来控制队列的状态（阻塞 or 唤醒）
    private final Queue<Bot> bots = new LinkedList<>();   // 队列为空，进入阻塞状态；一旦有新的东西进来，将会被唤醒

    public void addBot(Integer userId, String botCode, String input) {  // 向队列中添加一个 Bot
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();  // 唤醒下边那个通过 condition.await() 方法来阻塞的线程
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await();  // 自动包含了锁释放的操作，不需要写 finally 块，因此异常的话要解锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);   // 比较耗时
            }
        }
    }

    private void consume(Bot bot) {     // 可以在此方法里修改成放在 Docker 里面跑（Java 操作终端命令，然后拿到返回结果）
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }


}
