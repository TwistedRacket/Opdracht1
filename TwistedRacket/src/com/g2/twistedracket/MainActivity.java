package com.g2.twistedracket;

import java.util.ArrayList;

import com.g2.twistedracket.CanvasFragment.ActivityCommunication;
import com.g2.twistedracket.canvas.Item;
import com.g2.twistedracket.layerdrawer.LayerDrawerListAdapter;
import com.g2.twistedracket.navdrawer.ItemListAdapter;
import com.g2.twistedracket.navdrawer.NavigationDrawerItem;
import com.g2.twistedracket.navdrawer.NavigationDrawerListAdapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		ActivityCommunication {

	private DrawerLayout drawerLayout;
	private ListView navigationDrawerListView;
	public ListView layerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	// slide menu items
	private ArrayList<NavigationDrawerItem> navDrawerItems;
	private ArrayList<Item> layerItems;
	private ArrayList<NavigationDrawerItem> itemPopupList;
	private NavigationDrawerListAdapter adapter;
	private LayerDrawerListAdapter layerListAdapter;
	private ItemListAdapter itemListAdapter;
	private Toolbar toolbar;

	private FrameLayout dimmedBackground;

	private CanvasFragment canvasFragment;

	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		createLeftNavigationDrawer();
		createRightLayerDrawer();

		dimmedBackground = (FrameLayout) findViewById(R.id.dimmedBackground);
		dimmedBackground.getForeground().setAlpha(0);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.fragmentContainer, new CanvasFragment());
		ft.commit();

	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		if (fragment instanceof CanvasFragment) {
			canvasFragment = (CanvasFragment) fragment;
		}
	}

	private void createLeftNavigationDrawer() {
		String[] navMenuTitleList;
		TypedArray navMenuIconList;

		navMenuIconList = getResources().obtainTypedArray(
				R.array.navigation_drawer_icons);
		navMenuTitleList = getResources().getStringArray(
				R.array.navigation_drawer_items_title);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationDrawerListView = (ListView) findViewById(R.id.left_menu);

		navDrawerItems = new ArrayList<NavigationDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[0],
				navMenuIconList.getResourceId(0, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[1],
				navMenuIconList.getResourceId(1, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[2],
				navMenuIconList.getResourceId(2, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[3],
				navMenuIconList.getResourceId(2, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[4],
				navMenuIconList.getResourceId(2, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[5],
				navMenuIconList.getResourceId(2, -1)));

		navMenuIconList.recycle();

		// setting the nav drawer list adapter
		adapter = new NavigationDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		navigationDrawerListView.setAdapter(adapter);

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				toolbar, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};

		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
		navigationDrawerListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						drawerLayout.closeDrawers();
						if (position == 0) {

							createConnectionLostPopupWindow();
							return;
						}
						//
						drawerLayout.closeDrawers();
						if (position == 4) {
							canvasFragment.createColorPicker();
						} else {
							canvasFragment.updateCanvas(position);
						}
						layerListAdapter.notifyDataSetChanged();

					}
				});
	}

	public void createRightLayerDrawer() {
		layerItems = new ArrayList<>();

		layerListAdapter = new LayerDrawerListAdapter(this,
				getApplicationContext(), layerItems);

		layerListView = (ListView) findViewById(R.id.right_menu);
		layerListView.setAdapter(layerListAdapter);
		layerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				drawerLayout.closeDrawers();
				canvasFragment.canvasViewSetSelectedItem(position);
			}
		});

		CheckBox checkBoxInverted = (CheckBox) findViewById(R.id.invertedRacket);
		checkBoxInverted
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						canvasFragment.canvasView.isInverted = isChecked;
						canvasFragment.canvasView.invalidate();
					}
				});

		CheckBox checkBox = (CheckBox) findViewById(R.id.showRacketCheckBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				canvasFragment.canvasView.canShowRacket = isChecked;
				canvasFragment.canvasView.invalidate();
			}
		});

	}

	public void invalidateCanvas() {
		canvasFragment.canvasView.invalidate();
	}

	public void createConnectionLostPopupWindow() {
		dimmedBackground.getForeground().setAlpha(200);

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.dialog_new_item, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);

		String[] navMenuTitleList;
		TypedArray navMenuIconList;

		navMenuIconList = getResources().obtainTypedArray(
				R.array.navigation_drawer_icons);
		navMenuTitleList = getResources().getStringArray(
				R.array.navigation_drawer_items_title);

		itemPopupList = new ArrayList<NavigationDrawerItem>();

		// adding nav drawer items to array
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[0],
				navMenuIconList.getResourceId(0, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[1],
				navMenuIconList.getResourceId(1, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[2],
				navMenuIconList.getResourceId(2, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[3],
				navMenuIconList.getResourceId(3, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[4],
				navMenuIconList.getResourceId(4, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[5],
				navMenuIconList.getResourceId(5, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[6],
				navMenuIconList.getResourceId(6, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[7],
				navMenuIconList.getResourceId(7, -1)));
		itemPopupList.add(new NavigationDrawerItem(navMenuTitleList[8],
				navMenuIconList.getResourceId(8, -1)));

		navMenuIconList.recycle();

		ListView listView = (ListView) popupView.findViewById(R.id.item_menu);

		itemListAdapter = new ItemListAdapter(getApplicationContext(),
				itemPopupList);
		listView.setAdapter(itemListAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				drawerLayout.closeDrawers();
				if (position == 7) {
					canvasFragment.createColorPicker();
				} else {
					canvasFragment.updateCanvas(position);
				}
				layerListAdapter.notifyDataSetChanged();
				popupWindow.dismiss();
				dimmedBackground.getForeground().setAlpha(0);
			}
		});

		popupWindow.showAtLocation(findViewById(R.id.activity_main_name),
				Gravity.CENTER, 0, 0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			dimmedBackground.getForeground().setAlpha(0);
			return;
		}

		if (drawerLayout.isDrawerOpen(Gravity.START)
				|| drawerLayout.isDrawerOpen(Gravity.END)) {
			drawerLayout.closeDrawers();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public ArrayList<Item> getArrayList() {
		return layerItems;
	}

}
