package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class PolygonalDrawable extends Drawable {

    private int mViewHeight;
    private int mPosition;
    private Context context;
    private int mBarHeight;
    private int mBarWidth;

    private Path polygon = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mLeftTop;
    private int mRightTop;

    public PolygonalDrawable(Context context, int viewHeight, int position, int color, int screen_height, int screen_width, int leftTop, int rightTop) {
        this.context = context;
        mViewHeight = viewHeight;
        mBarHeight = screen_height;
        mBarWidth = screen_width;
        mPosition = position;

        mLeftTop = leftTop;
        mRightTop = rightTop;

        paint.setColor(color);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        polygon.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.translate(0,canvas.getHeight());
        canvas.scale(1,-1);
        canvas.drawPath(polygon, paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        polygon.reset();
        polygon.addPath(CreatePolygon());
        invalidateSelf();
    }

    private Path CreatePolygon() {

        polygon = new Path();

        polygon.reset();

        polygon.moveTo(0f, 0f);

        int height = mLeftTop != mBarHeight ? mLeftTop : mBarHeight;

        polygon.lineTo(0f, height);

        polygon.lineTo(mBarWidth, mBarHeight);

        polygon.lineTo(mBarWidth, 0f);

        polygon.close();

        return polygon;
    }
}
