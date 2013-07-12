package com.amphibian.ffz;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

public class FFZRenderer implements Renderer {

	private Context context;
	
	private int frames  = 0;
	private long lastTime;
	
	private Engine engine;

    public FFZRenderer(Engine e, Context c) {
    	
    	//this.engine = e;
    	
    	this.context = c;
    	
    	lastTime = SystemClock.elapsedRealtime();

    	
    }
    
    public Engine getEngine() {
    	return engine;
    }
    
	@Override
	public void onDrawFrame(GL10 unused) {
		
		long now = SystemClock.elapsedRealtime();
		long frameTime = now - lastTime;
		lastTime = now;

		if (frames == 1000) {
			Log.d("ffz", "frame time = " + frameTime);
			frames = 0;
		}
		
		
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

	    // Calculate the projection and view transformation
		//Matrix.multiplyMM(mVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

		engine.update();
		
		
		//triangle.draw(mVPMatrix);
		//triangle.draw(projMatrix, viewMatrix);
//		triangle2.draw(projMatrix, viewMatrix);
		//square.draw(projMatrix, viewMatrix);
		
		engine.draw();
		
		
		
		
		frames++;

	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {

		engine.createViewport(height, width);
		
//		GLES20.glViewport(0, 0, width, height);
//		float ratio = (float)width/height;
//		//Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//		//Matrix.orthoM(projMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
//		Matrix.orthoM(projMatrix, 0,
//				0,       // left
//				width,   // right
//				-height, // bottom
//				0,       // top
//				-1,      // near
//				1);      // far
//
//		// Set the camera position (View matrix)
//		Matrix.setLookAtM(viewMatrix, 0,
//	    		0.0f, 0.0f, 1.0f, // eye
//	    		0.0f, 0.0f, 0.0f,  // center
//	    		0.0f, 1.0f, 0.0f); // up

	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
		Log.i("ffz", "onSurfaceCreated called");
		
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		
		//GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		//GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		//GLES20.glDepthMask( true );
		
		if (engine == null) {
		
			engine = new Engine(context);
			
		} else {
			engine.glSetup(context);
		}
		
		//engine.loadTextures(context);
//		}
		
		//triangle = new Triangle();
//		triangle2 = new Triangle();
//		triangle2.color = new float[]{ 0.8f, 0.2f, 0.2f, 1.0f };
		//square = new Square();
		//square.color = new float[]{ 0.8f, 0.2f, 0.2f, 1.0f };
		//square.loadGLTexture(context);

	}

	public static int loadShader(int type, String shaderCode){

	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	}

//	public Engine getEngine() {
//		return engine;
//	}

	

}
