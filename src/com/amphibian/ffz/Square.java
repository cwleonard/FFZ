package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.amphibian.ffz.geometry.ConvexPolygon;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Square {

	private static float BASE_SPEED = 0.3f;
	
	float x = 50f;
	float y = -50f;
	
	private TextureManager tm;
	
	private FrogPath fp;
	
	//private int mProgram;

//	private final String vertexShaderCode =
//			"uniform mat4 uMVPMatrix;" +
//            "attribute vec4 vPosition;" +
//			"varying vec2 v_TexCoordinate;" +
//            "attribute vec2 a_TexCoordinate;" +
//            "void main() {" +
//            	// Pass through the texture coordinate.
//            "  v_TexCoordinate = a_TexCoordinate;" +
//               // the matrix must be included as a modifier of gl_Position
//            "  gl_Position = uMVPMatrix * vPosition;" +
//            "}";
//
//	private final String fragmentShaderCode =
//		    "precision mediump float;" +
//		    "uniform vec4 vColor;" +
//		    "varying vec2 v_TexCoordinate;" +
//		    "uniform sampler2D u_Texture;" +
//		    "void main() {" +
//		    "  gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
////            "  gl_FragColor = vColor;" +
//		    "}";

	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    
    private final static int spriteCount = 4;
    private final static int spriteStride = 18;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
    	
    	-46f,  50f, 0.0f,		// frog looking at you, 92x101
    	-46f, -51f, 0.0f,
    	 46f,  50f, 0.0f,
    	-46f, -51f, 0.0f,
    	 46f, -51f, 0.0f,
    	 46f,  50f, 0.0f,

    	-50f,  50f, 0.0f,		// "normal" frog, 100x100
    	-50f, -50f, 0.0f,
    	 50f,  50f, 0.0f,
    	-50f, -50f, 0.0f,
    	 50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,

     	-50f,  50f, 0.0f,		// "normal" frog, 100x100
     	-50f, -50f, 0.0f,
     	 50f,  50f, 0.0f,
     	-50f, -50f, 0.0f,
     	 50f, -50f, 0.0f,
      	 50f,  50f, 0.0f,

     	-46f,  46f, 0.0f,		// frog looking away from you, 91x93
     	-46f, -47f, 0.0f,
     	 45f,  46f, 0.0f,
     	-46f, -47f, 0.0f,
     	 45f, -47f, 0.0f,
      	 45f,  46f, 0.0f
    
    }; 

    
    
    private final int vertexCount = (squareCoords.length / spriteCount) / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int sprite = 0;
    private int textureStride = 12;
    
	private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
	private float texture[] = {

			// frog sitting, facing down
			0.0078125f, 0.0078125f,        // left top
			0.0078125f, 0.40234375f,       // left bottom
			0.3671875f, 0.0078125f,        // right top
			0.0078125f, 0.40234375f,       // left bottom
			0.3671875f, 0.40234375f,       // right bottom
			0.3671875f, 0.0078125f,        // right top
			// frog sitting, facing left
			0.375f,    0.0078125f,
			0.375f,    0.3984375f,
			0.765625f, 0.0078125f,
			0.375f,    0.3984375f,
			0.765625f, 0.3984375f,
			0.765625f, 0.0078125f,
			// frog sitting, facing right
			0.0078125f, 0.41015625f,       // left top
			0.0078125f, 0.80078125f,       // left bottom
			0.3984375f, 0.41015625f,       // right top
			0.0078125f, 0.80078125f,       // left bottom
			0.3984375f, 0.80078125f,       // right bottom
			0.3984375f, 0.41015625f,       // right top
			// frog sitting, facing up
			0.40625f,    0.41015625f,
			0.40625f,    0.7734375f,
			0.76171875f, 0.41015625f,
			0.40625f,    0.7734375f,
			0.76171875f, 0.7734375f,
			0.76171875f, 0.41015625f
			
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

    public Square() {
    	
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
        
//        int vertexShader = FFZRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
//        int fragmentShader = FFZRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
//
//        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
//        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
//        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
//        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
        
    }
    
    public ConvexPolygon getBlocker(float x, float y) {
    	
    	float[] c = { x, y };
    	float[] p = { -50, 50, 50, 50, 50, -50, -50, -50 };
    	return new ConvexPolygon(c, p);
    	
    }
    
	public void setFrogPath(FrogPath fp) {
		this.fp = fp;
	}
	
	public float[] getMovement(long delta, float stickX, float stickY) {
		
		float[] m = new float[2];
		if (fp != null) { // this frog has a path set, follow it!
			
			float dist = delta * BASE_SPEED;
			float[] cp = new float[2];
			cp[0] = this.x;
			cp[1] = this.y;
			float[] p = fp.getNextPoint(cp, dist);
			
			//Log.i("ffz", "moving frog to " + p[0] + ", " + p[1]);
			
			//this.moveTo(p[0], p[1]);
			//this.move(p[0]-this.x, p[1]-this.y);
			m[0] = p[0]-this.x;
			m[1] = p[1]-this.y;
			
			if (p[2] == 1f) {
				fp = null;
			}
			
		} else { // we might have a joystick
			
			m[0] = delta * BASE_SPEED * stickX;
			m[1] = delta * BASE_SPEED * stickY;
			
		}
		return m;
		
	}
	
	public void update(long delta) {
		
		//TODO: update something
		
	}

	public void setDirection(float x, float y) {

		if (Math.abs(x) > Math.abs(y)) {
			if (x < 0) {
				this.faceLeft();
			}
			if (x > 0) {
				this.faceRight();
			}
		} else {
			if (y > 0) {
				this.faceUp();
			}
			if (y < 0) {
				this.faceDown();
			}
		}
		
	}
	
	public void move(float x, float y) {
    	this.x += x;
    	this.y += y;
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
    }
    
    public void moveTo(float x, float y) {
    	this.x = x;
    	this.y = y;
    	Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
    }
    
    public void grow() {
    	Matrix.scaleM(mMMatrix, 0, 1.1f, 1.1f, 0.0f);
    }
    
    public void shrink() {
    	Matrix.scaleM(mMMatrix, 0, 0.9f, 0.9f, 0.0f);
    }
    
    public void faceDown() {
    	this.sprite = 0;
    }
    
    public void faceRight() {
    	this.sprite = 2;
    }
    
    public void faceLeft() {
    	this.sprite = 1;
    }
    
    public void faceUp() {
    	this.sprite = 3;
    }

    public void draw(StandardProgram prog, Viewport vp) {
    	
    	float[] projMatrix = vp.getProjMatrix();
    	float[] viewMatrix = vp.getViewMatrix();
    	
        // get handle to vertex shader's vPosition member
        mPositionHandle = prog.getAttributeLocation("vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        vertexBuffer.position(this.sprite * spriteStride);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        // texture stuff
        
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        //tm.setTexture(R.drawable.frog_sprites);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        // Pass in the texture coordinate information
        textureBuffer.position(this.sprite * this.textureStride);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
				mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
				textureBuffer);
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        

        // translate! sorta...
        
        Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);
        
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        
        // Draw the square
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		
		//tm.add(R.drawable.frog_sprites);
		
		tm.loadTextures();
		
	}
    
    
}