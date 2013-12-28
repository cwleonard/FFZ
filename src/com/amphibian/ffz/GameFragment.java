package com.amphibian.ffz;


import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class GameFragment extends Fragment {

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		GLSurfaceView glView = new FFZSurfaceView(this.getActivity());
		this.getActivity().setContentView(glView);

		
	}
	
	
}
