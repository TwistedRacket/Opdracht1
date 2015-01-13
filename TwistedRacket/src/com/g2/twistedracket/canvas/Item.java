package com.g2.twistedracket.canvas;

import android.graphics.Bitmap;
import android.graphics.Path;

import com.g2.twistedracket.Constants;

public class Item {

	public int shapeVersion;
	public int posX = 100;
	public int posY = 100;
	public int width = Constants.DEFAULT_ITEM_WIDTH;
	public int height = Constants.DEFAULT_ITEM_HEIGHT;
	public float scaleFactor;
	public int color;
	public boolean isVisible = true;

	public String layerName;
	public String text;
	public Bitmap bitmap;
	public Path path;

	public static int itemNumber = 1;
	public int number;

	public Item(int shapeVersion, int color) {
		this.shapeVersion = shapeVersion;
		this.color = color;
		if (shapeVersion == Constants.FINGER_PAINT) {
			this.path = new Path();
		}

		this.layerName = "Layer" + itemNumber;
		number = itemNumber;
		itemNumber++;
	}
}
