package com.nojom.multitypepicker.adapter;

public interface OnSelectStateListener<T> {
    void OnSelectStateChanged(boolean state, T file);
}
