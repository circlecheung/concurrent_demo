package me.zhongmingmao.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 通过AtomicStampedReference解决ABA问题
 */
public class AtomicStampedReferenceDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(10, 0);
        
        Thread t1 = new Thread(() -> { // 构造ABA问题
            int stamp = stampedReference.getStamp();
            // 要注意autoboxing
            boolean success = stampedReference.compareAndSet(10, 20, stamp, stamp + 1);
            System.out.println(String.format("thread: %s , compareAndSet success : %s , current value : %s",
                    Thread.currentThread().getName(), success, stampedReference.getReference()));
            
            stamp = stampedReference.getStamp();
            success = stampedReference.compareAndSet(20, 10, stamp, stamp + 1);
            System.out.println(String.format("thread: %s , compareAndSet success : %s , current value : %s",
                    Thread.currentThread().getName(), success, stampedReference.getReference()));
        });
        
        Thread t2 = new Thread(() -> { // 遇到ABA问题，无法更新
            int stamp = stampedReference.getStamp();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean success = stampedReference.compareAndSet(10, 30, stamp, stamp + 1);
            System.out.println(String.format("thread: %s , compareAndSet success : %s , current value : %s",
                    Thread.currentThread().getName(), success, stampedReference.getReference()));
        });
        
        t2.start();
        TimeUnit.MILLISECONDS.sleep(200);
        t1.start();
    }
}