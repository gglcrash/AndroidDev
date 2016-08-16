package com.softdesign.devintensive.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;

/**
 * Created by gglcrash on 09.08.2016.
 */
public class StatsPanelBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    public StatsPanelBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        child.setY(dependency.getY());
        layoutParams.height = (int) (dependency.getY() * 0.300+110);
        dependency.setPadding(dependency.getPaddingLeft(), layoutParams.height, dependency.getPaddingRight(), dependency.getPaddingBottom());
        return true;
    }
}
