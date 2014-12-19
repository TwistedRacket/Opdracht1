package com.g2.twistedracket.canvas;

import android.graphics.Bitmap;

import com.g2.twistedracket.Constants;

public enum EnumItem {
	RECTANGLE(), CIRCLE(), TRIANGLE();

	public int shapeVersion;
	public int posX = 100;
	public int posY = 100;
	public int width = Constants.DEFAULT_ITEM_WIDTH;
	public int height = Constants.DEFAULT_ITEM_HEIGHT;
	public int color;
	public boolean isVisible = true;

	public String layerName;
	public String text;
	public Bitmap bitmap;

	EnumItem() {

	}
}
