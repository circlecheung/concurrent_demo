package me.zhongmingmao._synchronized;

import org.openjdk.jol.datamodel.X86_32_DataModel;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.layouters.HotSpotLayouter;
import org.openjdk.jol.layouters.Layouter;

import java.util.concurrent.TimeUnit;

// JVM Args : -Djol.tryWithSudo=true
public class FatLockingWaitMarkWord {
    static Object object = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        Layouter layouter = new HotSpotLayouter(new X86_32_DataModel());
        ClassLayout layout = ClassLayout.parseInstance(object, layouter);
        new Thread(() -> {
            try {
                synchronized (object) {
                    System.out.println("**** Before wait");
                    System.out.println(layout.toPrintable());
                    object.wait();
                    System.out.println("**** After wait");
                    System.out.println(layout.toPrintable());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        TimeUnit.SECONDS.sleep(10);
        synchronized (object) {
            object.notifyAll();
        }
    }
}