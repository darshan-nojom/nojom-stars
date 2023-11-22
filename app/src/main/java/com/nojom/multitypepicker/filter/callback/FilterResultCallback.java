package com.nojom.multitypepicker.filter.callback;

import com.nojom.multitypepicker.filter.entity.BaseFile;
import com.nojom.multitypepicker.filter.entity.Directory;

import java.util.List;

public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
