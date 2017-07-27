package me.zhongmingmao._synchronized;

/**
 * 偏向锁与轻量级锁的性能对比
 */
// JVM Args : -XX:BiasedLockingStartupDelay=10000
// JVM Args : -XX:BiasedLockingStartupDelay=0
public class BiasedLockingSpeedTest {
    static Number number = new Number();
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long i = 0;
        while (i++ < 1000000000L) {
            number.increase();
        }
        System.out.println(String.format("%sms", System.currentTimeMillis() - start));
    }
    
    static class Number {
        int i;
        
        public synchronized void increase() {
            i++;
        }
    }
}
