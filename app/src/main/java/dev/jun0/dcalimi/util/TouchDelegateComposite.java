package dev.jun0.dcalimi.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TouchDelegateComposite extends TouchDelegate {

    private final List<TouchDelegate> delegates = new ArrayList<>();
    private static final Rect emptyRect = new Rect();

    public TouchDelegateComposite(View view) {
        super(emptyRect, view);
    }

    public void addDelegate(TouchDelegate delegate) {
        if (delegate != null) {
            delegates.add(delegate);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = false;
        float x = event.getX();
        float y = event.getY();
        for (TouchDelegate delegate : delegates) {
            event.setLocation(x, y);
            res = delegate.onTouchEvent(event) || res;
        }
        return res;
    }

}