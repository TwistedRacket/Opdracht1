package com.g2.twistedracket;

import java.io.File;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.g2.twistedracket.R.color;
import com.g2.twistedracket.canvas.Item;
import com.g2.twistedracket.canvas.Utils;
import com.g2.twistedracket.drawer.ItemListAdapter;
import com.g2.twistedracket.drawer.LayerDrawerListAdapter;
import com.g2.twistedracket.drawer.NavigationDrawerItem;
import com.g2.twistedracket.drawer.NavigationDrawerListAdapter;

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

	public CanvasFragment canvasFragment;

	private PopupWindow popupWindow;

	private Uri imageUri;

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int RESULT_LOAD_IMAGE = 2;

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_splash_screen);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				displayCanasFragment();
			}
		}, 1000);
	}

	public void displayCanasFragment() {
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

		actionBarDrawerToggle.syncState();
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

		if (getResources().getBoolean(R.bool.isTablet)) {
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[0],
					navMenuIconList.getResourceId(3, -1)));
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[1],
					navMenuIconList.getResourceId(4, -1)));
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[2],
					navMenuIconList.getResourceId(5, -1)));
		} else {
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[0],
					navMenuIconList.getResourceId(0, -1)));
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[1],
					navMenuIconList.getResourceId(1, -1)));
			navDrawerItems.add(new NavigationDrawerItem(navMenuTitleList[2],
					navMenuIconList.getResourceId(2, -1)));
		}
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
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
					drawerLayout.closeDrawer(Gravity.RIGHT);
				}
				super.onDrawerSlide(drawerView, slideOffset);
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
							canvasFragment.canvasView.saveTo("test");
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
		layerListView.setEmptyView(findViewById(R.id.empty_list_text_view));
		layerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// drawerLayout.closeDrawers();
				canvasFragment.canvasViewSetSelectedItem(layerListAdapter
						.revertSelectedItemPosition(position));
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

	public void setSelectedLayerListItem(int position) {
		layerListView.setItemChecked(position, true);
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
					canvasFragment.createColorPicker(null);
				} else if (position == 6) {
					launchCamera();
				} else if (position == 4) {
					canvasFragment.updateCanvas(position);
				} else if (position == 5) {
					openFoto();
				} else {
					canvasFragment.updateCanvas(position);
				}
				layerListAdapter.notifyDataSetChanged();
				popupWindow.dismiss();

				setSelectedLayerListItem(0);
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
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null) {
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, "New Picture");
			values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
			imageUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
			try {
				String imageUrl = Utils.getRealPathFromURI(imageUri,
						getApplicationContext());

				File imgFile = new File(imageUrl);
				if (imgFile.exists()) {
					Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile
							.getAbsolutePath());

					canvasFragment.canvasView.addPicture(Utils
							.getResizedBitmap(imageBitmap,
									getApplicationContext()));
					layerListAdapter.notifyDataSetChanged();

					Log.i("TR", "Width: " + imageBitmap.getWidth());
					Log.i("TR", "Height: " + imageBitmap.getHeight());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

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
		} else if (item.getItemId() == R.id.action_add_item) {
			createNewItemPopupWindow();
		} else if (item.getItemId() == R.id.action_menu) {
			if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				drawerLayout.closeDrawer(Gravity.RIGHT);
			} else {
				if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
					drawerLayout.closeDrawer(Gravity.LEFT);
				}
				drawerLayout.openDrawer(Gravity.RIGHT);
			}
		} else if (item.getItemId() == R.id.action_save) {
			canvasFragment.saveToImage();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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

	@Override
	public LayerDrawerListAdapter getLayerListAdapter() {
		return layerListAdapter;
	}
}
