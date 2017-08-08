package me.zhongmingmao.reentrant_lock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用Condition实现生产者-消费者
 * @author zhongmingmao zhongmingmao0625@gmail.com
 */
public class ProducerAndConsumer {
    
    static class Buffer {
        // 缓冲区大小
        private static final int BUFFER_LENGTH = 5;
        
        // 非公平锁
        private final Lock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();
        
        // 缓冲区
        private final Object[] buffer = new Object[BUFFER_LENGTH];
        
        int produceIndex;
        int consumeIndex;
        int count;
        
        public void produce() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (count == BUFFER_LENGTH) {
                        System.out.println("buffer is full , need to consume");
                        notFull.await();  // 缓存区已满，需要等待消费者消费后，唤醒生产者才能继续生产
                    }
                    buffer[produceIndex++] = new Object();
                    produceIndex %= BUFFER_LENGTH;
                    ++count;
                    System.out.println(String.format("produce buffer[%s] , buffer size : %s",
                            (BUFFER_LENGTH + produceIndex - 1) % BUFFER_LENGTH, count));
                    notEmpty.signal(); // 已经生产，唤醒消费者去消费
                } finally {
                    lock.unlock();
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000)); // 模拟生产耗时，并让消费者能获得锁
                }
            }
        }
        
        public void consume() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (count == 0) {
                        System.out.println("buffer is full , need to produce");
                        notEmpty.await(); // 缓存区为空，需要等待生产者生产完成后，唤醒消费者
                    }
                    Object x = buffer[consumeIndex++];
                    consumeIndex %= BUFFER_LENGTH;
                    --count;
                    System.out.println(String.format("consume buffer[%s] , buffer size : %s",
                            (BUFFER_LENGTH + consumeIndex - 1) % BUFFER_LENGTH, count));
                    notFull.signal(); // 已经消费，唤醒生产者去生产
                } finally {
                    lock.unlock();
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000)); // 模拟消费耗时，并让生产者能获得锁
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer();
        
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> { // 生产者线程
            try {
                buffer.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        pool.submit(() -> { // 消费者线程
            try {
                buffer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}