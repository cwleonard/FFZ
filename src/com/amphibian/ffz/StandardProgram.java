package com.amphibian.ffz;

import android.opengl.GLES20;

public class StandardProgram {

	private int mProgram;

	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
			"varying vec2 v_TexCoordinate;" +
            "attribute vec2 a_TexCoordinate;" +
            "void main() {" +
            	// Pass through the texture coordinate.
            "  v_TexCoordinate = a_TexCoordinate;" +
               // the matrix must be included as a modifier of gl_Position
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

	private final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform vec4 vColor;" +
		    "varying vec2 v_TexCoordinate;" +
		    "uniform sampler2D u_Texture;" +
		    "void main() {" +
		    "  gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
//            "  gl_FragColor = vColor;" +
		    "}";

	
	public StandardProgram() {
		
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
		
	}
	
	public void enable() {
		GLES20.glUseProgram(mProgram);
	}
	
	public void disable() {
		// nothing?
	}
	
	public int getAttributeLocation(String a) {
		return GLES20.glGetAttribLocation(mProgram, a);
	}
	
	public int getUniformLocation(String u) {
		return GLES20.glGetUniformLocation(mProgram, u);
	}
	
	public int loadShader(int type, String shaderCode){

	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	    
	}
	
	
	
}
