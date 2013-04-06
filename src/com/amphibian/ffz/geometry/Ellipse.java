package com.amphibian.ffz.geometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Ellipse {

	private float _vertices[];
    private FloatBuffer _vertex_buffer;
    private int _segments;

    public Ellipse(int segments, float width, float height) {
        _vertices = new float[segments*2];
        _segments = segments;

        int count = 0;
        for (float i = 0; i < 360.0f; i += (360.0f/_segments)) {
            _vertices[count++] = (float)Math.cos(Math.PI/180.0f * i)*width;
            _vertices[count++] = (float)Math.sin(Math.PI/180.0f * i)*height;
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(_vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertex_buffer = vbb.asFloatBuffer();
        _vertex_buffer.put(_vertices);
        _vertex_buffer.position(0);
    }

//    public void draw(GL10 gl) {
//        gl.glFrontFace(GL10.GL_CW);
//
//        gl.glVertexPointer(2, gl.GL_FLOAT, 0, _vertex_buffer);
//        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//
//        gl.glDrawArrays(gl.GL_LINE_LOOP, 0, _segments);
//
//        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//    }
}
