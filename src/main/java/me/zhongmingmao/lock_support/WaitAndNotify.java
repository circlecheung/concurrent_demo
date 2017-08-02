package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;

/**
 * 验证notify()/notifyAll()必须在wait()之后
 */
public class WaitAndNotify {
    private static Object LOCK = new Object();
    
    private static Thread waitThread = new Thread(() -> {
        try {
            synchronized (LOCK) {
                log("before LOCK.wait()");
                LOCK.wait();
                log("after LOCK.wait()");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }, "waitThread");
    
    private static Thread notifyThread = new Thread(() -> {
        synchronized (LOCK) {
            log("before LOCK.notifyAll()");
            LOCK.notifyAll();
            log("after LOCK.notifyAll()");
        }
    }, "notifyThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) throws InterruptedException {
        notifyThread.start();
        TimeUnit.MILLISECONDS.sleep(100);
        waitThread.start();
        /*
        输出：
        notifyThread : before LOCK.notifyAll()
        notifyThread : after LOCK.notifyAll()
        waitThread : before LOCK.wait()
         */
    }
}