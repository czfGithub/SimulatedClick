package cn.ddh.simulatedclick.javaimpl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import cn.ddh.simulatedclick.R;
import cn.ddh.simulatedclick.event.BackEvent;
import cn.ddh.simulatedclick.event.ClickEvent;
import cn.ddh.simulatedclick.event.EventBase;
import cn.ddh.simulatedclick.event.SlideEvent;

public class AddsViewDialogBuilder {

    private Context context;
    private OnAddViewListener onAddViewListener;

    private Spinner rootViewGroup;
    private LinearLayout llDelay;
    private LinearLayout llCycle;
    private EditText etDelay;
    private EditText etCycle;
    private LinearLayout llPoint1;
    private EditText etPoint1x;
    private EditText etPoint1y;
    private LinearLayout llPoint2;
    private EditText etPoint2x;
    private EditText etPoint2y;

    private TextView tvCancel;
    private TextView tvConfirm;


    private AlertDialog.Builder dialogBuilder;

    private AlertDialog dialog;

    private int selectPos = -1;

    public AddsViewDialogBuilder(Context context, OnAddViewListener onAddViewListener){
        this.context = context;
        this.onAddViewListener = onAddViewListener;
        dialogBuilder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog);
        dialogBuilder.setMessage("添加任务");
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null);
        initViews(inflate);
        dialogBuilder.setView(inflate);
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectPos = -1;
                cleanUI();
            }
        });
    }

    public interface OnAddViewListener {
        void toast(String msg);
        void addTask(EventBase eventBase);
    }

    private void cleanUI() {
        llDelay.setVisibility(View.GONE);
        llPoint1.setVisibility(View.GONE);
        llPoint2.setVisibility(View.GONE);
        llCycle.setVisibility(View.GONE);
        etDelay.setText("1000");
        etCycle.setText("1");
        etPoint1x.setText("");
        etPoint2x.setText("");
        etPoint1y.setText("");
        etPoint2y.setText("");
    }

    private void initViews(View inflate) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        rootViewGroup = inflate.findViewById(R.id.sp_type);

        llDelay = inflate.findViewById(R.id.ll_delay);
        llCycle = inflate.findViewById(R.id.ll_cycle);
        etCycle = inflate.findViewById(R.id.et_cycle);
        etDelay = inflate.findViewById(R.id.et_delay);
        llPoint1 = inflate.findViewById(R.id.ll_point1);
        etPoint1x = inflate.findViewById(R.id.et_point1x);
        etPoint1y = inflate.findViewById(R.id.et_point1y);
        llPoint2 = inflate.findViewById(R.id.ll_point2);
        etPoint2x = inflate.findViewById(R.id.et_point2x);
        etPoint2y = inflate.findViewById(R.id.et_point2y);
        tvConfirm = inflate.findViewById(R.id.tv_confirm);
        tvCancel = inflate.findViewById(R.id.tv_cancel);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickConfirm();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rootViewGroup.setLayoutParams(new LinearLayout.LayoutParams(
                displayMetrics.widthPixels - 200,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));


        rootViewGroup.setAdapter(new ArrayAdapter(
                context,
                R.layout.item_spinner,
                Arrays.asList("点击", "滑动", "返回")
        ));
        rootViewGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectPos = position;
                changeUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void clickConfirm() {
        if (selectPos == -1) {
            onAddViewListener.toast("请确认选择的类型");
        } else {
            String delayStr = etDelay.getText().toString().trim();
            if (TextUtils.isEmpty(delayStr)) {
                onAddViewListener.toast("请输入延迟时间");
                return;
            }
            int delayInt = 0;
            try {
                delayInt = Integer.parseInt(delayStr);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (delayInt <= 0) {
                onAddViewListener.toast("延迟时间不得小于0");
                return;
            };
            String cycleNum = etCycle.getText().toString().trim();
            if (TextUtils.isEmpty(cycleNum)) {
                onAddViewListener.toast("请输入循环次数");
                return;
            }
            int cycleInt = 0;
            try {
                cycleInt = Integer.parseInt(cycleNum);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (cycleInt <= 0) {
                onAddViewListener.toast("循环次数不得小于0");
                return;
            }
            if (selectPos == 0) {
                String sPoint1x = etPoint1x.getText().toString().trim();
                String sPoint1y = etPoint1y.getText().toString().trim();
                if (TextUtils.isEmpty(sPoint1x) || TextUtils.isEmpty(sPoint1y)) {
                    onAddViewListener.toast("点击坐标未输入");
                    return;
                }
                onAddViewListener.addTask(
                        new ClickEvent(
                                delayInt,
                                new Point(Integer.parseInt(sPoint1x), Integer.parseInt(sPoint1y)), cycleInt
                        )
                );
                dialog.dismiss();
            } else if (selectPos == 1) {
                String sPoint1x = etPoint1x.getText().toString().trim();
                String sPoint1y = etPoint1y.getText().toString().trim();

                String sPoint2x = etPoint2x.getText().toString().trim();
                String sPoint2y = etPoint2y.getText().toString().trim();

                if (TextUtils.isEmpty(sPoint1x) || TextUtils.isEmpty(sPoint1y) || TextUtils.isEmpty(sPoint2x) || TextUtils.isEmpty(sPoint2y)) {
                    onAddViewListener.toast("点击坐标未输入");
                    return;
                }

                onAddViewListener.addTask(
                        new SlideEvent(
                                delayInt,
                                new Point(Integer.parseInt(sPoint1x), Integer.parseInt(sPoint1y)),
                                new Point(Integer.parseInt(sPoint2x),Integer.parseInt(sPoint2y)), cycleInt
                        )
                );
                dialog.dismiss();
            } else {
                onAddViewListener.addTask(
                        new BackEvent(delayInt, cycleInt)
                );
                dialog.dismiss();
            }
        }
    }

    private void changeUI() {
        cleanUI();
        if (selectPos != -1) {
            llDelay.setVisibility(View.VISIBLE);
            llCycle.setVisibility(View.VISIBLE);

            switch (selectPos) {
                case 0: //点击
                    llPoint1.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    llPoint1.setVisibility(View.VISIBLE);
                    llPoint2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    break;
            }
        }
    }

    public AlertDialog create(){
        dialog = dialogBuilder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            window.setType( WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else{
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                rootViewGroup.setSelection(0);
            }
        });
        return dialog;
    }

}
