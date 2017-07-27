package me.zhongmingmao._synchronized;

import java.util.concurrent.CountDownLatch;

/**
 * 重量级锁的性能测试
 */
public class FatLockingSpeedTest {
    static Number number = new Number();
    static final int THREAD_COUNT = 2;
    static CountDownLatch countDownLatch = new CountDownLatch(1000000000);
    
    
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(new Task()).start();
        }
        countDownLatch.await();
        System.out.println(String.format("%sms", System.currentTimeMillis() - start));
    }
    
    static class Number {
        int i;
        
        public synchronized void increase() {
            i++;
            countDownLatch.countDown();
        }
    }
    
    static class Task implements Runnable {
        
        @Override
        public void run() {
            while (countDownLatch.getCount() > 0) {
                number.increase();
            }
        }
    }
}
