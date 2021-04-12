package com.pastoreli.whatsapp.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {

    public static boolean validatePermission (String[] permissions, Activity activity, int requestCode) {
        // 23 is marshmallow
        if(Build.VERSION.SDK_INT > 23) {
            List<String> permissionList = new ArrayList<>();

            for( String permission : permissions) {
                Boolean hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if( !hasPermission )
                    permissionList.add(permission);
            }

            if( permissionList.isEmpty() )
                return true;

            String[] newPermissions = new String[ permissionList.size() ];
            permissionList.toArray( newPermissions );

            ActivityCompat.requestPermissions( activity, newPermissions, requestCode );
        }

        return true;
    }

}
