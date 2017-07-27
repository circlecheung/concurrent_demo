package me.zhongmingmao._synchronized;

public class SyncBlock {
    private int i;
    
    public void increase() {
        synchronized (this) {
            i++;
        }
    }
}