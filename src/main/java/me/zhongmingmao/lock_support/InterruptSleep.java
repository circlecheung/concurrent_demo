package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;

/**
 * 验证因sleep而进入TIMED_WAITING状态的线程被中断时，会抛出InterruptedException并重置中断状态
 */
public class InterruptSleep {
    private static Thread sleepThread = new Thread(() -> {
        try {
            log("before TimeUnit.SECONDS.sleep(10)");
            TimeUnit.SECONDS.sleep(10);
            log("after TimeUnit.SECONDS.sleep(10)");
        } catch (InterruptedException e) {
            log("interrupted when sleeping!!");
            // 抛出InterruptedException异常并重置中断状态
            log(String.format("interrupt status [%s]", Thread.currentThread().isInterrupted()));
        }
    }, "sleepThread");
    private static Thread interruptThread = new Thread(() -> {
        log("before sleepThread.interrupt()");
        sleepThread.interrupt();
        log("after sleepThread.interrupt()");
    }, "interruptThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) throws InterruptedException {
        sleepThread.start();
        TimeUnit.MILLISECONDS.sleep(100);
        interruptThread.start();
        /*
        输出：
        sleepThread : before TimeUnit.SECONDS.sleep(10)
        interruptThread : before sleepThread.interrupt()
        interruptThread : after sleepThread.interrupt()
        sleepThread : interrupted when sleeping!!
        sleepThread : interrupt status [false]
         */
    }
}