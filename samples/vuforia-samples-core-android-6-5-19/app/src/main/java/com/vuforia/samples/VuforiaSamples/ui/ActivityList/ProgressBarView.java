package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.util.AttributeSet;

public class ProgressBarView extends View{

    private static final int ARC_FULL_DEGREE = 300;
    private int STROKE_WIDTH;
    private int width, height;
    private float max, progress;
    private boolean draggingEnabled = true;
    private RectF circleRectF;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint thumbPaint;
    private int circleRadius;
    private int centerX, centerY;

    public ProgressBarView(Context context) {
        super(context);
        init();
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);


        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);

        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        max = 100;
        progress = 50;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();

            //计算圆弧半径和圆心点
            circleRadius = Math.min(width, height) / 5 * 2;
            STROKE_WIDTH = circleRadius / 6;
            circleRadius -= STROKE_WIDTH;

            centerX = width / 2;
            centerY = height / 2;

            //圆弧所在矩形区域
            circleRectF = new RectF();
            circleRectF.left = centerX - circleRadius;
            circleRectF.top = centerY - circleRadius;
            circleRectF.right = centerX + circleRadius;
            circleRectF.bottom = centerY + circleRadius;
        }
    }

    private Rect textBounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float start = 90 + ((360 - ARC_FULL_DEGREE) >> 1); //进度条起始点
        float sweep1 = ARC_FULL_DEGREE * (progress / max); //进度划过的角度
        float sweep2 = ARC_FULL_DEGREE - sweep1; //剩余的角度

        //绘制起始位置小圆形
        progressPaint.setColor(Color.parseColor("#218868"));
        progressPaint.setStrokeWidth(4);
        progressPaint.setStyle(Paint.Style.FILL);
        float radians = (float) (((360.0f - ARC_FULL_DEGREE) / 2) / 180 * Math.PI);
        float startX = centerX - circleRadius * (float) Math.sin(radians);
        float startY = centerY + circleRadius * (float) Math.cos(radians);
        canvas.drawCircle(startX, startY, STROKE_WIDTH / 2, progressPaint);

        //绘制进度条
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStyle(Paint.Style.STROKE);//设置空心
        canvas.drawArc(circleRectF, start, sweep1, false, progressPaint);

        //绘制进度条背景
        progressPaint.setColor(Color.parseColor("#F5F5F5"));
        canvas.drawArc(circleRectF, start + sweep1, sweep2, false, progressPaint);

        //绘制结束位置小圆形
        progressPaint.setStrokeWidth(4);
        progressPaint.setStyle(Paint.Style.FILL);
        float endX = centerX + circleRadius * (float) Math.sin(radians);
        float endY = centerY + circleRadius * (float) Math.cos(radians);
        canvas.drawCircle(endX, endY, STROKE_WIDTH / 2, progressPaint);

        //绘制进度位置
        float progressRadians = (float) (((360.0f - ARC_FULL_DEGREE) / 2 + sweep1) / 180 * Math.PI);
        float thumbX = centerX - circleRadius * (float) Math.sin(progressRadians);
        float thumbY = centerY + circleRadius * (float) Math.cos(progressRadians);
        thumbPaint.setColor(Color.parseColor("#33218868"));
        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 2.0f, thumbPaint);
        thumbPaint.setColor(Color.parseColor("#88218868"));
        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 1.4f, thumbPaint);
        thumbPaint.setColor(Color.WHITE);
        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 0.8f, thumbPaint);

        //上一行文字
        textPaint.setTextSize(circleRadius >> 1);
        String text = (int) (100 * progress) + "";
        float textLen = textPaint.measureText(text);
        //计算文字高度
        textPaint.getTextBounds("6", 0, 1, textBounds);
        float h1 = textBounds.height();
        //前面的数字水平居中，适当调整
        canvas.drawText(text, centerX - textLen / 2, centerY - 30 + h1 / 2, textPaint);
        //下一行文字
        textPaint.setTextSize(circleRadius / 6);
        text = "Total Distance/km";
        textLen = textPaint.measureText(text);
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        float h2 = textBounds.height();
        canvas.drawText(text, centerX - textLen / 2, centerY + h1 / 2 + h2, textPaint);
    }

    private boolean isDragging = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null)
            return false;
        if (!draggingEnabled) {
            return super.onTouchEvent(event);
        }
        //处理拖动事件
        float currentX = event.getX();
        float currentY = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //判断是否在进度条thumb位置
                if (checkOnArc(currentX, currentY)) {
                    float newProgress = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * max;
                    setProgressSync(newProgress);
                    isDragging = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    //判断拖动时是否移出去了
                    if (checkOnArc(currentX, currentY)) {
                        setProgressSync(calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * max);
                    } else {
                        isDragging = false;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }
        return true;
    }


    private float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //判断该点是否在弧线上（附近）
    private boolean checkOnArc(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, centerX, centerY);
        float degree = calDegreeByPosition(currentX, currentY);
        return distance > circleRadius - STROKE_WIDTH * 5 && distance < circleRadius + STROKE_WIDTH * 5
                && (degree >= -8 && degree <= ARC_FULL_DEGREE + 8);
    }

    //根据当前位置，计算出进度条已经转过的角度。
    private float calDegreeByPosition(float currentX, float currentY) {
        float a1 = (float) (Math.atan(1.0f * (centerX - currentX) / (currentY - centerY)) / Math.PI * 180);
        if (currentY < centerY) {
            a1 += 180;
        } else if (currentY > centerY && currentX > centerX) {
            a1 += 360;
        }

        return a1 - (360 - ARC_FULL_DEGREE) / 2;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setProgress(float progress) {
        final float validProgress = checkProgress(progress);
        //动画切换进度值
        new Thread(new Runnable() {
            @Override
            public void run() {
                float oldProgress = ProgressBarView.this.progress;
                for (int i = 1; i <= 100; i++) {
                    ProgressBarView.this.progress = oldProgress + (validProgress - oldProgress) * (1.0f * i / 100);
                    postInvalidate();
                    SystemClock.sleep(20);
                }
            }
        }).start();
    }

    public void setProgressSync(float progress) {
        this.progress = checkProgress(progress);
        invalidate();
    }

    //保证progress的值位于[0,max]
    private float checkProgress(float progress) {
        if (progress < 0) {
            return 0;
        }
        return progress > max ? max : progress;
    }
}
