package me.zhongmingmao.lock_support;

import java.util.concurrent.TimeUnit;

/**
 * 验证中断一个正在运行状态的线程，只会设置中断状态，而不会抛出InterruptedException
 */
public class InterruptRunning {
    private static Thread runningThread = new Thread(() -> {
        boolean hasPrintInterruptStatus = false;
        while (true) {
            if (!hasPrintInterruptStatus && Thread.currentThread().isInterrupted()) {
                log("interrupted when running!!");
                // 设置中断状态，但不会抛出InterruptedException
                log(String.format("interrupt status [%s]", Thread.currentThread().isInterrupted()));
                hasPrintInterruptStatus = true;
            }
        }
    }, "runningThread");
    
    private static Thread interruptThread = new Thread(() -> {
        log("before runningThread.interrupt()");
        runningThread.interrupt();
        log("after runningThread.interrupt()");
    }, "interruptThread");
    
    private static void log(String message) {
        System.out.println(String.format("%s : %s",
                Thread.currentThread().getName(),
                message));
    }
    
    public static void main(String[] args) throws InterruptedException {
        runningThread.start();
        TimeUnit.MILLISECONDS.sleep(100);
        interruptThread.start();
        /*
        输出：
        interruptThread : before runningThread.interrupt()
        interruptThread : after runningThread.interrupt()
        runningThread : interrupted when running!!
        runningThread : interrupt status [true]
         */
    }
}