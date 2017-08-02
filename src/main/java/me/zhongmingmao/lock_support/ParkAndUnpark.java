package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 验证unpark(Thread thread)可以在park(Object blocker)前面（前提：被park的线程需要在执行unpark操作之前启动）
 */
public class ParkAndUnpark {
    private static Object BLOCKER = new Object();
    
    private static Thread parkThread = new Thread(() -> {
        try {
            TimeUnit.SECONDS.sleep(1); // 休眠1秒，等待unparkThread执行完
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log("before LockSupport.park(BLOCKER)");
        LockSupport.park(BLOCKER);
        log("after LockSupport.park(BLOCKER)");
    }, "parkThread");
    
    private static Thread unparkThread = new Thread(() -> {
        log("before LockSupport.unpark(parkThread)");
        LockSupport.unpark(parkThread);
        log("after LockSupport.unpark(parkThread)");
    }, "unparkThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) {
        parkThread.start();// parkThread必须要先启动，否则不保证LockSupport.unpark(parkThread)能让许可证有效
        unparkThread.start();
        /*
        输出：
        unparkThread : before LockSupport.unpark(parkThread)
        unparkThread : after LockSupport.unpark(parkThread)
        parkThread : before LockSupport.park(BLOCKER)
        parkThread : after LockSupport.park(BLOCKER)
         */
    }
}