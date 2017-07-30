package me.zhongmingmao.unsafe;

import sun.misc.Unsafe;

public class UnsafeMemory {
    public static void main(String[] args) {
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        System.out.println(unsafe.pageSize()); // 4096
        
        long address = unsafe.allocateMemory(1024);
        System.out.println(address);
        unsafe.putLong(address, 1024);
        System.out.println(unsafe.getLong(address)); // 1024
        unsafe.freeMemory(address);
    }
}