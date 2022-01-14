package cn.ddh.simulatedclick.javaimpl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import cn.ddh.simulatedclick.R;

public class SimulatedClick extends Activity {

    private final int REQUEST_CODE = 2001;

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_show);
        if(getFloat()){
            checkAccessibility();
        }else{
            startSetting();
        }
    }

    //判断自定义辅助功能服务是否开启
    public boolean isAccessibilitySettingsOn(Context context, String className) {
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices =
                    activityManager.getRunningServices(100);// 获取正在运行的服务列表
            if (runningServices.size() < 0) {
                return false;
            }
            for (int i = 0; i < runningServices.size(); i++) {
                ComponentName service = runningServices.get(i).service;
                if (service.getClassName().equals(className)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private void checkAccessibility() {
        if(!isAccessibilitySettingsOn(this,FloatingsService.class.getName())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
        startService(new Intent(this, FloatingsService.class));
    }

    private boolean getFloat(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this);
            return true;
        } else {
            Toast.makeText(this, "版本过低，不支持此功能", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void startSetting() {
        try {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), REQUEST_CODE);
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (getFloat()) {
                checkAccessibility();
            } else {
                Toast.makeText(this, "悬浮窗权限开启失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        textView.setText(event.getRawX() + "," + event.getRawY());
        return super.onTouchEvent(event);
    }
}
