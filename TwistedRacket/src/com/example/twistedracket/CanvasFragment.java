package com.example.twistedracket;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class CanvasFragment extends Fragment {

	private Canvas canvas;
	private Paint paint;
	private Button drawButton;
	private CanvasView cView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_canvas, container,
				false);

		cView = (CanvasView) rootView.findViewById(R.id.canvas_view);

		return rootView;
	}

	public void draw() {
		cView.draw();
	}

}
