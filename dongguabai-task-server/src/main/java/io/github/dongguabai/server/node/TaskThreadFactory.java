package io.github.dongguabai.server.node;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 15:30
 */
public class TaskThreadFactory implements ThreadFactory {

    private static final AtomicInteger NUM = new AtomicInteger(1);

    private static final String PREFIX = "TaskExecutor-";

    @Override
    public Thread newThread(Runnable r) {
        String name = PREFIX + NUM.getAndIncrement();
        ThreadGroup tg = System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup();
        return new Thread(tg, r, name);
    }

}
