package com.g2.twistedracket.drawer;

import android.widget.ListAdapter;

public interface DragNDropAdapter extends ListAdapter,
		DragNDropListView.OnItemDragNDropListener {
	public int getDragHandler();
}