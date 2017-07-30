package me.zhongmingmao.unsafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class UnsafeCAS {
    
    private static final int THREAD_COUNT = 4;
    private static final long TASK_COUNT = 500 * 1000 * 1000;
    
    @Data
    @AllArgsConstructor
    static class Counter {
        private long count;
    }
    
    public static void main(String[] args) throws NoSuchFieldException, InterruptedException {
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        Field count = Counter.class.getDeclaredField("count");
        long offset = unsafe.objectFieldOffset(count);
        Counter counter = new Counter(0);
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> LongStream.range(0, TASK_COUNT)
                        .forEach(j -> unsafe.getAndAddInt(counter, offset, 1))));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        
        // CAS实现并发安全
        System.out.println(counter.getCount()); // 2,000,000,000 = THREAD_COUNT * TASK_COUNT
    }
}