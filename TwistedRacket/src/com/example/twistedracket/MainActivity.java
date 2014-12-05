package com.example.twistedracket;

import java.util.ArrayList;

import com.example.naviagationdrawer.NavigationDrawerItem;
import com.example.naviagationdrawer.NavigationDrawerListAdapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private DrawerLayout drawerLayout;
	private ListView navigationDrawerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	// slide menu items
	private ArrayList<NavigationDrawerItem> navDrawerItems;
	private NavigationDrawerListAdapter adapter;
	private Toolbar toolbar;

	private CanvasFragment canvasFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		createNavigationDrawer();

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

	private void createNavigationDrawer() {
		String[] navMenuTitleList;
		TypedArray navMenuIconList;

		navMenuIconList = getResources().obtainTypedArray(
				R.array.navigation_drawer_icons);
		navMenuTitleList = getResources().getStringArray(
				R.array.navigation_drawer_items_title);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationDrawerListView = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavigationDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[0],
				navMenuIconList.getResourceId(0, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[1],
				navMenuIconList.getResourceId(1, -1)));
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[2],
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
						switch (position) {
						case 0:
							canvasFragment.draw();
							break;
						}
						drawerLayout.closeDrawers();
					}
				});
	}
}
