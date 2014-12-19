package com.g2.twistedracket.drawer;

import java.util.ArrayList;

import com.g2.twistedracket.MainActivity;
import com.g2.twistedracket.R;
import com.g2.twistedracket.canvas.Item;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class LayerDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Item> itemList;
	private ViewHolder holder;
	private MainActivity activity;

	public LayerDrawerListAdapter(MainActivity activity, Context context,
			ArrayList<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
		this.activity = activity;
		this.holder = new ViewHolder();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layer_item, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.layerName);
			holder.visibilityOnButton = (ImageButton) convertView
					.findViewById(R.id.visibilityOnButton);
			holder.visibilityOffButton = (ImageButton) convertView
					.findViewById(R.id.visibilityOffButton);
			holder.changeColorButton = (ImageButton) convertView
					.findViewById(R.id.changeColorButton);
			holder.deleteButton = (ImageButton) convertView
					.findViewById(R.id.deleteButton);

			holder.visibilityOffButton.setVisibility(View.INVISIBLE);
			holder.visibilityOffButton.setEnabled(false);

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
				holder.visibilityOnButton.setVisibility(View.INVISIBLE);
				holder.visibilityOffButton.setVisibility(View.VISIBLE);
				holder.visibilityOnButton.setEnabled(false);
				holder.visibilityOffButton.setEnabled(true);

				item.isVisible = false;
				activity.invalidateCanvas();
			}
		});

		holder.visibilityOffButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.visibilityOnButton.setVisibility(View.VISIBLE);
				holder.visibilityOffButton.setVisibility(View.INVISIBLE);
				holder.visibilityOnButton.setEnabled(true);
				holder.visibilityOffButton.setEnabled(false);

				item.isVisible = true;
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
		ImageButton visibilityOffButton;
		ImageButton deleteButton;
	}

}
