package com.fanxing.lib.entity.capability;

public interface Rollable {
    float getRoll();
    default float getRollO(){
        return 0;
    }
}
