package me.zhongmingmao._synchronized;

public class SyncMethod {
    private int i;
    
    public synchronized void increase() {
        i++;
    }
}