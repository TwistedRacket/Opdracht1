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
import android.widget.TextView;
import android.widget.ImageView;

public class NavigationDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavigationDrawerItem> navigationDrawerItems;
	private Typeface typeFace;
	private ViewHolder holder;

	public NavigationDrawerListAdapter(Context context,
			ArrayList<NavigationDrawerItem> navigationDrawerItems) {
		this.context = context;
		this.navigationDrawerItems = navigationDrawerItems;
		this.typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Medium.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.navigation_drawer_list_item, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.image = (ImageView) convertView
					.findViewById(R.id.navigationDrawerImage);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setTypeface(typeFace);
		holder.title.setText(navigationDrawerItems.get(position).getTitle());
		holder.image.setImageResource(navigationDrawerItems.get(position)
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
		ImageView image;
	}
}