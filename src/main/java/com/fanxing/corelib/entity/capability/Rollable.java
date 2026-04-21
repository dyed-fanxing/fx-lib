package com.fanxing.corelib.entity.capability;

public interface Rollable {
    float getRoll();
    default float getRollO(){
        return 0;
    }
}
