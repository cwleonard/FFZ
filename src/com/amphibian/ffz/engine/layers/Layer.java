package com.amphibian.ffz.engine.layers;

import com.amphibian.ffz.engine.Viewport;
import com.amphibian.ffz.opengl.StandardProgram;

public interface Layer {

	public int[] getTextures();
	
	public void draw(StandardProgram prog, Viewport vp);
	
}
