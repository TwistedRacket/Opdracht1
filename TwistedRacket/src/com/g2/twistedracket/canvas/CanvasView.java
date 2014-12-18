package com.g2.twistedracket.canvas;

import java.util.ArrayList;

import com.g2.twistedracket.Constants;
import com.g2.twistedracket.R;
import com.g2.twistedracket.R.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CanvasView extends View {

	private Context context;
	private Paint paint;
	private Paint fingerPaint;

	private float scaleFactor = 1.0f;

	private ArrayList<Item> itemList;
	private Path path = new Path();

	private boolean fingerDrawingEnabled = false;
	private boolean movingEnabled = true;
	private boolean scaleEnabled = true;

	public int selectedItem = 0;
	public int selectedColor = Color.parseColor("#DA001A");
	private ScaleGestureDetector scaleGestureDetector;

	private final Bitmap racketBackground = Bitmap.createScaledBitmap(
			BitmapFactory.decodeResource(getResources(),
					R.drawable.tennis_racket_full_black), 1080, 1700, true);

	private final Bitmap grayBackground = BitmapFactory.decodeResource(
			getResources(), R.drawable.tennisracketsnaart);

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(selectedColor);
		paint.setTextSize(80f);

		fingerPaint = new Paint();
		fingerPaint.setAntiAlias(true);
		fingerPaint.setStrokeWidth(20f);
		fingerPaint.setColor(selectedColor);
		fingerPaint.setStyle(Paint.Style.STROKE);
		fingerPaint.setStrokeJoin(Paint.Join.ROUND);

		scaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener());
	}

	public void setUp(ArrayList<Item> itemList) {
		this.itemList = itemList;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// canvas.drawColor(color.black);

		int pos = 0;
		for (Item item : itemList) {
			if (item.isVisible) {
				if (pos == selectedItem && scaleEnabled) {
					item.width = (int) (Constants.DEFAULT_ITEM_WIDTH * scaleFactor);
					item.height = (int) (Constants.DEFAULT_ITEM_HEIGHT * scaleFactor);
				}

				paint.setColor(item.color);

				switch (item.shapeVersion) {
				case Constants.SHAPE_RECT:
					canvas.drawRect(item.posX, item.posY, item.posX
							+ item.width, item.posY + item.height, paint);
					break;
				case Constants.SHAPE_CIRCLE:
					canvas.drawCircle(item.posX, item.posY, item.width / 2,
							paint);
					break;

				case Constants.TEXT:
					canvas.drawText(item.text, item.posX, item.posY, paint);
					break;
				}
				pos++;
			}
		}

		canvas.drawPath(path, fingerPaint);
		// canvas.drawBitmap(racketBackground, 0, 0, paint);
	}

	public void addItemToCanvas(int shapeVersion) {
		Log.i("MyActivity", "Int: " + shapeVersion);
		if (shapeVersion == 2) {
			fingerDrawingEnabled = true;
		} else {
			fingerDrawingEnabled = false;
			itemList.add(new Item(shapeVersion, selectedColor));
		}

		invalidate();
	}

	public void clearCanvas() {
		fingerDrawingEnabled = false;
		itemList.clear();
		path.reset();
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!itemList.isEmpty()) {
			scaleGestureDetector.onTouchEvent(event);
			float eventX = event.getX();
			float eventY = event.getY();

			if (fingerDrawingEnabled) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					path.moveTo(eventX, eventY);
					return true;
				case MotionEvent.ACTION_MOVE:
					path.lineTo(eventX, eventY);
					break;
				default:
					return false;
				}
			} else if (movingEnabled) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					Item item = itemList.get(selectedItem);
					if (item.shapeVersion == Constants.SHAPE_CIRCLE) {
						item.posX = (int) eventX;
						item.posY = (int) eventY;
					} else {
						item.posX = (int) eventX - (item.width / 2);
						item.posY = (int) eventY - (item.height / 2);
					}

					break;
				}
			}
			invalidate();
		}
		return true;
	}

	public void setColor(int color) {
		selectedColor = color;
		paint.setColor(color);
		fingerPaint.setColor(color);
		invalidate();
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();

			// don't let the object get too small or too large.
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

			invalidate();
			return true;
		}
	}

	public void setText(String text) {
		Item item = new Item(Constants.TEXT, selectedColor);
		item.text = text;
		itemList.add(item);
		invalidate();
	}
}
