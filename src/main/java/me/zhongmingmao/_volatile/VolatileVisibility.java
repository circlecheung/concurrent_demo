package me.zhongmingmao._volatile;

/**
 * 结合happens-before说明volatile的可见性
 * @author zhongmingmao zhongmingmao0625@gmail.com
 */
public class VolatileVisibility {
    static class A {
        private int x = 0;
        private volatile int y = 0; // 如果y不声明为volatile，程序将无法结束
        
        private void write() {
            x = 5;
            y = 1;
            System.out.println("update x = 5");
        }
        
        private void read() {
            while (y < Integer.MAX_VALUE && x != 5) {
            }
            System.out.println("read x = 5");
        }
    }
    
    public static void main(String[] args) throws Exception {
        A a = new A();
        Thread writeThread = new Thread(() -> a.write());
        Thread readThread = new Thread(() -> a.read());
        
        readThread.start();
        Thread.sleep(100); // 休眠一段时间，确保readThread已经在运行，验证volatile的可见性
        writeThread.start();
    }
}