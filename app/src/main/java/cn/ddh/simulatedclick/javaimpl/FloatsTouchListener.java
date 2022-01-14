package cn.ddh.simulatedclick.javaimpl;

import android.view.MotionEvent;
import android.view.View;

import cn.ddh.simulatedclick.R;

public class FloatsTouchListener implements View.OnTouchListener {

    private int x;
    private int y;

    private OnFloatTouchListener onFloatTouchListener;

    public FloatsTouchListener(OnFloatTouchListener onFloatTouchListener){
        this.onFloatTouchListener = onFloatTouchListener;
    };

    public interface OnFloatTouchListener {
        void move(int movedX, int movedY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                onFloatTouchListener.move(movedX, movedY);
                break;
        }
        return false;
    }
}
