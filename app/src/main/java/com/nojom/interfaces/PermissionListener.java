package com.nojom.interfaces;

import com.karumi.dexter.MultiplePermissionsReport;

public interface PermissionListener {

    void onPermissionGranted(MultiplePermissionsReport report);
}
