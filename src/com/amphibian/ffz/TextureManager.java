package com.amphibian.ffz;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureManager {
	
	private HashMap<Integer, Integer> textureMap; 
	private int[] textureFiles; 
	private Context context; 
	private int[] textures;
	
	public TextureManager(Context context) {
		this.context = context;
		this.textureMap = new HashMap<Integer, Integer>();
	}
	
	public void add(int resource) {
		
		if (textureFiles == null) { 
			textureFiles = new int[1]; 
			textureFiles[0]=resource; 
		} else { 
			int[] newarray = new int[textureFiles.length+1]; 
			for(int i=0; i < textureFiles.length; i++)	 { 
				newarray[i] = textureFiles[i]; 
			} 
			newarray[textureFiles.length] = resource; 
			textureFiles = newarray; 
		}
		
	}

	public void loadTextures() {
		
		int[] tmp_tex = new int[textureFiles.length];
		GLES20.glGenTextures(textureFiles.length, tmp_tex, 0);
		textures = tmp_tex;
		
		for (int i = 0; i < textureFiles.length; i++) {

			final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
					textureFiles[i], options);
			
			textureMap.put(Integer.valueOf(textureFiles[i]), Integer.valueOf((i)));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tmp_tex[i]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_LINEAR);
			
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
			
			bmp.recycle();

		}
	}

	public void setTexture(int id) {
		try {
			
			int textureid = this.textureMap.get(Integer.valueOf(id));
			
			// Set the active texture unit to texture unit 0.
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textures[textureid]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			
		} catch (Exception e) {
			Log.e("ffz", "unable to set texture", e);
		}
	}

	public void setTextureToRepeat(int id) {
		try {
			
			int textureid = this.textureMap.get(Integer.valueOf(id));
			
			// Set the active texture unit to texture unit 0.
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textures[textureid]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			//GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			
		} catch (Exception e) {
			Log.e("ffz", "unable to set texture", e);
		}
	}


}
