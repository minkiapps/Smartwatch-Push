package com.minkiapps.hos.pushdemo.slice;

import com.huawei.hms.push.common.ApiException;
import com.huawei.hms.push.ohos.HmsInstanceId;
import com.minkiapps.hos.pushdemo.MyApplication;
import com.minkiapps.hos.pushdemo.ResourceTable;
import com.minkiapps.hos.pushdemo.util.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.app.dispatcher.task.TaskPriority;

public class MainAbilitySlice extends AbilitySlice {

    private static final String TAG = MainAbilitySlice.class.getSimpleName();

    private Text logText;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        findComponentById(ResourceTable.Id_s_ability_main).requestFocus();

        logText = (Text) findComponentById(ResourceTable.Id_t_ability_main_log);

        getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final String token = HmsInstanceId.getInstance(getAbility().getAbilityPackage(),
                        MainAbilitySlice.this).getToken(MyApplication.APP_ID, "HCM");
                printLogText(String.format("Token received: %s", token));
            } catch (ApiException e) {
                printLogText(String.format("Failed to obtain token, error code: %d error msg: %s", e.getStatusCode(), e.getStatusMessage()));
            }
        });
    }

    private void printLogText(final String text) {
        LogUtils.d(TAG, text);
        getUITaskDispatcher().asyncDispatch(() -> logText.append(text));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
