package com.estimote.proximity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class LocationView extends View {
    private Paint mPaint;
    private float mX, mY;

    public LocationView(Context context, float x, float y, int color) {
        super(context);
        mX = x;
        mY = y;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mX, mY, 10, mPaint);
    }
}


