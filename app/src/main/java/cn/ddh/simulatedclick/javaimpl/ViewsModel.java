package cn.ddh.simulatedclick.javaimpl;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import cn.ddh.simulatedclick.event.BackEvent;
import cn.ddh.simulatedclick.event.ClickEvent;
import cn.ddh.simulatedclick.event.EventBase;
import cn.ddh.simulatedclick.event.SlideEvent;

public class ViewsModel {

    private String keyCode = "data_event";

    private OnWorkDoneListener listener;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            toDealMsg(msg);
        }
    };

    public ViewsModel(OnWorkDoneListener listener){
        this.listener = listener;
    }

    private void toDealMsg(Message msg){
        EventBase eventBase = (EventBase) msg.getData().getSerializable(keyCode);
        if(eventBase instanceof ClickEvent){
            ClickEvent test = (ClickEvent) eventBase;
            Point point = test.getPoint();
            listener.click(point, new OnSimulationResultListener(){

                @Override
                public void result(Boolean result) {
                    LOGPRINT("点击", result);
                }
            });
        }else if(eventBase instanceof SlideEvent){
            SlideEvent test = (SlideEvent) eventBase;
            Point point1 = test.getStartPoint();
            Point point2 = test.getEndPoint();
            listener.move(point1, point2, new OnSimulationResultListener() {
                @Override
                public void result(Boolean result) {
                    LOGPRINT("滑动", result);
                }
            });
        }else if(eventBase instanceof BackEvent){
            listener.back(new OnSimulationResultListener() {
                @Override
                public void result(Boolean result) {
                    LOGPRINT("返回", result);
                }
            });
        }
        listener.done();
    }

    public interface OnWorkDoneListener {
        void done();
        void click(Point point, OnSimulationResultListener onSimulationResultListener);
        void move(Point start, Point end, OnSimulationResultListener onSimulationResultListener);
        void back(OnSimulationResultListener onSimulationResultListener);
    }

    public interface OnSimulationResultListener{
        void result(Boolean result);
    }

    public void toWork(EventBase eventBase){
        Message obtainMessage = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putSerializable(keyCode, eventBase);
        obtainMessage.setData(bundle);
        obtainMessage.what = 100;
        handler.sendMessageDelayed(obtainMessage, eventBase.getDelay());
    }

    public void cancelAllView() {
        handler.removeMessages(100);
    }

    private void LOGPRINT(String s, Boolean result) {
        Log.d("test_hjd", "模拟" + s +",结果:" + result);
    }

}
