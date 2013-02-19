package com.recursivepenguin.botcubed.ui.printpanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import com.recursivepenguin.botcubed.R;

public class JogPanelView extends View {

    Bitmap buttonBitmap;

    public JogPanelView(Context context) {
        super(context);
        init();
    }

    public JogPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JogPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        buttonBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.control_xy);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        canvas.drawBitmap(buttonBitmap, 0, 0, null);
    }
}
