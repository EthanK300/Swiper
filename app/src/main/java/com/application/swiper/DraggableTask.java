package com.application.swiper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;

public class DraggableTask extends ConstraintLayout {
    private ViewDragHelper dragHelper;
    private View draggableView;
    private float restrict = 0.01f;
    private float dragCondition = 0.05f;
    private float velocityCondition = 0.15f;
    private float density;
    private float originalLeft;

    public DraggableTask(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
    }

    private void init() {
        originalLeft = (float)this.findViewById(R.id.container).getLeft();
        System.out.println("original left: " + originalLeft);
        dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == draggableView;
            }
            @Override
            public int clampViewPositionVertical(View child, int top, int dx) {
                return child.getTop(); // disallow vertical movement
            }
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                // restrict horizontal scrolling
                int parentWidth = getWidth();
                int viewWidth = child.getWidth();
                int minLeft = (int)(restrict * parentWidth);
                int maxLeft = (int)(((1.0f - restrict) * parentWidth) - viewWidth);
//                System.out.println("minLeft: " + minLeft + ", maxLeft: " + maxLeft);
                return Math.max(minLeft, Math.min(left, maxLeft));
            }
            @Override
            public void onViewReleased(View child, float xvel, float yvel) {
//                System.out.println("released on ml: " + child.getLeft() + ", mxl: " + (child.getLeft() + child.getWidth()));
                float draggedPercent = ((float)child.getLeft() - originalLeft) / (float)getWidth();
                if (Math.abs(draggedPercent) > dragCondition || Math.abs(xvel / density) > velocityCondition) {
                    // see which direction was swiped in
                    if(((float)child.getLeft() - originalLeft) < 0 || xvel < 0){
                        // dragged to the left
                        System.out.println("dragged left");
                    }else{
                        // dragged to the right
                        System.out.println("dragged right");
                    }
                    // reset to middle
                    dragHelper.settleCapturedViewAt((getWidth() / 2) - (child.getWidth() / 2), child.getTop());
                } else {
                    // snap back
                    dragHelper.settleCapturedViewAt((getWidth() / 2) - (child.getWidth() / 2), child.getTop());
                }
                System.out.println("xvel: " + (xvel / density) + ", direction: " + ((float)child.getLeft() - originalLeft) + ", draggedpercent: " + draggedPercent);
                invalidate();
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        draggableView = findViewById(R.id.container); // draggable child view to drag
    }
}
