package com.g2.twistedracket.canvas;

import com.g2.twistedracket.Constants;

public class Item {

	public int shapeVersion;
	public int posX = 100;
	public int posY = 100;
	public int width = Constants.DEFAULT_ITEM_WIDTH;
	public int height = Constants.DEFAULT_ITEM_HEIGHT;
	public int color;

	public String layerName;
	public String text;

	private static int itemNumber = 1;

	public Item(int shapeVersion, int color) {
		this.shapeVersion = shapeVersion;
		this.color = color;

		this.layerName = "Layer" + itemNumber;
		itemNumber++;
	}
}
