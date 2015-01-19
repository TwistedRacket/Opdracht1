package com.g2.twistedracket.canvas;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Path;

import com.g2.twistedracket.Constants;

public class Item {

	protected int shapeVersion;
	protected int posX = 400;
	protected int posY = 400;
	protected int width = Constants.DEFAULT_ITEM_WIDTH;
	protected int height = Constants.DEFAULT_ITEM_HEIGHT;
	protected float scaleFactor;
	public int color;
	public boolean isVisible = true;

	public String layerName;
	protected String text;
	protected Bitmap bitmap;
	protected Path path;

	protected static int itemNumber = 1;
	private static HashMap<Integer, Integer> itemAmountMap = new HashMap<>();

	public Item(int shapeVersion, int color) {
		this.shapeVersion = shapeVersion;
		this.color = color;
		if (shapeVersion == Constants.FINGER_PAINT) {
			this.path = new Path();
		}

		this.layerName = getLayerName(shapeVersion);
		itemNumber++;
	}

	public static String getLayerName(int shapeVersion) {
		if (itemAmountMap.isEmpty()) {
			itemAmountMap.put(Constants.SHAPE_RECT, 0);
			itemAmountMap.put(Constants.SHAPE_CIRCLE, 0);
			itemAmountMap.put(Constants.SHAPE_TRIANGLE, 0);
			itemAmountMap.put(Constants.FINGER_PAINT, 0);
			itemAmountMap.put(Constants.TEXT, 0);
			itemAmountMap.put(Constants.PHOTO, 0);
		}

		String name = "";

		switch (shapeVersion) {
		case Constants.SHAPE_RECT:
			name = "Rect";
			itemAmountMap.put(Constants.SHAPE_RECT,
					itemAmountMap.get(Constants.SHAPE_RECT) + 1);
			break;
		case Constants.SHAPE_CIRCLE:
			name = "Circle";
			itemAmountMap.put(Constants.SHAPE_CIRCLE,
					itemAmountMap.get(Constants.SHAPE_CIRCLE) + 1);
			break;
		case Constants.SHAPE_TRIANGLE:
			name = "Trngle";
			itemAmountMap.put(Constants.SHAPE_TRIANGLE,
					itemAmountMap.get(Constants.SHAPE_TRIANGLE) + 1);
			break;
		case Constants.FINGER_PAINT:
			name = "Line";
			itemAmountMap.put(Constants.FINGER_PAINT,
					itemAmountMap.get(Constants.FINGER_PAINT) + 1);
			break;
		case Constants.TEXT:
			name = "Text";
			itemAmountMap.put(Constants.TEXT,
					itemAmountMap.get(Constants.TEXT) + 1);
			break;
		case Constants.PHOTO:
			name = "Photo";
			itemAmountMap.put(Constants.PHOTO,
					itemAmountMap.get(Constants.PHOTO) + 1);
			break;
		}

		return name + itemAmountMap.get(shapeVersion);
	}
}
