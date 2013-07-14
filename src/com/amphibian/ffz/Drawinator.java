package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Drawinator {

	public static final int SHADOW_MODE = 0;
	public static final int NORMAL_MODE = 1;
	
	final static float SHADOW_SCALE = 0.7f;
	
	private final static int BYTES_PER_FLOAT = 4;
	private final static int BYTES_PER_SHORT = 2;
	private final static int VERTICES_PER_OBJECT = 4;
	
	private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;
	private final static int DRAW_ORDER_DATA_SIZE = 6;

	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;
	
	
	private static TextureManager tm;
	
	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];
	
	

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    
    private final int stride = COMBINED_DATA_SIZE * BYTES_PER_FLOAT;
    private final int skip = COMBINED_DATA_SIZE * VERTICES_PER_OBJECT;
    

    static float skewMatrix[] = {
    	1f,    0f, 0f, 0f,
      0.5f,    1f, 0f, 0f,
    	0f,    0f, 1f, 0f,
    	0f,    0f, 0f, 1f
    };
    
    private short drawOrder[] = { 0, 1, 2, 1, 3, 2 }; // order to draw vertices

	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	private Viewport viewport;
	private float[] viewMatrix;
	private float[] projMatrix;
	 
    // Set color with red, green, blue and alpha (opacity) values
    float normalColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    float shadowColor[] = { 0.0f, 0.0f, 0.0f, 0.2f };
    
    private int[] buffers = new int[2];

    public Drawinator(float[] vdata) {
    	
    	Matrix.setIdentityM(mMMatrix, 0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * BYTES_PER_SHORT);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder).position(0);


    	
        // initialize vertex byte buffer for shape coordinates
        
        ByteBuffer cb = ByteBuffer.allocateDirect(vdata.length * BYTES_PER_FLOAT);
        cb.order(ByteOrder.nativeOrder());
        FloatBuffer combinedBuffer = cb.asFloatBuffer();
        combinedBuffer.put(vdata);
        combinedBuffer.position(0);
        
    	// First, generate as many buffers as we need.
    	// This will give us the OpenGL handles for these buffers.
    	GLES20.glGenBuffers(2, buffers, 0);

    	// Bind to the buffer. Future commands will affect this buffer
    	// specifically.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);

    	// Transfer data from client memory to the buffer.
    	// We can release the client memory after this call.
    	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
    			combinedBuffer.capacity() * BYTES_PER_FLOAT,
    			combinedBuffer, GLES20.GL_STATIC_DRAW);
    	
    	GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			drawListBuffer.capacity() * BYTES_PER_SHORT,
    			drawListBuffer, GLES20.GL_STATIC_DRAW);
    	
    	
    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);


        
    }
    
	
    

	public void draw(List<Sprite> sprites, StandardProgram prog, Viewport vp) {
    	
		//viewport = vp;
		viewMatrix = vp.getViewMatrix();
		projMatrix = vp.getProjMatrix();
		
        // get handle to vertex shader's vPosition member
        mPositionHandle = prog.getAttributeLocation("vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        // enable handle for texture coordinates
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);


        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        
        
        // texture stuff
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        // set texture was here
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        tm.setTexture(R.drawable.all_textures);

        
    	// get handle to shape's transformation matrix
    	mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        
        
        // bind to the correct buffers
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]); // vertices and texture coordinates
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]); // draw order

        
        for (Sprite sprite : sprites) {
        	if (sprite != null) {
        		sprite.draw(this);
        	}
        }
        
    	// IMPORTANT: Unbind from the buffers when we're done with them.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }
	
	
	public void setBufferPosition(int p) {
		
    	// vertex coordinates
    	int pos = (p * skip) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE,
				GLES20.GL_FLOAT, false, stride, pos);

		// texture coordinates
    	pos = ((p * skip) + POSITION_DATA_SIZE) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
				GLES20.GL_FLOAT, false, stride, pos);

	}
	
	public void setMode(int m) {
		
		if (m == SHADOW_MODE) {
			
			this.setColor(shadowColor);
			
            Matrix.scaleM(mMMatrix, 0, 1f, SHADOW_SCALE, 1f); // half the y axis, leave x and z alone
            Matrix.multiplyMM(mMMatrix, 0, mMMatrix, 0, skewMatrix, 0);
			
		} else if (m == NORMAL_MODE) {
			
			this.setColor(normalColor);
			
		} else {
			Log.e("ffz", "unknown drawing mode " + m);
		}
		
	}
	
	public void setColor(float[] c) {
		GLES20.glUniform4fv(mColorHandle, 1, c, 0);
	}
	
	public void setDrawPosition(float x, float y) {
        Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
	}

	public void setDrawPosition(float x, float y, float z) {
        Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, z);
	}

	public void setScale(float sx, float sy) {
		Matrix.scaleM(mMMatrix, 0, sx, sy, 1);
	}
	
	public void performDraw() {
		
    	// set up the view matrix and projection matrix
    	Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
    	Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);
    	
    	// Apply the projection and view transformation
    	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
    	
    	// Draw 
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER_DATA_SIZE, GLES20.GL_UNSIGNED_SHORT, 0);
        
	}
	

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.all_textures);
		tm.loadTextures();
		
	}
    
    
}