package com.minkiapps.hos.pushdemo;

import com.minkiapps.hos.pushdemo.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainAbility extends Ability {

    private static final String OHOS_PERMISSION_LOCATION = "ohos.permission.LOCATION";
    private static final String OHOS_PERMISSION_LOCATION_IN_BACKGROUND = "ohos.permission.LOCATION_IN_BACKGROUND";

    private final static int REQUEST_PERMISSION_CODE = 10001;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        setSwipeToDismiss(true);

        final String[] permission = {
                OHOS_PERMISSION_LOCATION,
                OHOS_PERMISSION_LOCATION_IN_BACKGROUND
        };
        final List<String> permissionList = new ArrayList<>();
        for (String s : permission) {
            if (verifySelfPermission(s) != IBundleManager.PERMISSION_GRANTED && canRequestPermission(s)) {
                permissionList.add(s);
            }
        }
        if (permissionList.size() > 0) {
            requestPermissionsFromUser(permissionList.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }

        if(verifySelfPermission(OHOS_PERMISSION_LOCATION) == IBundleManager.PERMISSION_GRANTED) {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
        // Match requestCode of requestPermissionsFromUser.
        if (requestCode == REQUEST_PERMISSION_CODE) {
            final boolean allGranted = Arrays.stream(grantResults).allMatch(i -> i == 0);
            if(!allGranted) {
                new ToastDialog(getContext())
                        .setAlignment(LayoutAlignment.CENTER)
                        .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                        .setText("Not all permission are granted!")
                        .show();
            }

            if(verifySelfPermission(OHOS_PERMISSION_LOCATION) == IBundleManager.PERMISSION_GRANTED) {
                startLocationService();
            }
        }
    }

    private void startLocationService() {
        final Intent intent = new Intent();
        final Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName(LocationService.class.getName())
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }
}
