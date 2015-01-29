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
import android.widget.Toast;
import android.view.View.OnClickListener;

public class LayerDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Item> itemList;
	private MainActivity activity;
	private HashMap<Integer, Boolean> visibilityByIndex = new HashMap<>();

	public LayerDrawerListAdapter(MainActivity activity, Context context,
			ArrayList<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
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
			holder.itemUpButton = (ImageButton) convertView
					.findViewById(R.id.itemUpButton);
			holder.itemDownButton = (ImageButton) convertView
					.findViewById(R.id.itemDownButton);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final int normalPosition = position;
		final int reversedPosition = itemList.size() - position - 1;
		final View finalConvertView = convertView;
		final Item item = itemList.get(reversedPosition);
		holder.title.setText(item.layerName);
		holder.title.setTextColor(item.color);

		holder.visibilityOnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!visibilityByIndex.containsKey(reversedPosition)
						|| visibilityByIndex.get(reversedPosition)) {
					visibilityByIndex.put(reversedPosition, false);

					if (context.getResources().getBoolean(R.bool.isTablet)) {
						holder.visibilityOnButton
								.setImageDrawable(context
										.getResources()
										.getDrawable(
												R.drawable.ic_visibility_off_grey600_36dp));
					} else {
						holder.visibilityOnButton
								.setImageDrawable(context
										.getResources()
										.getDrawable(
												R.drawable.ic_visibility_off_grey600_24dp));
					}

					Log.i("TR", "LDGV: false" + " Position: "
							+ reversedPosition);
					item.isVisible = false;
				} else if (!visibilityByIndex.get(reversedPosition)) {
					visibilityByIndex.put(reversedPosition, true);
					boolean b = context.getResources().getBoolean(
							R.bool.isTablet);
					if (b) {
						holder.visibilityOnButton.setImageDrawable(context
								.getResources().getDrawable(
										R.drawable.ic_visibility_grey600_36dp));
					} else {
						holder.visibilityOnButton.setImageDrawable(context
								.getResources().getDrawable(
										R.drawable.ic_visibility_grey600_24dp));
					}
					Log.i("TR", "LDGV: true" + " Position: " + reversedPosition);
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
				activity.canvasFragment.canvasView.selectedItem = 0;
				notifyDataSetChanged();
				activity.invalidateCanvas();

			}
		});

		holder.itemUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int newPosition = reversedPosition + 1;
				if (newPosition < itemList.size()) {
					Item tempItem = itemList.get(newPosition);
					itemList.set(newPosition, itemList.get(reversedPosition));
					itemList.set(reversedPosition, tempItem);
					notifyDataSetChanged();
					activity.invalidateCanvas();
				} else {
					Toast.makeText(context, "Cannot move layer up",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		holder.itemDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int newPosition = reversedPosition - 1;
				if (reversedPosition > 0) {
					Item tempItem = itemList.get(newPosition);
					itemList.set(newPosition, itemList.get(reversedPosition));
					itemList.set(reversedPosition, tempItem);
					notifyDataSetChanged();
					activity.invalidateCanvas();
				} else {
					Toast.makeText(context, "Cannot move layer down",
							Toast.LENGTH_LONG).show();
				}
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

	private static class ViewHolder {
		TextView title;
		ImageButton changeColorButton;
		ImageButton visibilityOnButton;
		ImageButton deleteButton;
		ImageButton itemUpButton;
		ImageButton itemDownButton;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public int revertSelectedItemPosition(int position) {
		return itemList.size() - position - 1;
	}

}
