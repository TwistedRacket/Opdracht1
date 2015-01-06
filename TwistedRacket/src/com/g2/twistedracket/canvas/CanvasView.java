package com.g2.twistedracket.canvas;

import java.util.ArrayList;

import com.g2.twistedracket.Constants;
import com.g2.twistedracket.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Canvas.VertexMode;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CanvasView extends View {

	private Paint paint;
	private Paint fingerPaint;

	private float scaleFactor = 1.0f;

	private ArrayList<Item> itemList;
	private Path path = new Path();

	public boolean fingerDrawingEnabled = false;
	private boolean movingEnabled = true;
	private boolean scaleEnabled = true;
	public boolean canShowRacket = true;
	public boolean isInverted = false;

	public int selectedItem = 0;
	public int selectedColor = Color.parseColor("#DA001A");
	private final ScaleGestureDetector scaleGestureDetector;
	public ScaleGestureDetector detectorZ;

	private final Bitmap racketBackground;
	private final Bitmap racketBackgroundInverted;

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.racketBackground = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.tennis_racket_full_black), Utils
				.getWidth(context), Utils.getHeight(context), true);
		this.racketBackgroundInverted = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.tennis_racket_full_black_invert), Utils
				.getWidth(context), Utils.getHeight(context), true);

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(selectedColor);
		paint.setTextSize(160f);

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

		if (canShowRacket) {
			if (isInverted) {
				canvas.drawColor(Color.WHITE);
			} else {
				canvas.drawColor(Color.BLACK);
			}
		}

		int pos = 0;
		for (Item item : itemList) {
			if (item.isVisible) {
				if (pos == selectedItem && scaleEnabled && detectorZ != null) {
					item.width = (int) (Constants.DEFAULT_ITEM_WIDTH * scaleFactor);
					item.height = (int) (Constants.DEFAULT_ITEM_HEIGHT * scaleFactor);
					if (item.shapeVersion == Constants.SHAPE_CIRCLE) {
						item.posX = (int) detectorZ.getFocusX();
						item.posY = (int) detectorZ.getFocusY();
					} else {
						item.posX = (int) detectorZ.getFocusX() - item.width
								/ 2;
						item.posY = (int) detectorZ.getFocusY() - item.height
								/ 2;
					}

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
				case Constants.SHAPE_TRIANGLE:
					float[] verts = new float[6];
					verts[0] = item.posX;
					verts[1] = item.posY + item.height;
					verts[2] = item.posX + item.width;
					verts[3] = item.posY + item.height;
					verts[4] = item.posX + item.width / 2;
					verts[5] = item.posY;
					
					Path pathTriangle = new Path();

					pathTriangle.moveTo(verts[0], verts[1]);
					pathTriangle.lineTo(verts[2], verts[3]);
					pathTriangle.lineTo(verts[4], verts[5]);
					canvas.drawPath(pathTriangle, paint);
					break;
				case Constants.TEXT:
					canvas.drawText(item.text, item.posX, item.posY, paint);
					break;
				case Constants.PHOTO:
					canvas.drawBitmap(item.bitmap, 0, 0, paint);
					break;
				}
				pos++;
			}
		}

		canvas.drawPath(path, fingerPaint);

		if (canShowRacket) {
			if (isInverted) {
				canvas.drawBitmap(racketBackgroundInverted, 0, 0, paint);
			} else {
				canvas.drawBitmap(racketBackground, 0, 0, paint);
			}
		}
	}

	public void addItemToCanvas(int shapeVersion) {
		Log.i("MyActivity", "Int: " + shapeVersion);
		if (shapeVersion == 4) {
			fingerDrawingEnabled = true;
		} else {
			fingerDrawingEnabled = false;
			if (shapeVersion == 2) {
				itemList.add(new Item(Constants.SHAPE_TRIANGLE, selectedColor));
			} else {
				itemList.add(new Item(shapeVersion, selectedColor));
			}
		}

		invalidate();
	}

	public void setText(String text) {
		Item item = new Item(Constants.TEXT, selectedColor);
		item.text = text;
		itemList.add(item);
		invalidate();
	}

	public void addPicture(Bitmap bitmap) {
		Item item = new Item(Constants.PHOTO, selectedColor);
		item.bitmap = bitmap;
		itemList.add(item);
		invalidate();
	}

	public void setColor(int color) {
		selectedColor = color;
		paint.setColor(color);
		fingerPaint.setColor(color);
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
		float eventX = event.getX();
		float eventY = event.getY();

		if (eventX > 100 && eventX < 980) {
			scaleGestureDetector.onTouchEvent(event);

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
			} else if (movingEnabled && !itemList.isEmpty()) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					Item item = itemList.get(selectedItem);
					if (item.shapeVersion == Constants.SHAPE_CIRCLE) {
						item.posX = (int) eventX;
						item.posY = (int) eventY;
					} else {
						item.posX = (int) (eventX - (item.width / 2));
						item.posY = (int) (eventY - (item.height / 2));
					}
					break;
				}
			}
			invalidate();
		}
		return true;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
			detectorZ = detector;

			// don't let the object get too small or too large.
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

			invalidate();
			return true;
		}
	}

}
