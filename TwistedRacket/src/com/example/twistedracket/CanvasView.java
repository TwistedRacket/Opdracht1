package com.example.twistedracket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class CanvasView extends View {

	private Context context;
	private Paint paint;
	private int shape = -1;

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		Paint canvasPaint = new Paint();
		canvasPaint.setStyle(Paint.Style.FILL);
		canvasPaint.setColor(Color.WHITE);

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.parseColor("#DA001A"));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (shape) {
		case Constants.SHAPE_RECT:
			Bitmap b2 = BitmapFactory.decodeResource(getResources(),
					R.drawable.tennisracketsnaart);
			canvas.drawBitmap(b2, 0, 0, paint);
			canvas.drawRect(50, 50, 400, 400, paint);
			Bitmap b = BitmapFactory.decodeResource(getResources(),
					R.drawable.tennisracket3);
			canvas.drawBitmap(b, 0, 0, paint);
			break;
		}
	}

	public void draw() {
		shape = Constants.SHAPE_RECT;
		invalidate();
	}
}
