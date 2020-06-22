package tech.michaelx.vivohelper;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class InstallerHelperService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        if (event.getPackageName().equals("com.vivo.secime.service")) {
            //vivo账号密码
            String password = (String) SharePreferencesUtils.getParam(getApplicationContext(),
                    AppConstants.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode, password);
                findAndClickView(rootNode, "确定");
            }
        } else {
            List<AccessibilityNodeInfo> appStoreInstallList = rootNode.findAccessibilityNodeInfosByText("商店安装新版本");
            if (appStoreInstallList != null && appStoreInstallList.size() > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        findAndClickView(rootNode, "继续安装旧版本");
                    }
                }).start();
            } else {
                findAndClickViews(rootNode);
            }

        }
    }

    /**
     * 自动填充密码
     */
    private void fillPassword(AccessibilityNodeInfo rootNode, String password) {
        AccessibilityNodeInfo editText = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editText == null) return;

        if (editText.getPackageName().equals("com.bbk.account")
                && editText.getClassName().equals("android.widget.EditText")) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
    }

    /**
     * 查找按钮并点击
     */
    private void findAndClickViews(AccessibilityNodeInfo rootNode) {
        if (findAndClickView(rootNode, "确定")) {
            return;
        }

        if (findAndClickView(rootNode, "继续安装")) {
            return;
        }

        if (findAndClickView(rootNode, "继续安装旧版本")) {
            return;
        }

        if (findAndClickView(rootNode, "打开")) {
            return;
        }

        if (findAndClickView(rootNode, "安装")) {
            return;
        }
    }

    private boolean findAndClickView(AccessibilityNodeInfo rootNode, String nodeText) {
        List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByText(nodeText);
        // vivo升级系统后,安装过程中会推荐多个app进行安装,所以,只有当size为1时进行点击处理,不然,会一下子安装多个推荐的app
        if (nodeInfoList != null && nodeInfoList.size() == 1) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onInterrupt() {
    }
}
