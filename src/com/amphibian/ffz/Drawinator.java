package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Drawinator {

	final static float SHADOW_SCALE = 0.7f;
	
	private final static int BYTES_PER_FLOAT = 4;
	private final static int VERTICES_PER_OBJECT = 4;
	
	private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;

	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;
	
	private List<? extends Sprite> stuff;
	
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
    	1f, -0.5f, 0f,
    	0f,    1f, 0f,
    	0f,    0f, 1f
    };
    
    private short drawOrder[] = { 0, 1, 2, 1, 3, 2 }; // order to draw vertices

    private ShortBuffer drawListBuffer;

	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	private Viewport viewport;
	 
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    float shadowColor[] = { 0.0f, 0.0f, 0.0f, 0.2f };
    
    private int[] buffers = new int[2];

    public Drawinator(float[] vdata) {
    	
    	Matrix.setIdentityM(mMMatrix, 0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
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

    	// Transfer data from client memory to the buffer.
    	// We can release the client memory after this call.
    	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
    			combinedBuffer.capacity() * BYTES_PER_FLOAT,
    			combinedBuffer, GLES20.GL_STATIC_DRAW);
    	
    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        
    }
    
	public void setStuff(List<? extends Sprite> stuff) {
		this.stuff = stuff;
	}
	
    @SuppressWarnings("unchecked")
	public List<Sprite> getStuff() {
		return (List<Sprite>) stuff;
	}
    
    public ConvexPolygon[] getBlockers() {
    	
    	ConvexPolygon[] blockers = new ConvexPolygon[stuff.size()];
    	Iterator<? extends Sprite> i = stuff.iterator();
    	int b = 0;
    	while (i.hasNext()) {
    		
    		Sprite s = i.next();
    		
    		if (s.getBufferIndex() == 4) {
        		float[] c = {s.getDrawX() - 5, s.getDrawY() - 124};
        		float[] p = {
        				-25,  21,
        				 25,  21,
        				 24, -18,
        				-25, -18 
        		};
        		blockers[b] = new ConvexPolygon(c, p);

			} else if (s.getBufferIndex() == 5) {
				
				float[] c = { s.getDrawX(), s.getDrawY() - 15f };
				float[] p = {
						-48, 18.5f,
						48, 19.5f,
						43, -22.5f,
						-41, -21.5f 
				};
				blockers[b] = new ConvexPolygon(c, p);

				// (-48, -18.5) , (48, -19.5) , (43, 22.5) , (-41, 21.5)

			} else {
        		float[] c = {s.getDrawX(), s.getDrawY()};
        		float[] p = {
        				-50,  50,
        				 50,  50,
        				 50, -50,
        				-50, -50};
        		blockers[b] = new ConvexPolygon(c, p);
    			
    		}
    		
    		
    		
    		b++;
    		
    	}
    	return blockers;
    	
    }

	public void draw(StandardProgram prog, Viewport vp) {
    	
		viewport = vp;
		
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
        
        
        // bind to the correct buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);

        
        Collections.sort(stuff, new Comparator<Sprite>() {

			@Override
			public int compare(Sprite lhs, Sprite rhs) {
//				float lhsBottom = lhs.getDrawY() + combinedData[(lhs.getBufferIndex()*skip)+6];
//				float rhsBottom = rhs.getDrawY() + combinedData[(rhs.getBufferIndex()*skip)+6];
				float lhsBottom = lhs.getBottom();
				float rhsBottom = rhs.getBottom();
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

        	setBufferPosition(sprite.getBufferIndex());

    		
            // draw shadow? maybe? ========================================================
            
            // Set color for drawing
            GLES20.glUniform4fv(mColorHandle, 1, shadowColor, 0);

//        	setDrawPosition(sprite.getDrawX(), sprite.getDrawY()+(combinedData[(sprite.getBufferIndex()*skip)+6]*(1f-SHADOW_SCALE)));
        	setDrawPosition(sprite.getDrawX(), sprite.getShadowY());

            Matrix.scaleM(mMMatrix, 0, 1f, SHADOW_SCALE, 1f); // half the y axis, leave x and z alone
            

        	// we're all set up, draw it
        	performDraw();

        	
        
            // draw normal thing =============================================================
            
            // Set color for drawing
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);

            sprite.draw(this);
        	
        }
        
    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }
	
	
	public void setBufferPosition(int p) {
		
		//Log.i("ffz", "setting buffer position " + p);
		
    	// vertex coordinates
    	int pos = (p * skip) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE,
				GLES20.GL_FLOAT, false, stride, pos);

		// texture coordinates
    	pos = ((p * skip) + POSITION_DATA_SIZE) * BYTES_PER_FLOAT;
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
				GLES20.GL_FLOAT, false, stride, pos);

	}
	
	public void setDrawPosition(float x, float y) {
        Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
	}
	
	public void performDraw() {
		
    	// set up the view matrix and projection matrix
    	Matrix.multiplyMM(eyeMatrix, 0, viewport.getViewMatrix(), 0, mMMatrix, 0);
    	Matrix.multiplyMM(mvpMatrix, 0, viewport.getProjMatrix(), 0, eyeMatrix, 0);
    	
    	// Apply the projection and view transformation
    	GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
    	
    	// Draw 
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        
	}
	

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.all_textures);
		tm.loadTextures();
		
	}
    
    
}