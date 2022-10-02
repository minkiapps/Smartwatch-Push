package com.minkiapps.hos.pushdemo;

import com.minkiapps.hos.pushdemo.util.PermissionHelper;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;

import static com.minkiapps.hos.pushdemo.util.PermissionHelper.LOCATION_PERMISSIONS;

public class SplashAbility extends Ability {

    private final static int REQUEST_PERMISSION_CODE = 10001;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_splash);
        setSwipeToDismiss(true);

        if(!PermissionHelper.isLocationInBackgroundPermissionGranted(this)) {
            requestPermissionsFromUser(LOCATION_PERMISSIONS, REQUEST_PERMISSION_CODE);
        }

        findComponentById(ResourceTable.Id_t_ability_splash_app_setting)
                .setClickedListener(component -> startAbility(PermissionHelper.getAppSettingIntent(SplashAbility.this)));
    }

    private void startLocationServiceAndMainAbility() {
        startAbility(LocationService.class.getName());
        startAbility(MainAbility.class.getName());
    }

    @Override
    protected void onActive() {
        super.onActive();
        if(PermissionHelper.isLocationInBackgroundPermissionGranted(this)) {
            startLocationServiceAndMainAbility();
            terminateAbility();
        }
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
        // Match requestCode of requestPermissionsFromUser.
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if(PermissionHelper.isLocationInBackgroundPermissionGranted(this)) {
                startLocationServiceAndMainAbility();
                terminateAbility();
            }
        }
    }

    private void startAbility(final String abilityName) {
        final Intent intent = new Intent();
        final Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName(abilityName)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }
}
