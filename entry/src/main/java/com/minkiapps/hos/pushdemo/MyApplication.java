package com.minkiapps.hos.pushdemo;

import com.minkiapps.hos.pushdemo.util.LogUtils;
import ohos.aafwk.ability.AbilityPackage;
import ohos.agp.utils.Color;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;

public class MyApplication extends AbilityPackage {

    private static final String TAG = MyApplication.class.getSimpleName();

    public static final String APP_ID = "905047232397454016";

    public static final String PUSH_SLOT_ID = "PUSH_SLOT_ID";
    public static final String WEATHER_WARNING_SLOT_ID = "WEATHER_WARNING_SLOT_ID";

    @Override
    public void onInitialize() {
        super.onInitialize();

        initSlotIds();
    }

    private void initSlotIds() {
        final NotificationSlot pushSlot = new NotificationSlot(PUSH_SLOT_ID, "slot_default", NotificationSlot.LEVEL_HIGH); // Create a NotificationSlot object.
        pushSlot.setDescription("Weather Warning Notification");
        pushSlot.setEnableVibration(true); // Enable vibration when a notification is received.
        pushSlot.setEnableLight(true); // Enable the notification light.
        pushSlot.setLedLightColor(Color.RED.getValue());// Set the color of the notification light.
        try {
            NotificationHelper.addNotificationSlot(pushSlot);
        } catch (RemoteException ex) {
            LogUtils.e(TAG, String.format("Exception occurred during addNotificationSlot invocation, error: %s", ex.getMessage()), ex);
        }

        final NotificationSlot onGoingSlot = new NotificationSlot(WEATHER_WARNING_SLOT_ID, "weather_card", NotificationSlot.LEVEL_MIN);
        onGoingSlot.setDescription("Weather Warning On");
        try {
            NotificationHelper.addNotificationSlot(onGoingSlot);
        } catch (RemoteException ex) {
            LogUtils.e(TAG, String.format("Add ongoing card pushSlot exception, error: %s", ex.getMessage()));
        }
    }
}
