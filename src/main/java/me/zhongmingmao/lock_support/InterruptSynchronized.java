package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;

/**
 * 验证Synchronized无法响应中断,要么获得锁，要么一直等待
 */
public class InterruptSynchronized {
    private static Object LOCK = new Object();
    
    private static Thread holdLockThread = new Thread(() -> {
        log("hold LOCK forever!!");
        synchronized (LOCK) {
            while (true) {
                Thread.yield(); // 只会尝试让出CPU资源，但不会释放锁资源
            }
        }
    }, "holdLockThread");
    
    private static Thread acquireLockThread = new Thread(() -> {
        log("try to acquire LOCK");
        synchronized (LOCK) {
            log("hold LOCK successfully!!");
        }
    }, "acquireLockThread");
    
    private static Thread interruptThread = new Thread(() -> {
        log(" interrupt acquireLockThread!!");
        acquireLockThread.interrupt();
    }, "interruptThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) throws InterruptedException {
        holdLockThread.start();
        TimeUnit.SECONDS.sleep(1); // 确保holdLockThread持有锁
        acquireLockThread.start(); // 尝试获得锁，进入阻塞状态
        TimeUnit.MILLISECONDS.sleep(100); // 确保acquireLockThread进入阻塞状态
        interruptThread.start();
        /*
        输出：
        holdLockThread : hold LOCK forever!!
        acquireLockThread : try to acquire LOCK
        interruptThread :  interrupt acquireLockThread!!
         */
    }
}