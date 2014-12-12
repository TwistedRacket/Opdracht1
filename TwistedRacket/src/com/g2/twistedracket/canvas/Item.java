package com.g2.twistedracket.canvas;

import com.g2.twistedracket.Constants;
import com.g2.twistedracket.R.color;

public class Item {

	public int shapeVersion;
	public int posX = 100;
	public int posY = 100;
	public int width = Constants.DEFAULT_ITEM_WIDTH;
	public int height = Constants.DEFAULT_ITEM_HEIGHT;
	public int colorInt;
	
	public String name = "Layer";
	public String text;

	public Item(int shapeItem) {
		this.shapeVersion = shapeItem;
		colorInt = color.colorPrimary;
	}
}
