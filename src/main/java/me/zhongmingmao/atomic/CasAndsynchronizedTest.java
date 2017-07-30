package me.zhongmingmao.atomic;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 比对CAS(无锁)和synchronize(锁)速度
 */
public class CasAndsynchronizedTest {
    private static final int THREAD_COUNT = 4;
    private static final long TASK_COUNT = 100 * 1000 * 1000;
    
    @Data
    static class Counter {
        private long count;
        
        public synchronized long incrementAndGet() {
            return ++count;
        }
    }
    
    private static void runWithCas() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        AtomicInteger counter = new AtomicInteger();
        
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> LongStream.range(0, TASK_COUNT)
                        .forEach(j -> counter.incrementAndGet())));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println(String.format("cas takes %sms",
                Duration.between(start, LocalDateTime.now()).toMillis())); // cas takes 6552ms
    }
    
    private static void runWithSynchronized() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        Counter counter = new Counter();
        
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> LongStream.range(0, TASK_COUNT)
                        .forEach(j -> counter.incrementAndGet())));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println(String.format("synchronized takes %sms",
                Duration.between(start, LocalDateTime.now()).toMillis())); // synchronized takes 17974ms
    }
    
    public static void main(String[] args) throws NoSuchFieldException, InterruptedException {
        runWithCas();
        runWithSynchronized();
    }
}