package me.zhongmingmao.atomic;

import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.IntStream;

public class AtomicIntegerFieldUpdaterDemo {
    private static final int THREAD_COUNT = 4;
    private static final int TASK_COUNT = 1000 * 1000;
    
    @Data
    static class Counter {
        volatile int count; // 让原本没有原子更新能力的count具有原子更新能力
    }
    
    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerFieldUpdater<Counter> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Counter.class, "count");
        Counter counter = new Counter();
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> IntStream.range(0, TASK_COUNT)
                        .forEach(j -> fieldUpdater.incrementAndGet(counter))));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println(counter.getCount()); // 4000000 = THREAD_COUNT * TASK_COUNT
    }
}