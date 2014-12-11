package com.g2.twistedracket;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import com.g2.twistedracket.canvas.CanvasView;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CanvasFragment extends Fragment {

	private CanvasView canvasView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_canvas, container,
				false);

		canvasView = (CanvasView) rootView.findViewById(R.id.canvas_view);

		return rootView;
	}

	public void updateCanvas(int version) {
		Log.i("MyActivity", "" + version);
		if (version == 3) {
			canvasView.clearCanvas();
		} else {
			canvasView.addItemToCanvas(version);
		}
	}

	public void createColorPicker() {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(),
				canvasView.sharedColor, new OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {

						canvasView.setColor(color);
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

		dialog.show();
	}

}
