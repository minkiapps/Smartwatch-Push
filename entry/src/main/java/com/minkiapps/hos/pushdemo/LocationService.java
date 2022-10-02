package com.minkiapps.hos.pushdemo;

import com.huawei.hms.location.harmony.*;
import com.minkiapps.hos.pushdemo.util.LogUtils;
import com.minkiapps.hos.pushdemo.util.ResUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.LocalRemoteObject;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.event.intentagent.IntentAgent;
import ohos.event.intentagent.IntentAgentConstant;
import ohos.event.intentagent.IntentAgentHelper;
import ohos.event.intentagent.IntentAgentInfo;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

import java.util.ArrayList;
import java.util.List;

import static com.minkiapps.hos.pushdemo.MyApplication.WEATHER_WARNING_SLOT_ID;

public class LocationService extends Ability {

    private static final String TAG = LocationService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 1001;

    private static final String ONGOING_TAG = "Ongoing_Overview";

    private FusedLocationClient fusedLocationClient;
    private final LocationRemoteObject locationRemoteObject = new LocationRemoteObject();

    public class LocationRemoteObject extends LocalRemoteObject {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    private LocationCallback callingAbilityLocationCallBack = null;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            LogUtils.d(TAG, String.format("Location result: %s", locationResult.getLastHWLocation()));
            if(callingAbilityLocationCallBack != null) {
                callingAbilityLocationCallBack.onLocationResult(locationResult);
            }
        }
    };

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        LogUtils.d(TAG, "On LocationService start");

        createNotification("Weather Warning on");

        fusedLocationClient = new FusedLocationClient(this);

        doLocationRequest();
    }

    public void setCallingAbilityLocationCallBack(final LocationCallback callingAbilityLocationCallBack) {
        this.callingAbilityLocationCallBack = callingAbilityLocationCallBack;
    }

    public void createNotification(String text) {
        final NotificationRequest request = new NotificationRequest(this, NOTIFICATION_ID);
        request.setSlotId(WEATHER_WARNING_SLOT_ID);
        request.setLittleIcon(ResUtil.getPixelMap(this, ResourceTable.Media_icon));

        //notification content
        final NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setText(text);
        final NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);

        //notification intent
        final Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName(MainAbility.class.getName())
                .build();
        intent.setOperation(operation);
        List<Intent> intentList = new ArrayList<>();
        intentList.add(intent);
        IntentAgentInfo paramsInfo = new IntentAgentInfo(200,
                IntentAgentConstant.OperationType.START_ABILITY, IntentAgentConstant.Flags.UPDATE_PRESENT_FLAG, intentList, null);
        IntentAgent intentAgent = IntentAgentHelper.getIntentAgent(this, paramsInfo);
        request.setIntentAgent(intentAgent);

        try {
            NotificationHelper.publishNotification(ONGOING_TAG, request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        keepBackgroundRunning(NOTIFICATION_ID, request);
    }

    private void doLocationRequest() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setNeedAddress(true);
        locationRequest.setMaxWaitTime(10000);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback).addOnCompleteListener(harmonyTask -> {
            if (harmonyTask.isSuccessful()) {
                LogUtils.d(TAG, "Successful requested location.");
            } else {
                LogUtils.e(TAG, String.format("Failed to request location update: %s", harmonyTask.getException().getMessage()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "On Location Service Stop");
        fusedLocationClient.removeLocationUpdates(locationCallback).addOnCompleteListener(harmonyTask -> {
            if(harmonyTask.isSuccessful()) {
                LogUtils.d(TAG, "Successful remove location updates.");
            } else {
                LogUtils.e(TAG, String.format("Failed to remove location update: %s", harmonyTask.getException().getMessage()));
            }
        });

        cancelBackgroundRunning();
        try {
            NotificationHelper.cancelNotification(ONGOING_TAG, NOTIFICATION_ID);
        } catch (RemoteException exception) {
            LogUtils.e(TAG, "A remote exception occurred when publish ongoing card notification.");
        }
    }

    @Override
    protected IRemoteObject onConnect(Intent intent) {
        return locationRemoteObject;
    }
}
