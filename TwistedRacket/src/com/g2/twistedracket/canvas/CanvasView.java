package com.g2.twistedracket.canvas;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.g2.twistedracket.Constants;
import com.g2.twistedracket.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
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

	public boolean fingerDrawingEnabled = false;
	private boolean movingEnabled = true;
	private boolean scaleEnabled = true;
	public boolean canShowRacket = true;
	public boolean isInverted = false;

	private int firstDragX;
	private int firstDragY;
	private int itemStartingX;
	private int itemStartingY;

	public int selectedItem = 0;
	public int selectedColor = Color.parseColor("#DA001A");
	private final ScaleGestureDetector scaleGestureDetector;
	public ScaleGestureDetector detectorZ;

	private static final int MIN_TOUCH_X = 100;
	private int MAX_TOUCH_X;

	private final Bitmap racketBackground;
	private final Bitmap racketBackgroundInverted;

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);

		MAX_TOUCH_X = Utils.getWidth(context) - 100;

		this.racketBackground = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.tennis_racket_full_black), Utils
				.getWidth(context), Utils.get16by9Height(context), true);
		this.racketBackgroundInverted = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.tennis_racket_full_black_invert), Utils
				.getWidth(context), Utils.get16by9Height(context), true);

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
			// sLog.i("TR", "itemValue: " + item.number + "  " +
			// item.isVisible);
			if (item.isVisible && item != null) {
				if (pos == selectedItem && scaleEnabled && detectorZ != null) {

					if (item.shapeVersion == Constants.TEXT) {
						paint.setTextSize(50f * scaleFactor);
					} else {
						item.width = (int) (Constants.DEFAULT_ITEM_WIDTH * scaleFactor);
						item.height = (int) (Constants.DEFAULT_ITEM_HEIGHT * scaleFactor);
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
					canvas.drawBitmap(item.bitmap, item.posX, item.posY, paint);
					break;
				case Constants.FINGER_PAINT:
					fingerPaint.setColor(item.color);
					canvas.drawPath(item.path, fingerPaint);
					break;
				}
				pos++;
			}
		}

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

			itemList.add(new Item(Constants.FINGER_PAINT, selectedColor));
			fingerDrawingEnabled = true;
		} else {
			fingerDrawingEnabled = false;
			if (shapeVersion == 2) {
				itemList.add(new Item(Constants.SHAPE_TRIANGLE, selectedColor));
			} else {
				itemList.add(new Item(shapeVersion, selectedColor));
			}
		}
		selectedItem = itemList.size() - 1;
		Log.i("TR", "LSCREATE: " + itemList.size());
		invalidate();
	}

	public boolean saveTo(String fileName) {
		boolean visibleItemsOnScreen = false;

		if (itemList.isEmpty()) {
			return false;
		} else {
			for (Item item : itemList) {
				if (item.isVisible)
					visibleItemsOnScreen = true;
			}
			if (visibleItemsOnScreen = false)
				return false;

			boolean isTabletSize = getResources().getBoolean(R.bool.isTablet);
			Log.i("TR", "" + isTabletSize + " W: " + this.getWidth() + " H: "
					+ this.getHeight());
			Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),
					this.getHeight(), Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);
			this.draw(canvas);

			File file = new File(Environment.getExternalStorageDirectory()
					+ "/" + fileName + ".png");

			try {
				file.createNewFile();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						new FileOutputStream(file));

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}

	public void setText(String text) {
		Item item = new Item(Constants.TEXT, selectedColor);
		item.text = text;
		itemList.add(item);
		selectedItem = itemList.size() - 1;
		invalidate();
	}

	public void addPicture(Bitmap bitmap) {
		Item item = new Item(Constants.PHOTO, selectedColor);
		item.bitmap = bitmap;
		itemList.add(0, item);
		selectedItem = itemList.size() - 1;
		invalidate();
	}

	public void setColor(int color) {
		selectedColor = color;
		paint.setColor(color);
		fingerPaint.setColor(color);
		invalidate();
	}

	public void clearCanvas() {
		Item.itemNumber = 1;
		fingerDrawingEnabled = false;
		itemList.clear();
		// path.reset();
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		float eventX = event.getX();
		float eventY = event.getY();

		if (eventX > MIN_TOUCH_X && eventX < MAX_TOUCH_X && selectedItem != -1) {
			scaleGestureDetector.onTouchEvent(event);

			if (fingerDrawingEnabled) {
				// selectedItem = -1; // Disable moving of shape when finger
				// drawing is enabled.
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.i("TR", "LS: " + itemList.size());
					itemList.get(selectedItem).path.moveTo(eventX, eventY);
					return true;
				case MotionEvent.ACTION_MOVE:
					Log.i("TR", "LS: " + itemList.size());
					itemList.get(selectedItem).path.lineTo(eventX, eventY);
					break;
				case MotionEvent.ACTION_UP:
					Log.i("TR", "UP");
					break;
				default:
					return false;
				}
			} else if (movingEnabled && !itemList.isEmpty()) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Item item = itemList.get(selectedItem);

					firstDragX = (int) eventX;
					firstDragY = (int) eventY;
					itemStartingX = item.posX;
					itemStartingY = item.posY;

					Log.i("TR", "FirstDragX: " + firstDragX);
					Log.i("TR", "FirstDragY: " + firstDragY);

					break;
				case MotionEvent.ACTION_MOVE:
					item = itemList.get(selectedItem);

					int diffX = firstDragX - (int) eventX;
					int diffY = firstDragY - (int) eventY;

					Log.i("TR", "DiffX: " + diffX);
					Log.i("TR", "DiffY " + diffY);

					item.posX = itemStartingX - diffX;
					item.posY = itemStartingY - diffY;

					Log.i("TR", "ItemPosX: " + item.posX);
					Log.i("TR", "ItemPosY: " + item.posY);
					break;
				}
			}

			// if(event.getAction() == MotionEvent.ACTION_UP){
			// Item item = itemList.get(selectedItem);
			// item.width = item.width * scaleFactor
			// }

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

			Log.i("TR", "ScaleFactor: " + scaleFactor);

			invalidate();
			return true;
		}
	}

}
