package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Water {

	private final static int BYTES_PER_FLOAT = 4;

	private static final int TILE_SIZE = 100;

	private Tile[][] oTiles;

	private static TextureManager tm;

	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];
	
	private boolean[][] wgrid;

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

    private short drawOrder2[] = { 0, 1, 2, 1, 3, 2, 4, 5, 6, 5, 7, 6 }; // order to draw vertices

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
    float color[] = { 0.21875f, 0.375f, 0.65625f, 0.7f };
    
    
    private int width;
    private int height;
    
    public Water(Tile[][] tiles) {

    	wgrid = new boolean[tiles.length][tiles[0].length];
    	
    	width = tiles[0].length * TILE_SIZE;
    	height = tiles.length * TILE_SIZE;
    	
    	float positions[] = {
                0f,    0f, 0f, // 0     // vertices of the square
                0f, -100f, 0f, // 1
              100f,    0f, 0f, // 2
              100f, -100f, 0f  // 3
    	};

    	float texturepos[] = {
                0f,    0f, // 0     // texture positions of the square
                1f,    0f, // 1
                0f,    1f, // 2
                1f,    1f  // 3
    	};

    	float[] alldata = new float[tiles.length * tiles[0].length * (SQUARE_DATA_SIZE + TEXTURE_STRIDE)];
    			
    	short[] indexes = new short[vertexCount * tiles.length * tiles[0].length];

    	float offsetY = 0f;
    	float offsetX = 0f;
    	
    	int doIndex = 0;
    	int index = 0;
    	int squares = 0;
		for (int i = 0; i < tiles.length; i++) {
			
			offsetX = 0f;
			
			for (int j = 0; j < tiles[i].length; j++) {
			
				if (tiles[i][j].getId() == 32 || tiles[i][j].isWater()) {
					
					wgrid[i][j] = true;
					
					//Log.d("ffz", "tile " + i + ", " + j + " is water");
					
					System.arraycopy(positions, 0, alldata, index, 3); // first coords (x, y, z);
					alldata[index++] += offsetX;
					alldata[index++] += offsetY;
					index++;

					System.arraycopy(texturepos, 0, alldata, index, 2); // first texture position
					index += 2;
					
					System.arraycopy(positions, 3, alldata, index, 3); // second coords (x, y, z);
					alldata[index++] += offsetX;
					alldata[index++] += offsetY;
					index++;

					System.arraycopy(texturepos, 2, alldata, index, 2); // second texture position
					index += 2;
					
					System.arraycopy(positions, 6, alldata, index, 3); // third coords (x, y, z);
					alldata[index++] += offsetX;
					alldata[index++] += offsetY;
					index++;
					
					System.arraycopy(texturepos, 4, alldata, index, 2); // third texture position
					index += 2;
					
					System.arraycopy(positions, 9, alldata, index, 3); // fourth coords (x, y, z);
					alldata[index++] += offsetX;
					alldata[index++] += offsetY;
					index++;
					
					System.arraycopy(texturepos,6, alldata, index, 2); // fourth texture position
					index += 2;

					
					System.arraycopy(drawOrder, 0, indexes, doIndex, vertexCount);
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
					
					squares++;
					
					
					
				}

				
				offsetX += 100f;

			}

			offsetY -= 100f;
			
		}
    	
    	
    	drawLength = indexes.length;
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	// ----------------------------------------
    	
    	Matrix.setIdentityM(mMMatrix, 0);

    	FloatBuffer everythingBuffer = ByteBuffer
    			.allocateDirect(alldata.length * BYTES_PER_FLOAT)
    			.order(ByteOrder.nativeOrder()).asFloatBuffer();
    	everythingBuffer.put(alldata).position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
        		indexes.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indexes).position(0);
    	
    	
    	
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
    
    public boolean isWater(float x, float y) {
    	
    	int xi = (int) (x / 100);
    	int yi = (int) (-y / 100);
    	if (xi < 0 || yi < 0) return false;
    	return wgrid[yi][xi];
    	
    }
    
    
    public void setTiles(Tile[][] oTiles) {
		this.oTiles = oTiles;
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
        
        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        // Set color for drawing
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        // texture stuff
        
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        tm.setTexture(R.drawable.blank);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        // start the position at -1, -1
        Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, 0, 0, -0.99f); // z index is at the very back
        
				
		GLES20.glVertexAttribPointer(mPositionHandle,
				COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, 0);

		GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
				TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, POSITION_DATA_SIZE * BYTES_PER_FLOAT);

		// translate!
		Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);


		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// Draw the square
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawLength, GLES20.GL_UNSIGNED_SHORT, 0);


    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}


	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.blank);
		tm.loadTextures();

	}
    
    
}