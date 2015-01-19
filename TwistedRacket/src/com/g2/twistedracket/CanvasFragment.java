package com.g2.twistedracket;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import com.g2.twistedracket.canvas.CanvasView;
import com.g2.twistedracket.canvas.Item;
import com.g2.twistedracket.drawer.LayerDrawerListAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CanvasFragment extends Fragment {

	protected CanvasView canvasView;
	private ActivityCommunication activityCommunication;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			activityCommunication = (ActivityCommunication) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_canvas, container,
				false);

		canvasView = (CanvasView) rootView.findViewById(R.id.canvas_view);
		canvasView.setUp(activityCommunication.getArrayList());

		return rootView;
	}

	protected void updateCanvas(int version) {
		if (version == 8) {
			canvasView.clearCanvas();
		} else if (version == 3) {
			addTextItem();
		} else {
			canvasView.addItemToCanvas(version);
		}
	}

	public void createColorPicker(final Item item) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(),
				canvasView.selectedColor, new OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						if (item != null) {
							item.color = color;
							canvasView.invalidate();
							activityCommunication.getLayerListAdapter()
									.notifyDataSetChanged();
						} else {
							canvasView.setColor(color);
						}
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

		dialog.show();
	}

	protected void canvasViewSetSelectedItem(int position) {
		canvasView.selectedItem = position;
	}

	private void addTextItem() {
		final Dialog dialog = createDialog("Set Text");
		Button ofButton = (Button) dialog.findViewById(R.id.textDialogOkButton);
		final EditText editText = (EditText) dialog
				.findViewById(R.id.inputText);

		ofButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!editText.getText().equals("")) {
					canvasView.setText(editText.getText().toString());
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	protected void saveToImage() {
		final Dialog dialog = createDialog("Set Name");
		Button ofButton = (Button) dialog.findViewById(R.id.textDialogOkButton);
		final EditText editText = (EditText) dialog
				.findViewById(R.id.inputText);

		ofButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!editText.getText().equals("")) {
					canvasView.saveTo(editText.getText().toString());
					Toast.makeText(getActivity().getApplicationContext(),
							"Saved", Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	private Dialog createDialog(String title) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle(title);
		dialog.setContentView(R.layout.text_dialog);

		return dialog;
	}

	protected interface ActivityCommunication {
		public ArrayList<Item> getArrayList();

		public LayerDrawerListAdapter getLayerListAdapter();

	}
}
