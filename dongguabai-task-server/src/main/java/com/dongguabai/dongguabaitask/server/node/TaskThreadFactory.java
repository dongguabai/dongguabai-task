package com.dongguabai.dongguabaitask.server.node;

public class TimeTaskThreadFactory implements ThreadFactory {

    private static final AtomicInteger NUM = new AtomicInteger(1);

    private static final String PREFIX = "timeTaskExecutor-";

    @Override
    public Thread newThread(Runnable r) {
        String name = PREFIX + NUM.getAndIncrement();
        ThreadGroup tg = System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup();
        return new Thread(tg, r, name);
    }

}
