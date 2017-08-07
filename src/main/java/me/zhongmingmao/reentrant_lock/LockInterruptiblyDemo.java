package me.zhongmingmao.reentrant_lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Debug查看lockInterruptibly的时同步队列的变化过程
 */
public class LockInterruptiblyDemo {
    static ReentrantLock lock = new ReentrantLock();
    static Runnable task = () -> {
        try {
            System.out.println(String.format("%s acquire lock...", Thread.currentThread().getName()));
            lock.lockInterruptibly();
            System.out.println(String.format("%s hold lock", Thread.currentThread().getName()));
        } catch (InterruptedException e) {
            System.out.println(String.format("%s interrupted", Thread.currentThread().getName()));
        } finally {
            // never unlock
        }
    };
    
    public static void main(String[] args) throws InterruptedException {
        Thread t0 = new Thread(task, "t0");
        Thread t1 = new Thread(task, "t1");
        Thread t2 = new Thread(task, "t2");
        Thread t3 = new Thread(task, "t3");
        Thread t4 = new Thread(task, "t4");
    
        // 确保t0持有锁，排队顺序为t1->t2->t3
        t0.start();
        sleepForAwhile();
        t1.start();
        sleepForAwhile();
        t2.start();
        sleepForAwhile();
        t3.start();
        sleepForAwhile();
        
        // 中断尾节点
        t3.interrupt();
        sleepForAwhile();
        
        // t4加入排队
        t4.start();
        sleepForAwhile();
        
        // 中断非尾节点、非head的后继节点
        t2.interrupt();
        sleepForAwhile();
        
        // 中断head的后继节点
        t1.interrupt();
    }
    
    private static void sleepForAwhile() throws InterruptedException {
        TimeUnit.SECONDS.sleep(500);
    }
}