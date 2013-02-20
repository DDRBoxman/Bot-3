package com.recursivepenguin.botcubed.ui.printpanel;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JogPanelView extends View {

    Paint arcPaint = new Paint();

    public JogPanelView(Context context) {
        super(context);
    }

    public JogPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JogPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        if (size > View.MeasureSpec.getSize(heightMeasureSpec)) {
            size = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        super.setMeasuredDimension(size, size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0x00AAAAAA);

        for (int level = numLevels; level > 0; level--) {
            for (int direction = 0; direction < 4; direction++) {
                drawControlSlice(canvas, level - 1, direction);
            }
        }
    }

    final int numLevels = 4;
    final int padding = 25;

    private void drawControlSlice(Canvas canvas, int level, int direction) {
        float angle = direction * 90 - 45;

        int size = getMeasuredHeight();
        int center = size / 2;

        int sliceSize = ((size / 2) - padding)  / numLevels;

        sliceSize = sliceSize * (level + 1);

        arcPaint.setColor(0xFFCC0000);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setStrokeWidth(12);
        arcPaint.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);

        RectF arcRect = new RectF();
        arcRect.set((center - sliceSize), (center - sliceSize), (sliceSize + center), (sliceSize + center));
        switch (direction) {
            case 3:
                arcRect.top -= padding;
                arcRect.bottom -= padding;
                break;
            case 0:
                arcRect.right += padding;
                arcRect.left += padding;
                break;
            case 1:
                arcRect.top += padding;
                arcRect.bottom += padding;
                break;
            case 2:
                arcRect.right -= padding;
                arcRect.left -= padding;
                break;
        }
        canvas.drawArc(arcRect, angle, 90, true, arcPaint);
    }
}
