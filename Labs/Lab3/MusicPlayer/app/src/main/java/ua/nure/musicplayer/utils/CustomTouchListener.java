package ua.nure.musicplayer.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import ua.nure.musicplayer.models.OnItemClickListener;

public class CustomTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector _gestureDetector;
    private OnItemClickListener _clickListener;

    public CustomTouchListener(Context context, final OnItemClickListener clickListener) {
        this._clickListener = clickListener;
        _gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && _clickListener != null && _gestureDetector.onTouchEvent(e)) {
            _clickListener.onClick(child, recyclerView.getChildLayoutPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
