package me.zhongmingmao._synchronized;

import org.openjdk.jol.datamodel.X86_32_DataModel;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.layouters.HotSpotLayouter;
import org.openjdk.jol.layouters.Layouter;

import static java.lang.System.out;

// JVM Args : -Djol.tryWithSudo=true
public class ThinLockingMarkWord {
    static class A {
    }
    
    public static void main(String[] args) throws InterruptedException {
        Layouter layouter = new HotSpotLayouter(new X86_32_DataModel());
        
        final A a = new A();
        
        ClassLayout layout = ClassLayout.parseInstance(a, layouter);
        out.println("**** Fresh object");
        out.println(layout.toPrintable());
        
        synchronized (a) {
            out.println("**** With the lock");
            out.println(layout.toPrintable());
        }
        
        out.println("**** After the lock");
        out.println(layout.toPrintable());
    }
}
