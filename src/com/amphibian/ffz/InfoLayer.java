package com.amphibian.ffz;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class InfoLayer {

	private final static int FLOATS_PER_UNIT = 20;

	private final static int BYTES_PER_FLOAT = 4;


	private int HEART;
	private int THERMOMETER;
	private int HYDROMETER;
	
	private static TextureManager tm;

    // Set color with red, green, blue and alpha (opacity) values
    float normalColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };

	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	// these are pointers to the buffers in the GPU where we load the vertex and texture data
	final int buffers[] = new int[2];

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    //private final static int SQUARE_DATA_SIZE = 18;
    private final static int SQUARE_DATA_SIZE = 12;
    
    
    private final int vertexCount = 6; // how many vertices does it take to draw the square?
    private final int vertexStride = COORDS_PER_VERTEX * BYTES_PER_FLOAT; // 4 bytes per vertex

    //private final static int TEXTURE_STRIDE = 12;
    private final static int TEXTURE_STRIDE = 8;
    
    private short drawOrder[] = { 0, 1, 2, 1, 3, 2 }; // order to draw vertices

    private int drawLength;
    
    //private ShortBuffer drawListBuffer;
    
    private final static int VERTICES_PER_OBJECT = 4;
    private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;
	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;

    private final int STRIDE = COMBINED_DATA_SIZE * BYTES_PER_FLOAT;
    private final int SKIP = COMBINED_DATA_SIZE * VERTICES_PER_OBJECT;


    

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** Size of the texture coordinate data in elements. */
	private final static int TEXTURE_COORD_DATA_SIZE = 2;
    private final static int TX_STRIDE = TEXTURE_COORD_DATA_SIZE * BYTES_PER_FLOAT;


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    
    public InfoLayer(Reader r) {

    	float[] alldata = this.readVertexData(r);
    			
    	
    	
    	// ----------------------------------------
    	
    	Matrix.setIdentityM(mMMatrix, 0);

    	FloatBuffer everythingBuffer = ByteBuffer
    			.allocateDirect(alldata.length * BYTES_PER_FLOAT)
    			.order(ByteOrder.nativeOrder()).asFloatBuffer();
    	everythingBuffer.put(alldata).position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
        		drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder).position(0);
    	
    	
    	
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
    			everythingBuffer.capacity() * BYTES_PER_FLOAT,
    			everythingBuffer, GLES20.GL_STATIC_DRAW);
    	
    	
    	GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			drawListBuffer.capacity() * 2,
    			drawListBuffer, GLES20.GL_STATIC_DRAW);


    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

    }
    
	private float[] readVertexData(Reader dataReader) {
		
		float[] data = {};
		try {
			
			Gson gson = new Gson();
			
			Type collectionType = new TypeToken<List<VertexDataHolder>>(){}.getType();
			List<VertexDataHolder> vList = gson.fromJson(dataReader, collectionType);			
			
			data = new float[vList.size() * FLOATS_PER_UNIT];
			
			for (int i = 0; i < vList.size(); i++) {
				
				VertexDataHolder vdh = vList.get(i);
				
				if ("heart".equalsIgnoreCase(vdh.getName())) {
					HEART = i;
				} else if ("hydrometer_demo".equalsIgnoreCase(vdh.getName())) {
					HYDROMETER = i;
				} else if ("thermometer_demo".equalsIgnoreCase(vdh.getName())) {
					THERMOMETER = i;
				}
				
				System.arraycopy(vdh.getVertexData(), 0, data, i * FLOATS_PER_UNIT, FLOATS_PER_UNIT);
				
			}

		} catch (Exception e) {
			Log.e("ffz", "vertex data read error", e);
		}
		
		return data;
		
	}

    

	public void draw(StandardProgram prog, Viewport vp) {
    	
		float[] projMatrix = vp.getProjMatrix();
		float[] viewMatrix = vp.getViewMatrix();

        // get handle to vertex shader's vPosition member
        mPositionHandle = prog.getAttributeLocation("vPosition");
        
        // set the vertext pointer to the front of the buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]); // draw order

        
        GLES20.glEnableVertexAttribArray(mPositionHandle);
//		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
//				GLES20.GL_FLOAT, false, vertexStride, 0);        
        
        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        // Set color for drawing
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        // texture stuff
        
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        tm.setTexture(R.drawable.info_textures);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        // start the position at -1, -1
        Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, 0, 0, 1.0f); // z index is at the very front
        

		/*
				
				int p = 0;//SQUARE_DATA_SIZE + ((oTiles[i][j].getId() - 1) * TEXTURE_STRIDE);
		    	int pos = (p * SKIP) * BYTES_PER_FLOAT;
				GLES20.glVertexAttribPointer(mPositionHandle,
						COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, pos);

				pos = ((p * SKIP) + POSITION_DATA_SIZE) * BYTES_PER_FLOAT;
				GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
						TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT, false,
						STRIDE, pos);

		        // translate!
		        Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
		        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);


		        // Apply the projection and view transformation
		        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		        // Draw the square
		        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, 0);

*/
		
		drawLife(vp);
		drawHydrometer(vp);
		drawThermometer(vp);
		
		        
    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.info_textures);
		tm.loadTextures();

	}
	
	private void drawLife(Viewport vp) {

		float x = 100f;

		setBufferPosition(HEART);
		setColor(normalColor);

		for (int i = 0; i < 3; i++) {

			setDrawPosition(x, -80f);

			// set up the view matrix and projection matrix (this stuff always draws in the same place,
			// no matter where the camera is looking)
			Matrix.multiplyMM(mvpMatrix, 0, vp.getProjMatrix(), 0, mMMatrix, 0);

			// Apply the projection and view transformation
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

			// Draw 
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, 0);
			
			x += 60f;

		}

		
	}
	

	private void drawHydrometer(Viewport vp) {

		setBufferPosition(HYDROMETER);
		setColor(normalColor);

		setDrawPosition(1810f, -130f);

		// set up the view matrix and projection matrix (this stuff always draws in the same place,
		// no matter where the camera is looking)
		Matrix.multiplyMM(mvpMatrix, 0, vp.getProjMatrix(), 0, mMMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// Draw 
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, 0);
			

		
	}
	

	private void drawThermometer(Viewport vp) {

		setBufferPosition(THERMOMETER);
		setColor(normalColor);

		setDrawPosition(1730f, -130f);

		// set up the view matrix and projection matrix (this stuff always draws in the same place,
		// no matter where the camera is looking)
		Matrix.multiplyMM(mvpMatrix, 0, vp.getProjMatrix(), 0, mMMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// Draw 
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, 0);
			

		
	}

	public void setColor(float[] c) {
		GLES20.glUniform4fv(mColorHandle, 1, c, 0);
	}
	
	public void setDrawPosition(float x, float y) {
        Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
	}

	private void setBufferPosition(int p) {
		
    	// vertex coordinates
    	int pos = (p * SKIP) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE,
				GLES20.GL_FLOAT, false, STRIDE, pos);

		// texture coordinates
    	pos = ((p * SKIP) + POSITION_DATA_SIZE) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
				GLES20.GL_FLOAT, false, STRIDE, pos);

	}

    
}