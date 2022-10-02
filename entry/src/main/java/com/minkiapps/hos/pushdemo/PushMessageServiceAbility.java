package com.minkiapps.hos.pushdemo;

import com.huawei.hms.push.ohos.HmsMessageService;
import com.huawei.hms.push.ohos.ZBaseException;
import com.huawei.hms.push.ohos.ZRemoteMessage;
import com.minkiapps.hos.pushdemo.util.LogUtils;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.event.intentagent.IntentAgent;
import ohos.event.intentagent.IntentAgentConstant;
import ohos.event.intentagent.IntentAgentHelper;
import ohos.event.intentagent.IntentAgentInfo;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class PushMessageServiceAbility extends HmsMessageService {

    private static final String TAG = PushMessageServiceAbility.class.getSimpleName();

    /**
     *
     * in case of watch is connected to phone, push message won't be received
     * Check for following log: I PushLog110105309:  [ReceiverDispatcher-240]stop push apk service, reason is 2, [1:no token, 2:harmon watch bluetooth connect to phone]
     */

    @Override
    public void onMessageReceived(ZRemoteMessage message) {
        LogUtils.d(TAG, String.format("OnMessageReceived,  data: %s", message.getData()));

        try {
            final NotificationSlot slot = NotificationHelper.getNotificationSlot(MyApplication.PUSH_SLOT_ID);
            final int notificationId = 1;
            final NotificationRequest request = new NotificationRequest(notificationId);
            request.setSlotId(slot.getId());

            final Intent intent = new Intent();
            // Set the bundleName and abilityName fields of the ability to start in an Operation object.
            // Add the Operation object to an Intent.
            final Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName(getBundleName())
                    .withAbilityName(MainAbility.class.getName())
                    .build();
            intent.setOperation(operation);
            final List<Intent> intentList = new ArrayList<>();
            intentList.add(intent);

            // Define the request code.
            final int requestCode = 200;

            // Set flags.
            final List<IntentAgentConstant.Flags> flags = new ArrayList<>();
            flags.add(IntentAgentConstant.Flags.UPDATE_PRESENT_FLAG);
            // Start an ability with a UI, that is, a Page ability.
            final IntentAgentInfo paramsInfo = new IntentAgentInfo(requestCode, IntentAgentConstant.OperationType.START_ABILITY, flags, intentList, null);

            // Obtain an IntentAgent instance.
            final IntentAgent agent = IntentAgentHelper.getIntentAgent(this, paramsInfo);

            final String title = message.getDataOfMap().get("title");
            final String text = message.getDataOfMap().get("message");

            final NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
            content.setTitle(title)
                    .setText(text);
            NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
            request.setContent(notificationContent); // Set the content type of the notification.
            request.setIntentAgent(agent);

            NotificationHelper.publishNotification(request);
        } catch (RemoteException e) {
            LogUtils.e(TAG, String.format("Exception occurred during publishNotification invocation, error: %s", e.getMessage()), e);
        }
    }

    @Override
    public void onNewToken(String token) {
        LogUtils.d(TAG, String.format("On new token,  data: %s", token));
    }

    @Override
    public void onTokenError(Exception exception) {
        if (exception instanceof ZBaseException) {
            LogUtils.d(TAG, String.format("On token error,  code: %d", ((ZBaseException) exception).getErrorCode()));
        }
    }
}
