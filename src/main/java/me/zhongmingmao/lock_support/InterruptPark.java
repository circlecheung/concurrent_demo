package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 验证park能响应中断
 */
public class InterruptPark {
    private static Object BLOCKER = new Object();
    
    private static Thread parkThread = new Thread(() -> {
        log("before LockSupport.park(BLOCKER)");
        LockSupport.park(BLOCKER);
        log("after LockSupport.park(BLOCKER)");
    }, "parkThread");
    
    private static Thread interruptThread = new Thread(() -> {
        log("before parkThread.interrupt()");
        parkThread.interrupt();
        log("after parkThread.interrupt()");
    }, "interruptThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) throws InterruptedException {
        parkThread.start();
        TimeUnit.SECONDS.sleep(1);
        interruptThread.start();
        /*
        输出：
        parkThread : before LockSupport.park(BLOCKER)
        interruptThread : before parkThread.interrupt()
        interruptThread : after parkThread.interrupt()
        parkThread : after LockSupport.park(BLOCKER)
         */
    }
}