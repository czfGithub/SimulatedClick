package cn.ddh.simulatedclick.javaimpl;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ddh.simulatedclick.R;
import cn.ddh.simulatedclick.event.EventBase;

public class FloatingsService extends AccessibilityService implements FloatsTouchListener.OnFloatTouchListener,AddsViewDialogBuilder.OnAddViewListener,ViewsModel.OnWorkDoneListener{

    private boolean taskIng = false;

    private ViewsModel viewModel = new ViewsModel(this);
    private WindowManager windowManager = null;

    private WindowManager.LayoutParams windowLayoutParams = null;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerView rvContent;
    private AlertDialog addViewDialog;
    private View parentView;

    private List<EventBase> eventList;
    private EventsAdapter eventAdapter;

    private int currentTaskPos = -1;

    private ImageView ivFun;
    private ImageView ivStop;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatingWindow();
    }

    private void initFloatingWindow() {
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        parentView = LayoutInflater.from(this).inflate(R.layout.layout_float, null);
        windowLayoutParams = setLayoutParams(0);
        initViews();
        windowManager.addView(parentView, windowLayoutParams);
    }

    private void initViews() {
        parentView.setOnTouchListener(new FloatsTouchListener(this));
        ImageView ivAddIcon = parentView.findViewById(R.id.iv_add);
        rvContent = parentView.findViewById(R.id.rv_content);
        ivFun = parentView.findViewById(R.id.iv_fun);
        ivStop = parentView.findViewById(R.id.iv_stop);
        addViewDialog = new AddsViewDialogBuilder(this, this).create();
//        ivAddIcon.setOnTouchListener(new FloatsTouchListener(this));
//        ivFun.setOnTouchListener(new FloatsTouchListener(this));
//        rvContent.setOnTouchListener(new FloatsTouchListener(this));
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvContent.setLayoutManager(linearLayoutManager);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(this, eventList);
        rvContent.setAdapter(eventAdapter);
        changeStatusUI();

//        rvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rvContent.getLayoutParams();
//                if(layoutParams.height > 200){
//                    layoutParams.height = 200;
//                }
//                rvContent.setLayoutParams(layoutParams);
//            }
//        });

        ivAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskIng) {
                    addViewDialog.show();
                } else {
                    toast("正在执行任务，请先停止");
                }
            }
        });

        ivAddIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!taskIng) {
                    if (eventList.size() > 0) {
                        eventList.clear();
                        eventAdapter.notifyDataSetChanged();
                    }
                } else {
                    toast("正在执行任务，请先停止");
                }
                return true;
            }
        });

        ivFun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (taskIng) {
//                    stopTask();
//                    toast("所有任务已取消执行");
//                } else {
//                    if (eventList.size() > 0) {
//                        currentTaskPos = 0;
//                        startTask();
//                        taskIng = true;
//                    } else {
//                        toast("没有任务可以执行");
//                    }
//                }
                if (eventList.size() > 0) {
                    currentTaskPos = 0;
                    startTask();
                    taskIng = true;
                } else {
                    toast("没有任务可以执行");
                }
                changeStatusUI();
            }
        });

        ivStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!taskIng){
                    return true;
                }
                stopTask();
                toast("所有任务已取消执行");
                changeStatusUI();
                return true;
            }
        });
    }

    private void startTask() {
        if (currentTaskPos < eventList.size()) {
            EventBase eventBase = eventList.get(currentTaskPos);
            eventBase.setTasking();
//            eventAdapter.notifyDataSetChanged();
            eventAdapter.notifyItemChanged(currentTaskPos);
            if(currentTaskPos > linearLayoutManager.findLastVisibleItemPosition()){
                rvContent.scrollToPosition(currentTaskPos);
            }
            viewModel.toWork(eventBase);
        } else {
            allFinish();
        }
    }

    private void allFinish() {
        stopTask();
        toast("所有任务都已经结束");
    }


    private void stopTask() {
        for (EventBase item : eventList) {
            item.setTasking(false);
        }
        eventAdapter.notifyDataSetChanged();
        viewModel.cancelAllView();

        currentTaskPos = -1;
        taskIng = false;
        changeStatusUI();
    }

    private void changeStatusUI(){
//        if (taskIng){
//            ivFun.setImageResource( R.mipmap.icon_puse);
//        }else{
//            ivFun.setImageResource(R.mipmap.icon_play);
//        }
        if(taskIng){
            ivStop.setVisibility(View.VISIBLE);
            ivFun.setVisibility(View.GONE);
        }else{
            ivStop.setVisibility(View.GONE);
            ivFun.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据参数生成 LayoutParams
     *
     * @param flag flag
     * WH   View 的宽高
     * x    初始view的位置x
     * y    初始view的位置y
     * @return 对应生成的LayoutParams
     */
    private WindowManager.LayoutParams  setLayoutParams(int flag){
        WindowManager.LayoutParams layout = getFloatLayoutParam(false, true);
        layout.gravity = Gravity.START | Gravity.TOP;

        layout.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layout.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if (flag != 0) {
            layout.flags = layout.flags | flag;
        }
        layout.x = 0;
        layout.y = 100;
        return layout;
    }

    /**
     * 获取 FloatLayoutParam
     *
     * @param fullScreen 是否是全屏
     * @param touchAble  是否可触摸
     * @return 对应的layoutParam
     */
    private WindowManager.LayoutParams getFloatLayoutParam(boolean fullScreen, boolean touchAble){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            //刘海屏延伸到刘海里面
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutParams.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        ) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

//        layoutParams.packageName = getPackageName();
        layoutParams.flags =
                layoutParams.flags | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

        //Focus会占用屏幕焦点，导致游戏无声
        if (touchAble) {
            layoutParams.flags =
                    layoutParams.flags | (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        } else {
            layoutParams.flags =
                    layoutParams.flags | (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        if (fullScreen) {
            layoutParams.flags = layoutParams.flags | (WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.flags =
                    layoutParams.flags | (WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        layoutParams.format = PixelFormat.TRANSPARENT;
        return layoutParams;
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addTask(EventBase eventBase) {
        if (eventBase.getCycleNum() >= 0) {
            for(int i = 0; i < eventBase.getCycleNum(); i++){
                eventList.add(eventBase);
            }
            eventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void move(int movedX, int movedY) {
        windowLayoutParams.x = windowLayoutParams.x + movedX;
        windowLayoutParams.y = windowLayoutParams.y + movedY;
//        windowLayoutParams.let {
//            it.y = it.y + movedY;
//        }
        windowManager.updateViewLayout(parentView, windowLayoutParams);
    }

    @Override
    public void done() {
        currentTaskPos++;
        startTask();
    }

    @Override
    public void click(Point point, ViewsModel.OnSimulationResultListener onSimulationResultListener) {
        autoClickView(point, onSimulationResultListener);
    }

    @Override
    public void move(Point start, Point end, ViewsModel.OnSimulationResultListener onSimulationResultListener) {
        autoSlideView(start, end, onSimulationResultListener);
    }

    @Override
    public void back(ViewsModel.OnSimulationResultListener onSimulationResultListener) {
        autoBackView(onSimulationResultListener);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private void autoClickView(Point point, ViewsModel.OnSimulationResultListener onTouchDoneListener) {
        Path path = new Path();
        path.moveTo(point.x, point.y);
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0, 50))
                .build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                onTouchDoneListener.result(true);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                onTouchDoneListener.result(false);
            }
        },null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autoSlideView(Point start, Point end, ViewsModel.OnSimulationResultListener onTouchDoneListener) {
        Path path = new Path();
        path.moveTo(start.x, start.y);
        path.lineTo(end.x, end.y);
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0, 500))
                .build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                onTouchDoneListener.result(true);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                onTouchDoneListener.result(false);
            }
        },null);
    }

    private void autoBackView(ViewsModel.OnSimulationResultListener onSimulationResultListener){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            onSimulationResultListener.result(performGlobalAction(GLOBAL_ACTION_BACK));
        }else{
            onSimulationResultListener.result(false);
        }
    }
}
