package com.datnht.android_extensions.extensions

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

fun requestLocationBackgroundPermission(
    context: Context,
    actionWhenGranted: () -> Unit,
    requestAllPermission: () -> Unit,
    requestOnlyBackGroundPermission: () -> Unit) {
    val permissionLocation =
        hasPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION) || hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    if (permissionLocation) {
        val bgLocation = hasPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (bgLocation) {
            // case nay la all quyen dc cap
            actionWhenGranted.invoke()
        } else {
            AlertDialog.Builder(context)
                .setTitle("Resquest Per Background Location")
                .setMessage("App cần cấp quyền location background để chạy feature")
                .setPositiveButton(
                    "ok"
                ) { _, i ->
                    //Prompt the user once explanation has been shown
                    requestOnlyBackGroundPermission.invoke()
                }
                .create()
                .show()
        }
    } else {
        // case nay la chua cap quyen gi, phai request het
        requestAllPermission.invoke()
    }
}