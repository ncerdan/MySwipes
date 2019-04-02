package com.nickcerdan.myswipes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class CardSwipeAnimator {

    private View swipeButton;

    CardSwipeAnimator(View swipeButton) {
        this.swipeButton = swipeButton;
    }

    //move swipe view to starting position smoothly
    void moveToStart(boolean atEnd, Display display) {
        Point dim = new Point();
        display.getSize(dim);
        float screen_width = (float) dim.x;
        float button_width = swipeButton.getWidth();

        if (atEnd) {
            exitRight(screen_width, button_width);
        } else {
            returnMiddle();
        }
    }

    //handles animation of leaving right end of screen
    private void exitRight(float screen_width, final float button_width) {
        ValueAnimator va_exit_right = ValueAnimator.ofFloat(swipeButton.getX(), screen_width);
        va_exit_right.setDuration(150);
        va_exit_right.setInterpolator(new LinearInterpolator());
        va_exit_right.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                swipeButton.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        va_exit_right.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                enterLeft(button_width);
            }
        });
        va_exit_right.setRepeatCount(0);
        va_exit_right.start();
    }

    //handles animation of entering left end of screen
    private void enterLeft(float button_width) {
        ValueAnimator va_enter_left = ValueAnimator.ofFloat((button_width*-1), 0);
        va_enter_left.setDuration(250);
        va_enter_left.setInterpolator(new OvershootInterpolator());
        va_enter_left.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                swipeButton.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        va_enter_left.setRepeatCount(0);
        va_enter_left.start();
    }

    //handles animation of return to middle if card isn't swiped to end of screen
    private void returnMiddle() {
        ValueAnimator va_return_middle = ValueAnimator.ofFloat(swipeButton.getX(), 0);
        va_return_middle.setDuration(300);
        va_return_middle.setInterpolator(new OvershootInterpolator());
        va_return_middle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                swipeButton.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        va_return_middle.setRepeatCount(0);
        va_return_middle.start();
    }
}
