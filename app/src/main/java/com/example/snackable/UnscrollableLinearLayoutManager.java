package com.example.snackable;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

public class UnscrollableLinearLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;
    public UnscrollableLinearLayoutManager(Context context, boolean isScrollEnabled) {
        super(context);
        this.isScrollEnabled = isScrollEnabled;
    }

    public UnscrollableLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public UnscrollableLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canScrollVertically() {
        //設定是否禁止滑動
        return isScrollEnabled && super.canScrollVertically();
    }
}
