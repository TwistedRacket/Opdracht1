package com.g2.twistedracket;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.g2.twistedracket.canvas.Item;
import com.g2.twistedracket.canvas.Utils;
import com.g2.twistedracket.drawer.ItemListAdapter;
import com.g2.twistedracket.drawer.LayerDrawerListAdapter;
import com.g2.twistedracket.drawer.NavigationDrawerItem;
import com.g2.twistedracket.drawer.NavigationDrawerListAdapter;

;

public class MainActivity extends ActionBarActivity implements
		CanvasFragment.ActivityCommunication {

	private DrawerLayout drawerLayout;
	private ListView navigationDrawerListView;
	private ListView layerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	// slide menu items
	private ArrayList<NavigationDrawerItem> navDrawerItems;
	private ArrayList<Item> layerItemList;
	private ArrayList<NavigationDrawerItem> popupItemList;

	private NavigationDrawerListAdapter navigationListAdapter;
	private LayerDrawerListAdapter layerListAdapter;
	private ItemListAdapter itemListAdapter;
	private Toolbar toolbar;

	private FrameLayout dimmedBackground;

	private CanvasFragment canvasFragment;

	private PopupWindow popupWindow;

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int RESULT_LOAD_IMAGE = 2;

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
				R.array.navigation_drawer_leftmenu_icons);
		navMenuTitleList = getResources().getStringArray(
				R.array.navigation_drawer_items_title);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationDrawerListView = (ListView) findViewById(R.id.left_menu);

		navDrawerItems = new ArrayList<NavigationDrawerItem>();
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[0],
				navMenuIconList.getResourceId(0, -1)));
		navMenuIconList.recycle();

		// setting the nav drawer list adapter
		navigationListAdapter = new NavigationDrawerListAdapter(
				getApplicationContext(), navDrawerItems);
		navigationDrawerListView.setAdapter(navigationListAdapter);

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
							createNewItemPopupWindow();
							return;
						}
					}
				});
	}

	public void createRightLayerDrawer() {
		layerItemList = new ArrayList<>();

		layerListAdapter = new LayerDrawerListAdapter(this,
				getApplicationContext(), layerItemList);

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

	private void createNewItemPopupWindow() {
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
				R.array.navigation_drawer_items_popup);

		popupItemList = new ArrayList<NavigationDrawerItem>();

		// adding nav drawer items to array
		for (int i = 0; i < 9; i++) {
			popupItemList.add(new NavigationDrawerItem(navMenuTitleList[i],
					navMenuIconList.getResourceId(i, -1)));
		}

		navMenuIconList.recycle();

		itemListAdapter = new ItemListAdapter(getApplicationContext(),
				popupItemList);

		ListView listView = (ListView) popupView.findViewById(R.id.item_menu);
		listView.setAdapter(itemListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				drawerLayout.closeDrawers();
				if (position == 7) {
					canvasFragment.createColorPicker();
				} else if (position == 6) {
					launchCamera();
				} else if (position == 4) {
					canvasFragment.canvasView.fingerDrawingEnabled = true;
				} else if (position == 5) {
					openFoto();
				} else {
					canvasFragment.updateCanvas(position);
				}
				layerListAdapter.notifyDataSetChanged();
				popupWindow.dismiss();
			}
		});

		popupWindow.showAtLocation(findViewById(R.id.activity_main_name),
				Gravity.CENTER, 0, 0);

		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				dimmedBackground.getForeground().setAlpha(0);
			}
		});
	}

	public void invalidateCanvas() {
		canvasFragment.canvasView.invalidate();
	}

	private void launchCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		}
	}

	public void openFoto() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RESULT_LOAD_IMAGE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extra = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extra.get("data");
			imageBitmap = Utils.getResizedBitmap(imageBitmap, 1920, 1080);
			canvasFragment.canvasView.addPicture(imageBitmap);
			layerListAdapter.notifyDataSetChanged();
		} else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			canvasFragment.canvasView.addPicture(BitmapFactory
					.decodeFile(picturePath));
			layerListAdapter.notifyDataSetChanged();
		}
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
		if (drawerLayout.isDrawerOpen(Gravity.START)
				|| drawerLayout.isDrawerOpen(Gravity.END)) {
			drawerLayout.closeDrawers();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public ArrayList<Item> getArrayList() {
		return layerItemList;
	}

}
