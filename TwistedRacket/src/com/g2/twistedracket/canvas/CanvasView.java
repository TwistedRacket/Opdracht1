package com.g2.twistedracket.canvas;

import java.util.ArrayList;

import org.w3c.dom.Text;

import com.g2.twistedracket.Constants;
import com.g2.twistedracket.R;
import com.g2.twistedracket.R.color;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

	private int selectedItem = 0;
	public int sharedColor = color.primary_material_light;
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
		paint.setColor(Color.parseColor("#DA001A"));
		paint.setTextSize(80f);

		fingerPaint = new Paint();
		fingerPaint.setAntiAlias(true);
		fingerPaint.setStrokeWidth(20f);
		fingerPaint.setColor(sharedColor);
		fingerPaint.setStyle(Paint.Style.STROKE);
		fingerPaint.setStrokeJoin(Paint.Join.ROUND);

		itemList = new ArrayList<>();

		scaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// canvas.drawColor(color.black);

		int pos = 0;
		for (Item item : itemList) {
			if (pos == selectedItem && scaleEnabled) {
				item.width = (int) (Constants.DEFAULT_ITEM_WIDTH * scaleFactor);
				item.height = (int) (Constants.DEFAULT_ITEM_HEIGHT * scaleFactor);
			}
			//
			switch (item.shapeVersion) {
			case Constants.SHAPE_RECT:

				// Log.i("MyActivity", "X: " + item.posX);
				// Log.i("MyActivity", "Y: " + item.posY);
				// Log.i("MyActivity", "W: " + item.width);
				// Log.i("MyActivity", "H: " + item.height);

				canvas.drawRect(item.posX, item.posY, item.posX + item.width,
						item.posY + item.height, paint);
				break;
			case Constants.SHAPE_CIRCLE:
				canvas.drawCircle(item.posX, item.posY, item.width, paint);
				break;

			case Constants.TEXT:

				canvas.drawText("", item.posX, item.posY, paint);
				break;
			}
			pos++;
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
			itemList.add(new Item(shapeVersion));
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
				case MotionEvent.ACTION_UP:
					break;
				default:
					return false;
				}
			} else if (movingEnabled) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					Item item = itemList.get(selectedItem);
					item.posX = (int) eventX - (item.width / 2);
					item.posY = (int) eventY - (item.height / 2);
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
			}
			invalidate();
		}
		return true;
	}

	public void setColor(int color) {
		sharedColor = color;
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
	public void setText() {
		Dialog d = new Dialog(getContext());
		d.setTitle("Set text");
		EditText t = new EditText(getContext());
		t.setSingleLine(true);
		Button b = new Button(getContext());
		b.setText("OK");
		d.setContentView(t);
		d.show();	
	}

}
