package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Obstacles {

	float x = 50f;
	float y = -50f;
	
	private Obstacle[] stuff;
	
	private static TextureManager tm;
	
	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    
    private final static int spriteCount = 13;
    private final static int spriteStride = 18;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
    	
    	-28.75f,  50f, 0.0f,		// flower, 57.5x100
    	-28.75f, -50f, 0.0f,
    	 28.75f,  50f, 0.0f,
    	-28.75f, -50f, 0.0f,
    	 28.75f, -50f, 0.0f,
     	 28.75f,  50f, 0.0f,

     	-50f,  50f, 0.0f,		// placeholder, 100x100
    	-50f, -50f, 0.0f,
    	 50f,  50f, 0.0f,
    	-50f, -50f, 0.0f,
    	 50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

       	-50f,  50f, 0.0f,		// placeholder, 100x100
      	-50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,
      	-50f, -50f, 0.0f,
      	 50f, -50f, 0.0f,
       	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

      	-50f,  50f, 0.0f,		// placeholder, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

     	-100f,  146.25f, 0.0f,			// pine tree, 200x292.5
     	-100f, -146.25f, 0.0f,
     	 100f,  146.25f, 0.0f,
     	-100f, -146.25f, 0.0f,
     	 100f, -146.25f, 0.0f,
      	 100f,  146.25f, 0.0f,

     	-132.5f,  148.75f, 0.0f,		// deciduous tree, 265x297.5
     	-132.5f, -148.75f, 0.0f,
     	 132.5f,  148.75f, 0.0f,
     	-132.5f, -148.75f, 0.0f,
     	 132.5f, -148.75f, 0.0f,
      	 132.5f,  148.75f, 0.0f
    
    }; 

    
    
    private final int vertexCount = (squareCoords.length / spriteCount) / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int sprite = 0;
    private int textureStride = 12;
    
	private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
	private float texture[] = {

			// flower
			0.0078125f, 0.0078125f,		// left top
			0.0078125f, 0.1640625f,		// left bottom
			0.09765625f, 0.0078125f,	// right top
			0.0078125f, 0.1640625f,		// left bottom
			0.09765625f, 0.1640625f,	// right bottom
			0.09765625f, 0.0078125f,	// right top

			// fence_top_right
			0.10546875f, 0.0078125f,
			0.10546875f, 0.2421875f,		// 1
			0.26171875f, 0.0078125f,
			0.10546875f, 0.2421875f,
			0.26171875f, 0.2421875f,
			0.26171875f, 0.0078125f,

			// tall_grass
			0.26953125f, 0.0078125f,		// 2
			0.26953125f, 0.2421875f,
			0.42578125f, 0.0078125f,
			0.26953125f, 0.2421875f,
			0.42578125f, 0.2421875f,
			0.42578125f, 0.0078125f,

			// rock
			0.43359375f, 0.0078125f,		// 3
			0.43359375f, 0.1640625f,
			0.58984375f, 0.0078125f,
			0.43359375f, 0.1640625f,
			0.58984375f, 0.1640625f,
			0.58984375f, 0.0078125f,

			// fence_right
			0.59765625f, 0.0078125f,		// 4
			0.59765625f, 0.2421875f,
			0.75390625f, 0.0078125f,
			0.59765625f, 0.2421875f,
			0.75390625f, 0.2421875f,
			0.75390625f, 0.0078125f,

			// fence_top_left
			0.76171875f, 0.0078125f,		// 5
			0.76171875f, 0.2421875f,
			0.91796875f, 0.0078125f,
			0.76171875f, 0.2421875f,
			0.91796875f, 0.2421875f,
			0.91796875f, 0.0078125f,

			// fence__bottom_left
			0.0078125f, 0.25f,				// 6
			0.0078125f, 0.484375f,
			0.1640625f, 0.25f,
			0.0078125f, 0.484375f,
			0.1640625f, 0.484375f,
			0.1640625f, 0.25f,

			// fence_bottom_right
			0.171875f, 0.25f,				// 7
			0.171875f, 0.484375f,
			0.328125f, 0.25f,
			0.171875f, 0.484375f,
			0.328125f, 0.484375f,
			0.328125f, 0.25f,

			// fence_left
			0.3359375f, 0.25f,				// 8
			0.3359375f, 0.484375f,
			0.4921875f, 0.25f,
			0.3359375f, 0.484375f,
			0.4921875f, 0.484375f,
			0.4921875f, 0.25f,

			// fence_middle_middle
			0.5f, 0.25f,					// 9
			0.5f, 0.484375f,
			0.65625f, 0.25f,
			0.5f, 0.484375f,
			0.65625f, 0.484375f,
			0.65625f, 0.25f,

			// fence_middle
			0.6640625f, 0.25f,				// 10
			0.6640625f, 0.484375f,
			0.8203125f, 0.25f,
			0.6640625f, 0.484375f,
			0.8203125f, 0.484375f,
			0.8203125f, 0.25f,

			// pine_tree
			0.0078125f, 0.4921875f,			// 11
			0.0078125f, 0.94921875f,
			0.3203125f, 0.4921875f,
			0.0078125f, 0.94921875f,
			0.3203125f, 0.94921875f,
			0.3203125f, 0.4921875f,

			// tree
			0.328125f, 0.4921875f,			// 12
			0.328125f, 0.95703125f,
			0.7421875f, 0.4921875f,
			0.328125f, 0.95703125f,
			0.7421875f, 0.95703125f,
			0.7421875f, 0.4921875f
			
	};

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;
	 
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public Obstacles() {
    	
    	Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
    	
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(squareCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        textureBuffer = ByteBuffer.allocateDirect(texture.length * 4)
        		.order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(texture).position(0);
        
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
        
    }
    
    public Obstacle[] getStuff() {
		return stuff;
	}

	public void setStuff(Obstacle[] stuff) {
		this.stuff = stuff;
	}

    
	public void update(long delta) {
		
		
	}

	
    public void draw(StandardProgram prog, Viewport vp) {
    	
    	float[] projMatrix = vp.getProjMatrix();
    	float[] viewMatrix = vp.getViewMatrix();
    	
        // get handle to vertex shader's vPosition member
        mPositionHandle = prog.getAttributeLocation("vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        // Set color for drawing
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        // texture stuff
        
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        //tm.setTexture(R.drawable.objects);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        
        
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        
        for (int i = 0; i < stuff.length; i++) {

            // Prepare the coordinate data
            vertexBuffer.position(stuff[i].getId() * spriteStride);
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                         GLES20.GL_FLOAT, false,
                                         vertexStride, vertexBuffer);

            // set the texture position
            textureBuffer.position(stuff[i].getId() * this.textureStride);
    		GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
    				mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
    				textureBuffer);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        	
        	// move to the correct location

            //float z = 1 - (Math.abs(stuff[i].getY()) / 1080f);
            
            Matrix.setIdentityM(mMMatrix, 0);
        	Matrix.translateM(mMMatrix, 0, stuff[i].getX(), stuff[i].getY(), 0);
        	
        	Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        	Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);

        	// get handle to shape's transformation matrix
        	mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        	// Apply the projection and view transformation
        	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        	// Draw 
        	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        }
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		//tm.add(R.drawable.objects);
		tm.loadTextures();
		
	}
    
    
}