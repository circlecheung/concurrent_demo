package me.zhongmingmao.unsafe;

import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

public class UnsafePark {
    private static Thread mainThread;
    
    public static void main(String[] args) {
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        mainThread = Thread.currentThread();
        
        System.out.println(String.format("park %s", mainThread.getName())); // park main
        unsafe.park(false, TimeUnit.SECONDS.toNanos(1));
        
        new Thread(() -> {
            System.out.println(String.format("%s unpark %s",
                    Thread.currentThread().getName(),
                    mainThread.getName())); // Thread-0 unpark main
            unsafe.unpark(mainThread);
        }).start();
    }
}
