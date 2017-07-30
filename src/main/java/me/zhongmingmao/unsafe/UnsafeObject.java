package me.zhongmingmao.unsafe;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.openjdk.jol.info.ClassLayout;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

// JVM Args : -Djol.tryWithSudo=true
public class UnsafeObject {
    @AllArgsConstructor
    @ToString(of = {"name", "age", "location"})
    static class User {
        private String name;
        private int age;
        private static String location = "ZhongShan";
    }
    
    public static void main(String[] args) throws InstantiationException, NoSuchFieldException {
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        
        //通过allocateInstance直接创建对象，但未运行任何构造函数
        User user = (User) unsafe.allocateInstance(User.class);
        System.out.println(user); // UnsafeObject.User(name=null, age=0, location=ZhongShan)
    
        // 通过JOL打印对象内存布局
        /*
        me.zhongmingmao.unsafe.UnsafeObject$User object internals:
         OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
              0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
              4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
              8     4                    (object header)                           81 c1 00 f8 (10000001 11000001 00000000 11111000) (-134168191)
             12     4                int User.age                                  0
             16     4   java.lang.String User.name                                 null
             20     4                    (loss due to the next object alignment)
        Instance size: 24 bytes
        Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        System.out.println(ClassLayout.parseInstance(user).toPrintable());
        
        // Class && Field
        Class<? extends User> userClass = user.getClass();
        Field name = userClass.getDeclaredField("name");
        Field age = userClass.getDeclaredField("age");
        Field location = userClass.getDeclaredField("location");
        
        // 获取实例域name和age在对象内存中的偏移量并设置值
        System.out.println(unsafe.objectFieldOffset(name)); // 16
        unsafe.putObject(user, unsafe.objectFieldOffset(name), "zhongmingmao");
        System.out.println(unsafe.objectFieldOffset(age)); // 12
        unsafe.putInt(user, unsafe.objectFieldOffset(age), 99);
        System.out.println(user); // UnsafeObject.User(name=zhongmingmao, age=99, location=ZhongShan)
        
        // 获取定义location字段的类
        Object staticFieldBase = unsafe.staticFieldBase(location);
        System.out.println(staticFieldBase); // class me.zhongmingmao.unsafe.UnsafeObject$User
        
        // 获取static变量location的偏移量
        long staticFieldOffset = unsafe.staticFieldOffset(location);
        // 获取static变量location的值
        System.out.println(unsafe.getObject(staticFieldBase, staticFieldOffset)); // ZhongShan
        // 设置static变量location的值
        unsafe.putObject(staticFieldBase, staticFieldOffset, "GuangZhou");
        System.out.println(user); // UnsafeObject.User(name=zhongmingmao, age=99, location=GuangZhou)
    }
}