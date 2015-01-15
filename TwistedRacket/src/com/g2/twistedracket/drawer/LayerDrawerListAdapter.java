package com.g2.twistedracket.drawer;

import java.util.ArrayList;
import java.util.HashMap;

import com.g2.twistedracket.MainActivity;
import com.g2.twistedracket.R;
import com.g2.twistedracket.canvas.Item;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class LayerDrawerListAdapter extends BaseAdapter implements
		DragNDropAdapter {

	private Context context;
	private ArrayList<Item> itemList;
	private MainActivity activity;
	HashMap<Integer, Boolean> visibilityByIndex = new HashMap<Integer, Boolean>();

	int mPosition[];
	int mHandler;

	public LayerDrawerListAdapter(MainActivity activity, Context context,
			ArrayList<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
		this.activity = activity;
	}

	private void setup(int size) {
		mPosition = new int[size];

		for (int i = 0; i < size; ++i)
			mPosition[i] = i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final int position2 = position;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layer_item, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.layerName);
			holder.visibilityOnButton = (ImageButton) convertView
					.findViewById(R.id.visibilityButton);
			holder.changeColorButton = (ImageButton) convertView
					.findViewById(R.id.changeColorButton);
			holder.deleteButton = (ImageButton) convertView
					.findViewById(R.id.deleteButton);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Item item = itemList.get(position);
		holder.title.setText(item.layerName);
		holder.title.setTextColor(item.color);

		holder.visibilityOnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!visibilityByIndex.containsKey(position2)
						|| visibilityByIndex.get(position2)) {
					visibilityByIndex.put(position2, false);
					holder.visibilityOnButton.setImageDrawable(context
							.getResources().getDrawable(
									R.drawable.ic_visibility_off_grey600_24dp));

					Log.i("paf", "LDGV: false" + " Position: " + position2);
					item.isVisible = false;
				} else if (!visibilityByIndex.get(position2)) {
					visibilityByIndex.put(position2, true);
					holder.visibilityOnButton.setImageDrawable(context
							.getResources().getDrawable(
									R.drawable.ic_visibility_grey600_24dp));
					Log.i("paf", "LDGV: true" + " Position: " + position2);
					item.isVisible = true;
				}
				activity.invalidateCanvas();
			}
		});

		holder.changeColorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.canvasFragment.createColorPicker(item);
				notifyDataSetChanged();
			}
		});

		holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				itemList.remove(item);
				notifyDataSetChanged();
				activity.invalidateCanvas();
			}
		});

		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(mPosition[position]);
	}

	@Override
	public View getDropDownView(int position, View view, ViewGroup group) {
		return super.getDropDownView(mPosition[position], view, group);
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		TextView title;
		ImageButton changeColorButton;
		ImageButton visibilityOnButton;
		ImageButton deleteButton;
	}

	@Override
	public void onItemDrag(DragNDropListView parent, View view, int position,
			long id) {

	}

	@Override
	public void onItemDrop(DragNDropListView parent, View view,
			int startPosition, int endPosition, long id) {
		int position = mPosition[startPosition];

		if (startPosition < endPosition)
			for (int i = startPosition; i < endPosition; ++i)
				mPosition[i] = mPosition[i + 1];
		else if (endPosition < startPosition)
			for (int i = startPosition; i > endPosition; --i)
				mPosition[i] = mPosition[i - 1];

		mPosition[endPosition] = position;
	}

	@Override
	public int getDragHandler() {
		return mHandler;
	}

}
