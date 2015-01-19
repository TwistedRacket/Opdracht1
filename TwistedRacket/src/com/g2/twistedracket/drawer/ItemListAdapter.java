package com.g2.twistedracket.drawer;

import java.util.ArrayList;

import com.g2.twistedracket.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<NavigationDrawerItem> navigationDrawerItems;
	private Typeface typeFace;
	private ViewHolder holder;

	public ItemListAdapter(Context context,
			ArrayList<NavigationDrawerItem> navigationDrawerItems) {
		this.context = context;
		this.navigationDrawerItems = navigationDrawerItems;
		this.typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Medium.ttf");
		// this.holder = new ViewHolder();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.new_shape_list_item,
					parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.shapeTitle);
			holder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setTypeface(typeFace);
		holder.title.setText(navigationDrawerItems.get(position).getTitle());
		holder.imgIcon.setImageResource(navigationDrawerItems.get(position)
				.getIcon());

		return convertView;
	}

	@Override
	public int getCount() {
		return navigationDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navigationDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		TextView title;
		ImageView imgIcon;
	}
}