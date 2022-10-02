package com.minkiapps.hos.pushdemo.util;

import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.utils.net.Uri;

public class PermissionHelper {

    public static final String PERMISSION_LOCATION = "ohos.permission.LOCATION";
    public static final String PERMISSION_LOCATION_BACKGROUND = "ohos.permission.LOCATION_IN_BACKGROUND";

    public static final String[] LOCATION_PERMISSIONS = {
            PERMISSION_LOCATION, PERMISSION_LOCATION_BACKGROUND
    };

    public static boolean isLocationPermissionGranted(final Context context) {
        return context.verifySelfPermission(PERMISSION_LOCATION) == IBundleManager.PERMISSION_GRANTED;
    }

    public static boolean isLocationInBackgroundPermissionGranted(final Context context) {
        return context.verifySelfPermission(PERMISSION_LOCATION_BACKGROUND) == IBundleManager.PERMISSION_GRANTED;
    }

    public static boolean isLocationPermissionDenied(final Context context) {
        return context.verifySelfPermission(PERMISSION_LOCATION) == IBundleManager.PERMISSION_DENIED
                 && !context.canRequestPermission(PERMISSION_LOCATION);
    }

    public static boolean needAskForLocationPermission(final Context context) {
        return context.verifySelfPermission(PERMISSION_LOCATION) == IBundleManager.PERMISSION_DENIED
                && context.canRequestPermission(PERMISSION_LOCATION);
    }

    public static Intent getAppSettingIntent(final Context context) {
        final Uri uri = Uri.parse("package:" + context.getBundleName());
        final Intent intent = new Intent();
        final Operation operation = new Intent.OperationBuilder()
                .withAction("android.settings.APPLICATION_DETAILS_SETTINGS")
                .withUri(uri)
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .build();
        intent.setOperation(operation);
        return intent;
    }
}
