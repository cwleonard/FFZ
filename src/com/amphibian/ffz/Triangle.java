package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Triangle {

	float x = 300;
	float y = -300;
	
	private int mProgram;
	
	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
               // the matrix must be included as a modifier of gl_Position
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

	private final String fragmentShaderCode = 
			"precision mediump float;" +
			"uniform vec4 vColor;" + 
			"void main() {" +
			"  gl_FragColor = vColor;" +
			"}";

	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	private FloatBuffer vertexBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
//         0.0f,  0.622008459f, 0.0f,   // top
//        -0.5f, -0.311004243f, 0.0f,   // bottom left
//         0.5f, -0.311004243f, 0.0f    // bottom right
      0.0f,  100f, 0.0f,   // top
     -200f, -100f, 0.0f,   // bottom left
      200f, -100f, 0.0f    // bottom right
    };
    
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
    	
    	Matrix.setIdentityM(mMMatrix, 0);
    	Matrix.translateM(mMMatrix, 0, x, y, 0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        
        int vertexShader = FFZRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = FFZRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables

        
    }
    
    public void move(float x, float y) {
    	Matrix.translateM(mMMatrix, 0, x, y, 0);
    }
    
    public void grow() {
    	Matrix.scaleM(mMMatrix, 0, 1.1f, 1.1f, 0.0f);
    }
    
    public void shrink() {
    	Matrix.scaleM(mMMatrix, 0, 0.9f, 0.9f, 0.0f);
    }
    
    public void draw(float[] projMatrix, float[] viewMatrix) {
    	
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // translate! sorta...
        
        Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);
        
        //Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, mMMatrix, 0);
        
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    
    
}