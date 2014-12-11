package com.g2.twistedracket.layerdrawer;

import java.util.ArrayList;

import com.g2.twistedracket.R;
import com.g2.twistedracket.canvas.Item;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LayerDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Item> itemList;
	private ViewHolder holder;

	public LayerDrawerListAdapter(Context context, ArrayList<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Log.i("MyActivity", "asdfsa");
		holder.title.setText("whalaa " + itemList.get(position).shapeVersion);

		return convertView;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void addItem(Item item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	private static class ViewHolder {
		TextView title;
		ImageView imgIcon;
	}

}
