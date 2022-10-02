package com.minkiapps.hos.pushdemo.slice;

import com.huawei.hms.location.harmony.LocationCallback;
import com.huawei.hms.location.harmony.LocationResult;
import com.huawei.hms.push.common.ApiException;
import com.huawei.hms.push.ohos.HmsInstanceId;
import com.minkiapps.hos.pushdemo.LocationService;
import com.minkiapps.hos.pushdemo.LocationService.LocationRemoteObject;
import com.minkiapps.hos.pushdemo.MyApplication;
import com.minkiapps.hos.pushdemo.ResourceTable;
import com.minkiapps.hos.pushdemo.util.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Text;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.bundle.ElementName;
import ohos.rpc.IRemoteObject;

public class MainAbilitySlice extends AbilitySlice {

    private static final String TAG = MainAbilitySlice.class.getSimpleName();

    private LocationRemoteObject remoteObject;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            printText(locationText, locationResult.getLastHWLocation().toString());
        }
    };

    private final IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int resultCode) {
            MainAbilitySlice.this.remoteObject = (LocationRemoteObject)iRemoteObject;
            remoteObject.getService().setCallingAbilityLocationCallBack(locationCallback);
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
            remoteObject.getService().setCallingAbilityLocationCallBack(null);
        }
    };

    private Text pushText;
    private Text locationText;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        findComponentById(ResourceTable.Id_s_ability_main).requestFocus();

        pushText = (Text) findComponentById(ResourceTable.Id_t_ability_main_push);
        locationText = (Text) findComponentById(ResourceTable.Id_t_ability_main_location);

        getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final String token = HmsInstanceId.getInstance(getAbility().getAbilityPackage(),
                        MainAbilitySlice.this).getToken(MyApplication.APP_ID, "HCM");
                printText(pushText, String.format("Token received: %s", token));
            } catch (ApiException e) {
                printText(pushText, String.format("Failed to obtain token, error code: %d error msg: %s", e.getStatusCode(), e.getStatusMessage()));
            }
        });

        connectService();
    }

    private void connectService() {
        LogUtils.d(TAG, "Connect to Location Service");
        Intent intent = new Intent();
        final Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName(LocationService.class.getName())
                .build();
        intent.setOperation(operation);
        connectAbility(intent, connection);
    }

    private void printText(final Text component, final String text) {
        LogUtils.d(TAG, text);
        getUITaskDispatcher().asyncDispatch(() -> component.setText(text));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "Disconnect from Location Service");
        remoteObject.getService().setCallingAbilityLocationCallBack(null);
        disconnectAbility(connection);
    }
}
