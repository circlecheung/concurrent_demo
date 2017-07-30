package me.zhongmingmao.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {
    @Data
    @AllArgsConstructor
    static class User {
        private String name;
        private String location;
    }
    
    public static void main(String[] args) {
        AtomicReference<User> atomicReference = new AtomicReference<>();
        User expectUser = new User("zhongmingmao", "ZhongShan");
        atomicReference.set(expectUser);
        User updateUser = new User("zhongmingwu", "GuangZhou");
        
        expectUser.setLocation("HangZhou"); // 修改实例域不影响结果
        boolean casOK = atomicReference.compareAndSet(expectUser, updateUser);
        System.out.println(casOK); // true
        System.out.println(atomicReference.get()); // AtomicReferenceDemo.User(name=zhongmingwu, location=GuangZhou)
    }
}