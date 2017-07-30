package me.zhongmingmao.unsafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import sun.misc.Unsafe;

import java.util.stream.IntStream;

// JVM Args : -Djol.tryWithSudo=true
public class UnsafeArray {
    @Data
    @AllArgsConstructor
    static class User {
        private String name;
        private int age;
    }
    
    public static void main(String[] args) {
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        
        // 通过JOL打印虚拟机信息
        /*
        # Running 64-bit HotSpot VM.
        # Using compressed oop with 3-bit shift.
        # Using compressed klass with 3-bit shift.
        # Objects are 8 bytes aligned.
        # Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
        # Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
         */
        System.out.println(VM.current().details());
        
        // 实例化User[]
        User[] users = new User[3];
        IntStream.range(0, users.length).forEach(i ->
                users[i] = new User(String.format("zhongmingmao_%s", i), i));
        
        // 通过JOL打印users的内存布局
        /*
        [Lme.zhongmingmao.unsafe.UnsafeArray$User; object internals:
         OFFSET  SIZE                                      TYPE DESCRIPTION                               VALUE
              0     4                                           (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
              4     4                                           (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
              8     4                                           (object header)                           24 f1 00 f8 (00100100 11110001 00000000 11111000) (-134155996)
             12     4                                           (object header)                           03 00 00 00 (00000011 00000000 00000000 00000000) (3)
             16    12   me.zhongmingmao.unsafe.UnsafeArray$User UnsafeArray$User;.<elements>              N/A
             28     4                                           (loss due to the next object alignment)
        Instance size: 32 bytes
        Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        System.out.println(ClassLayout.parseInstance(users).toPrintable());
        
        // users[0]的偏移
        int baseOffset = unsafe.arrayBaseOffset(User[].class);
        System.out.println(baseOffset); // 16
        int indexScale = unsafe.arrayIndexScale(User[].class);
        System.out.println(indexScale); // 4
        
        // users[1]
        Object object = unsafe.getObject(users, baseOffset + indexScale + 0L);
        System.out.println(object); // UnsafeArray.User(name=zhongmingmao_1, age=1)
    }
}