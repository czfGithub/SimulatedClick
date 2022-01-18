package cn.ddh.simulatedclick.javaimpl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ddh.simulatedclick.R;
import cn.ddh.simulatedclick.event.EventBase;

public class SimulatedClick extends Activity implements View.OnTouchListener {

    private final int REQUEST_CODE = 2001;

    private RecyclerView rvContent;
    private TextView textView;
    private LinearLayoutManager linearLayoutManager;
    private List<EventBase> eventList;
    private EventsAdapter eventAdapter;

    private int index = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvContent = findViewById(R.id.rv_content);
        textView = findViewById(R.id.tv_show);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(this, eventList);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(eventAdapter);
        rvContent.setOnTouchListener(this);
        initData();
        if(getFloat()){
            checkAccessibility();
        }else{
            startSetting();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(index >= 0 && index < eventList.size()){
                    eventList.get(index++).setTasking();
//                    Toast.makeText(SimulatedClick.this,index + ":" + linearLayoutManager.findLastVisibleItemPosition(),Toast.LENGTH_SHORT).show();
                    if(index > linearLayoutManager.findLastVisibleItemPosition()){
                        rvContent.scrollToPosition(index);
                    }
                    eventAdapter.notifyDataSetChanged();
                    handler.postDelayed(this,1000);
                }else{
                    index = 0;
                    for(int i = 0; i < eventList.size(); i++){
                        eventList.get(i).setTasking(false);
                    }
                    rvContent.scrollToPosition(index);
                    eventAdapter.notifyDataSetChanged();
                    handler.postDelayed(this,1000);
                }
            }
        },2000);
    }

    private void initData(){
        for(int i = 0; i < 30; i++){
            eventList.add(new EventBase("点击" + (i + 1),1000,false,10));
        }
        eventAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        textView.setText(event.getRawX() + "," + event.getRawY());
        return false;
    }
}
