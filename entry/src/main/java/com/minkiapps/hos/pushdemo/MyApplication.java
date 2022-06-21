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
    public static final String PUSH_SLOT_1 = "push_slot_1";

    @Override
    public void onInitialize() {
        super.onInitialize();

        final NotificationSlot slot = new NotificationSlot(PUSH_SLOT_1, "slot_default", NotificationSlot.LEVEL_HIGH); // Create a NotificationSlot object.
        slot.setDescription("Weather Warning Notification");
        slot.setEnableVibration(true); // Enable vibration when a notification is received.
        slot.setEnableLight(true); // Enable the notification light.
        slot.setLedLightColor(Color.RED.getValue());// Set the color of the notification light.

        try {
            NotificationHelper.addNotificationSlot(slot);
        } catch (RemoteException ex) {
            LogUtils.e(TAG, String.format("Exception occurred during addNotificationSlot invocation, error: %s", ex.getMessage()), ex);
        }
    }
}
