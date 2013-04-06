package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Drawinator {

	private final static float SHADOW_SCALE = 0.7f;
	
	private final static int BYTES_PER_FLOAT = 4;
	private final static int VERTICES_PER_OBJECT = 6;
	
	private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;

	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;
	
	private List<? extends Sprite> stuff;
	
	private static TextureManager tm;
	
	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	private FloatBuffer combinedBuffer;
	
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    
    private final int stride = COMBINED_DATA_SIZE * BYTES_PER_FLOAT;
    private final int skip = COMBINED_DATA_SIZE * VERTICES_PER_OBJECT;
    
//            ctx.transform(1, 0, sx, 1, 0, 0);

    static float skewMatrix[] = {
    	1f, -0.5f, 0f,
    	0f,    1f, 0f,
    	0f,    0f, 1f
    };
    
    
    static float combinedData[] = {

    	// jumping_left_1
    	-68f, 53f, 0.0f,   // vertex 0
    	0.00390625f, 0.001953125f, // texture 0
    	-68f, -53f, 0.0f,  // vertex 1
    	0.00390625f, 0.10546875f, // texture 1
    	68f, 53f, 0.0f,  // vertex 2
    	0.26953125f, 0.001953125f, // texture 2
    	-68f, -53f, 0.0f,  // vertex 3
    	0.00390625f, 0.10546875f, // texture 3
    	68f, -53f, 0.0f,  // vertex 4
    	0.26953125f, 0.10546875f, // texture 4
    	68f, 53f, 0.0f,  // vertex 5
    	0.26953125f, 0.001953125f,  // texture 5


    	// jumping_left_2
    	-68.5f, 48f, 0.0f,   // vertex 0
    	0.2734375f, 0.001953125f, // texture 0
    	-68.5f, -48f, 0.0f,  // vertex 1
    	0.2734375f, 0.095703125f, // texture 1
    	68.5f, 48f, 0.0f,  // vertex 2
    	0.541015625f, 0.001953125f, // texture 2
    	-68.5f, -48f, 0.0f,  // vertex 3
    	0.2734375f, 0.095703125f, // texture 3
    	68.5f, -48f, 0.0f,  // vertex 4
    	0.541015625f, 0.095703125f, // texture 4
    	68.5f, 48f, 0.0f,  // vertex 5
    	0.541015625f, 0.001953125f,  // texture 5


    	// jumping_right_1
    	-68f, 53f, 0.0f,   // vertex 0
    	0.544921875f, 0.001953125f, // texture 0
    	-68f, -53f, 0.0f,  // vertex 1
    	0.544921875f, 0.10546875f, // texture 1
    	68f, 53f, 0.0f,  // vertex 2
    	0.810546875f, 0.001953125f, // texture 2
    	-68f, -53f, 0.0f,  // vertex 3
    	0.544921875f, 0.10546875f, // texture 3
    	68f, -53f, 0.0f,  // vertex 4
    	0.810546875f, 0.10546875f, // texture 4
    	68f, 53f, 0.0f,  // vertex 5
    	0.810546875f, 0.001953125f,  // texture 5


    	// jumping_right_2
    	-68.5f, 48f, 0.0f,   // vertex 0
    	0.00390625f, 0.107421875f, // texture 0
    	-68.5f, -48f, 0.0f,  // vertex 1
    	0.00390625f, 0.201171875f, // texture 1
    	68.5f, 48f, 0.0f,  // vertex 2
    	0.271484375f, 0.107421875f, // texture 2
    	-68.5f, -48f, 0.0f,  // vertex 3
    	0.00390625f, 0.201171875f, // texture 3
    	68.5f, -48f, 0.0f,  // vertex 4
    	0.271484375f, 0.201171875f, // texture 4
    	68.5f, 48f, 0.0f,  // vertex 5
    	0.271484375f, 0.107421875f,  // texture 5


    	// pine_tree
    	-100f, 146f, 0.0f,   // vertex 0
    	0.275390625f, 0.107421875f, // texture 0
    	-100f, -146f, 0.0f,  // vertex 1
    	0.275390625f, 0.392578125f, // texture 1
    	100f, 146f, 0.0f,  // vertex 2
    	0.666015625f, 0.107421875f, // texture 2
    	-100f, -146f, 0.0f,  // vertex 3
    	0.275390625f, 0.392578125f, // texture 3
    	100f, -146f, 0.0f,  // vertex 4
    	0.666015625f, 0.392578125f, // texture 4
    	100f, 146f, 0.0f,  // vertex 5
    	0.666015625f, 0.107421875f,  // texture 5


    	// rock
    	-50f, 40.5f, 0.0f,   // vertex 0
    	0.669921875f, 0.107421875f, // texture 0
    	-50f, -40.5f, 0.0f,  // vertex 1
    	0.669921875f, 0.1865234375f, // texture 1
    	50f, 40.5f, 0.0f,  // vertex 2
    	0.865234375f, 0.107421875f, // texture 2
    	-50f, -40.5f, 0.0f,  // vertex 3
    	0.669921875f, 0.1865234375f, // texture 3
    	50f, -40.5f, 0.0f,  // vertex 4
    	0.865234375f, 0.1865234375f, // texture 4
    	50f, 40.5f, 0.0f,  // vertex 5
    	0.865234375f, 0.107421875f,  // texture 5


    	// sitting_down
    	-46f, 50.5f, 0.0f,   // vertex 0
    	0.00390625f, 0.39453125f, // texture 0
    	-46f, -50.5f, 0.0f,  // vertex 1
    	0.00390625f, 0.4931640625f, // texture 1
    	46f, 50.5f, 0.0f,  // vertex 2
    	0.18359375f, 0.39453125f, // texture 2
    	-46f, -50.5f, 0.0f,  // vertex 3
    	0.00390625f, 0.4931640625f, // texture 3
    	46f, -50.5f, 0.0f,  // vertex 4
    	0.18359375f, 0.4931640625f, // texture 4
    	46f, 50.5f, 0.0f,  // vertex 5
    	0.18359375f, 0.39453125f,  // texture 5


    	// sitting_left
    	-50f, 50f, 0.0f,   // vertex 0
    	0.1875f, 0.39453125f, // texture 0
    	-50f, -50f, 0.0f,  // vertex 1
    	0.1875f, 0.4921875f, // texture 1
    	50f, 50f, 0.0f,  // vertex 2
    	0.3828125f, 0.39453125f, // texture 2
    	-50f, -50f, 0.0f,  // vertex 3
    	0.1875f, 0.4921875f, // texture 3
    	50f, -50f, 0.0f,  // vertex 4
    	0.3828125f, 0.4921875f, // texture 4
    	50f, 50f, 0.0f,  // vertex 5
    	0.3828125f, 0.39453125f,  // texture 5


    	// sitting_right
    	-50f, 50f, 0.0f,   // vertex 0
    	0.38671875f, 0.39453125f, // texture 0
    	-50f, -50f, 0.0f,  // vertex 1
    	0.38671875f, 0.4921875f, // texture 1
    	50f, 50f, 0.0f,  // vertex 2
    	0.58203125f, 0.39453125f, // texture 2
    	-50f, -50f, 0.0f,  // vertex 3
    	0.38671875f, 0.4921875f, // texture 3
    	50f, -50f, 0.0f,  // vertex 4
    	0.58203125f, 0.4921875f, // texture 4
    	50f, 50f, 0.0f,  // vertex 5
    	0.58203125f, 0.39453125f,  // texture 5


    	// sitting_up
    	-45.5f, 46.5f, 0.0f,   // vertex 0
    	0.5859375f, 0.39453125f, // texture 0
    	-45.5f, -46.5f, 0.0f,  // vertex 1
    	0.5859375f, 0.4853515625f, // texture 1
    	45.5f, 46.5f, 0.0f,  // vertex 2
    	0.763671875f, 0.39453125f, // texture 2
    	-45.5f, -46.5f, 0.0f,  // vertex 3
    	0.5859375f, 0.4853515625f, // texture 3
    	45.5f, -46.5f, 0.0f,  // vertex 4
    	0.763671875f, 0.4853515625f, // texture 4
    	45.5f, 46.5f, 0.0f,  // vertex 5
    	0.763671875f, 0.39453125f,  // texture 5


    	// tall_grass
    	-75f, 73f, 0.0f,   // vertex 0
    	0.00390625f, 0.4951171875f, // texture 0
    	-75f, -73f, 0.0f,  // vertex 1
    	0.00390625f, 0.6376953125f, // texture 1
    	75f, 73f, 0.0f,  // vertex 2
    	0.296875f, 0.4951171875f, // texture 2
    	-75f, -73f, 0.0f,  // vertex 3
    	0.00390625f, 0.6376953125f, // texture 3
    	75f, -73f, 0.0f,  // vertex 4
    	0.296875f, 0.6376953125f, // texture 4
    	75f, 73f, 0.0f,  // vertex 5
    	0.296875f, 0.4951171875f,  // texture 5


    	// tree1
    	-175f, 246.5f, 0.0f,   // vertex 0
    	0.30078125f, 0.4951171875f, // texture 0
    	-175f, -246.5f, 0.0f,  // vertex 1
    	0.30078125f, 0.9765625f, // texture 1
    	175f, 246.5f, 0.0f,  // vertex 2
    	0.984375f, 0.4951171875f, // texture 2
    	-175f, -246.5f, 0.0f,  // vertex 3
    	0.30078125f, 0.9765625f, // texture 3
    	175f, -246.5f, 0.0f,  // vertex 4
    	0.984375f, 0.9765625f, // texture 4
    	175f, 246.5f, 0.0f,  // vertex 5
    	0.984375f, 0.4951171875f  // texture 5

    	
    };
    
	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	 
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    float shadowColor[] = { 0.0f, 0.0f, 0.0f, 0.2f };
    

    public Drawinator() {
    	
    	Matrix.setIdentityM(mMMatrix, 0);
        
        // initialize vertex byte buffer for shape coordinates
        
        ByteBuffer cb = ByteBuffer.allocateDirect(combinedData.length * BYTES_PER_FLOAT);
        cb.order(ByteOrder.nativeOrder());
        combinedBuffer = cb.asFloatBuffer();
        combinedBuffer.put(combinedData);
        combinedBuffer.position(0);

        
    }
    
	public void setStuff(List<? extends Sprite> stuff) {
		this.stuff = stuff;
	}
	
    @SuppressWarnings("unchecked")
	public List<Sprite> getStuff() {
		return (List<Sprite>) stuff;
	}

	public void draw(StandardProgram prog, Viewport vp) {
    	
    	float[] projMatrix = vp.getProjMatrix();
    	float[] viewMatrix = vp.getViewMatrix();
    	
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
        
        Collections.sort(stuff, new Comparator<Sprite>() {

			@Override
			public int compare(Sprite lhs, Sprite rhs) {
				float lhsBottom = lhs.getDrawY() + combinedData[(lhs.getTexturePosition()*skip)+6];
				float rhsBottom = rhs.getDrawY() + combinedData[(rhs.getTexturePosition()*skip)+6];
				if (rhsBottom - lhsBottom < 0f) {
					return -1;
				} else if (rhsBottom - lhsBottom > 0f) {
					return 1;
				} else {
					return 0;
				}
			}
        	
        });
        for (Sprite sprite : stuff) {

        	// coordinates
        	combinedBuffer.position(sprite.getTexturePosition() * skip);
            GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE,
                                         GLES20.GL_FLOAT, false,
                                         stride, combinedBuffer);
        	
            // textures
			combinedBuffer.position((sprite.getTexturePosition() * skip) + POSITION_DATA_SIZE);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
                                         GLES20.GL_FLOAT, false,
                                         stride, combinedBuffer);

            // draw shadow? maybe? ========================================================
            
            // Set color for drawing
            GLES20.glUniform4fv(mColorHandle, 1, shadowColor, 0);

            Matrix.setIdentityM(mMMatrix, 0);

            
            
        	Matrix.translateM(mMMatrix, 0, sprite.getDrawX(), sprite.getDrawY()+(combinedData[(sprite.getTexturePosition()*skip)+6]*(1f-SHADOW_SCALE)), 0);

            Matrix.scaleM(mMMatrix, 0, 1f, SHADOW_SCALE, 1f); // half the y axis, leave x and z alone
            //Matrix.multiplyMM(mMMatrix, 0, skewMatrix, 0, mMMatrix, 0);
            //Matrix.rotateM(mMMatrix, 0, 25f, 0, 0, 0);
            
        	
        	Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        	Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);


        	// Apply the projection and view transformation
        	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        	// Draw 
        	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTICES_PER_OBJECT);

        	
        	
        
            // draw normal thing =============================================================
            
            // Set color for drawing
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);

            
        	// move to the correct location

            Matrix.setIdentityM(mMMatrix, 0);
        	Matrix.translateM(mMMatrix, 0, sprite.getDrawX(), sprite.getDrawY(), 0);
        	
        	Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        	Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);


        	// Apply the projection and view transformation
        	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        	// Draw 
        	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTICES_PER_OBJECT);

        }
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.all_textures);
		tm.loadTextures();
		
	}
    
    
}