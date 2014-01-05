package com.amphibian.ffz.engine.layers;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.amphibian.ffz.App;
import com.amphibian.ffz.R;
import com.amphibian.ffz.engine.Viewport;
import com.amphibian.ffz.engine.sprite.Frame;
import com.amphibian.ffz.engine.sprite.FrameDataManager;
import com.amphibian.ffz.engine.sprite.Frog;
import com.amphibian.ffz.engine.sprite.Rabbit;
import com.amphibian.ffz.engine.sprite.Sprite;
import com.amphibian.ffz.engine.util.CollisionDataHolder;
import com.amphibian.ffz.engine.util.VertexDataHolder;
import com.amphibian.ffz.opengl.StandardProgram;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SpriteLayer {

	public static final int SHADOW_MODE = 0;
	public static final int NORMAL_MODE = 1;
	
	public final static float SHADOW_SCALE = 0.7f;
	
	private final static int BYTES_PER_FLOAT = 4;
	private final static int BYTES_PER_SHORT = 2;
	private final static int VERTICES_PER_OBJECT = 4;
	
	private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;
	private final static int DRAW_ORDER_DATA_SIZE = 6;

	private final static int FLOATS_PER_UNIT = 20;

	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;
	
	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];
	
	private final static int texture = R.drawable.all_textures;

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
	 
	private float[] viewMatrix;
	private float[] projMatrix;
	 
    // Set color with red, green, blue and alpha (opacity) values
    float normalColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    float shadowColor[] = { 0.0f, 0.0f, 0.0f, 0.2f };
    
    private int[] buffers = new int[2];

    public SpriteLayer() {
    	
    	this.readVertexData();
    	
    	Frog.init();
    	Rabbit.init();
    	
    }
    	
    private void readVertexData() {

    	FrameDataManager fdm = FrameDataManager.getInstance();

    	float[] data = {};
    	try {

    		Gson gson = new Gson();

    		Type collectionType = new TypeToken<List<VertexDataHolder>>() {
    		}.getType();
    		List<VertexDataHolder> vList = gson.fromJson(new InputStreamReader(
    				App.getContext().getResources().openRawResource(R.raw.frames)),
    				collectionType);

    		data = new float[vList.size() * FLOATS_PER_UNIT];

    		for (int i = 0; i < vList.size(); i++) {

    			VertexDataHolder vdh = vList.get(i);

    			Frame f = new Frame();
    			f.setName(vdh.getName());
    			f.setIndex(i);

    			float[] vdata = vdh.getVertexData();
    			float width = vdata[10] - vdata[0];
    			float height = vdata[1] - vdata[16];

    			f.setHeight(height);
    			f.setWidth(width);

    			System.arraycopy(vdh.getVertexData(), 0, data, i
    					* FLOATS_PER_UNIT, FLOATS_PER_UNIT);

    			fdm.putFrame(f);

    		}

    		collectionType = new TypeToken<List<CollisionDataHolder>>() {
    		}.getType();
    		List<CollisionDataHolder> cList = gson
    				.fromJson(new InputStreamReader(App.getContext().getResources()
    						.openRawResource(R.raw.collisions)), collectionType);

    		for (int i = 0; i < cList.size(); i++) {

    			CollisionDataHolder cdh = cList.get(i);
    			if (cdh != null) {
    				fdm.putCollider(cdh);
    			}

    		}

    	} catch (Exception e) {
    		Log.e("ffz", "vertex data read error", e);
    	}

    	glInit(data);

    }
    	
    
    
    private void  glInit(float[] vdata) {
    	
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
    
    public int[] getTextures() {
    	return new int[] { texture };
    }

	public void draw(List<Sprite> sprites, StandardProgram prog, Viewport vp) {
    	
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

        prog.useTexture(texture);
        
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

	public void performDraw(float[] oMatrix) {
		
    	// set up the view matrix and projection matrix
    	Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, oMatrix, 0);
    	Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);
    	
    	// Apply the projection and view transformation
    	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
    	
    	// Draw 
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER_DATA_SIZE, GLES20.GL_UNSIGNED_SHORT, 0);
        
	}

}