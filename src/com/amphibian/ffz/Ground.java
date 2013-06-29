package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Ground {

	private final static int BYTES_PER_FLOAT = 4;

	private static final int TILE_SIZE = 100;

	private Tile[][] oTiles;

	private static TextureManager tm;

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

    private short drawOrder2[] = { 0, 1, 2, 1, 3, 2, 4, 5, 6, 5, 7, 6 }; // order to draw vertices

    private int drawLength;
    
    //private ShortBuffer drawListBuffer;
    
    private final static int VERTICES_PER_OBJECT = 4;
    private final static int POSITION_DATA_SIZE = 3;
	private final static int TEXTURE_COORDINATE_DATA_SIZE = 2;
	private final static int COMBINED_DATA_SIZE = POSITION_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE;

    private final int STRIDE = COMBINED_DATA_SIZE * BYTES_PER_FLOAT;
    private final int SKIP = COMBINED_DATA_SIZE * VERTICES_PER_OBJECT;


    private float whatnot[] = {
    		
            0f, 100f, 0f, // 0     // vertices of the square
    		0.5f, 0.16796875f,				// 1
            0f,   0f, 0f, // 1
    		0.5f, 0.24609375f,
          100f, 100f, 0f, // 2
  		0.65625f, 0.16796875f,
          100f,   0f, 0f, // 3
  		0.65625f, 0.24609375f,

  		
        100f, 100f, 0f, // 0     // vertices of the square
        0.3359375f, 0.16796875f,				// 1
        100f,   0f, 0f, // 1
        0.3359375f, 0.24609375f,
        200f, 100f, 0f, // 2
        0.4921875f, 0.16796875f,
        200f,   0f, 0f, // 3
        0.4921875f, 0.24609375f

    		
    };
    
	//private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
	private float everything[] = {

                0f, 100f, 0f, // 0     // vertices of the square
                0f,   0f, 0f, // 1
              100f, 100f, 0f, // 2
              100f,   0f, 0f, // 3

			// grass
			0.3359375f, 0.16796875f,		// 0
			0.3359375f, 0.24609375f,
			0.4921875f, 0.16796875f,
			//0.3359375f, 0.24609375f,
			0.4921875f, 0.24609375f,
			//0.4921875f, 0.16796875f,

			// grass2
			0.5f, 0.16796875f,				// 1
			0.5f, 0.24609375f,
			0.65625f, 0.16796875f,
			//0.5f, 0.24609375f,
			0.65625f, 0.24609375f,
			//0.65625f, 0.16796875f,


			// dirt_edge_bottom
			0.171875f, 0.00390625f,			// 2
			0.171875f, 0.08203125f,
			0.328125f, 0.00390625f,
			//0.171875f, 0.08203125f,
			0.328125f, 0.08203125f,
			//0.328125f, 0.00390625f,

			// dirt_edge_bottom_left
			0.3359375f, 0.00390625f,		// 3
			0.3359375f, 0.08203125f,
			0.4921875f, 0.00390625f,
			//0.3359375f, 0.08203125f,
			0.4921875f, 0.08203125f,
			//0.4921875f, 0.00390625f,

			// dirt_edge_bottom_right
			0.5f, 0.00390625f,				// 4
			0.5f, 0.08203125f,
			0.65625f, 0.00390625f,
			//0.5f, 0.08203125f,
			0.65625f, 0.08203125f,
			//0.65625f, 0.00390625f,

			// dirt_edge_left
			0.6640625f, 0.00390625f,		// 5
			0.6640625f, 0.08203125f,
			0.8203125f, 0.00390625f,
			//0.6640625f, 0.08203125f,
			0.8203125f, 0.08203125f,
			//0.8203125f, 0.00390625f,

			// dirt
			0.0078125f, 0.00390625f,		// 6
			0.0078125f, 0.08203125f,
			0.1640625f, 0.00390625f,
			//0.0078125f, 0.08203125f,
			0.1640625f, 0.08203125f,
			//0.1640625f, 0.00390625f,

			// dirt_edge_left
			0.6640625f, 0.00390625f,		// 7
			0.6640625f, 0.08203125f,
			0.8203125f, 0.00390625f,
			//0.6640625f, 0.08203125f,
			0.8203125f, 0.08203125f,
			//0.8203125f, 0.00390625f,

			// dirt_edge_right
			0.828125f, 0.00390625f,			// 8
			0.828125f, 0.08203125f,
			0.984375f, 0.00390625f,
			//0.828125f, 0.08203125f,
			0.984375f, 0.08203125f,
			//0.984375f, 0.00390625f,

			// dirt_edge_top
			0.0078125f, 0.0859375f,			// 9
			0.0078125f, 0.1640625f,
			0.1640625f, 0.0859375f,
			//0.0078125f, 0.1640625f,
			0.1640625f, 0.1640625f,
			//0.1640625f, 0.0859375f,

			// dirt_edge_bottom
			0.171875f, 0.00390625f,			// 10
			0.171875f, 0.08203125f,
			0.328125f, 0.00390625f,
			//0.171875f, 0.08203125f,
			0.328125f, 0.08203125f,
			//0.328125f, 0.00390625f,


			// dirt_edge_top_right
			0.3359375f, 0.0859375f,			// 11
			0.3359375f, 0.1640625f,
			0.4921875f, 0.0859375f,
			//0.3359375f, 0.1640625f,
			0.4921875f, 0.1640625f,
			//0.4921875f, 0.0859375f,

			// dirt_grass_edge_bottom_left
			0.5f, 0.0859375f,				// 12
			0.5f, 0.1640625f,
			0.65625f, 0.0859375f,
			//0.5f, 0.1640625f,
			0.65625f, 0.1640625f,
			//0.65625f, 0.0859375f,

			// dirt_grass_edge_bottom_right
			0.6640625f, 0.0859375f,			// 13
			0.6640625f, 0.1640625f,
			0.8203125f, 0.0859375f,
			//0.6640625f, 0.1640625f,
			0.8203125f, 0.1640625f,
			//0.8203125f, 0.0859375f,

			// dirt_grass_edge_bottom_right
			0.6640625f, 0.0859375f,			// 14
			0.6640625f, 0.1640625f,
			0.8203125f, 0.0859375f,
			//0.6640625f, 0.1640625f,
			0.8203125f, 0.1640625f,
			//0.8203125f, 0.0859375f,

			// dirt_grass_edge_top_left
			0.828125f, 0.0859375f,			// 15
			0.828125f, 0.1640625f,
			0.984375f, 0.0859375f,
			//0.828125f, 0.1640625f,
			0.984375f, 0.1640625f,
			//0.984375f, 0.0859375f,

			// dirt_grass_edge_top_right
			0.0078125f, 0.16796875f,		// 16
			0.0078125f, 0.24609375f,
			0.1640625f, 0.16796875f,
			//0.0078125f, 0.24609375f,
			0.1640625f, 0.24609375f,
			//0.1640625f, 0.16796875f,

			// dirt_grass_edge_top_right
			0.0078125f, 0.16796875f,		// 17
			0.0078125f, 0.24609375f,
			0.1640625f, 0.16796875f,
			//0.0078125f, 0.24609375f,
			0.1640625f, 0.24609375f,
			//0.1640625f, 0.16796875f,

			// grass_edge_top_left
			0.0078125f, 0.25f,				// 18
			0.0078125f, 0.328125f,
			0.1640625f, 0.25f,
			//0.0078125f, 0.328125f,
			0.1640625f, 0.328125f,
			//0.1640625f, 0.25f,

			// water_edge_left
			0.171875f, 0.7421875f,			// 19
			0.171875f, 0.8203125f,
			0.328125f, 0.7421875f,
			//0.171875f, 0.8203125f,
			0.328125f, 0.8203125f,
			//0.328125f, 0.7421875f,

			// water_edge_right
			0.3359375f, 0.7421875f,			// 20
			0.3359375f, 0.8203125f,
			0.4921875f, 0.7421875f,
			//0.3359375f, 0.8203125f,
			0.4921875f, 0.8203125f,
			//0.4921875f, 0.7421875f,

			// grass_edge_top_right
			0.171875f, 0.25f,				// 21
			0.171875f, 0.328125f,
			0.328125f, 0.25f,
			//0.171875f, 0.328125f,
			0.328125f, 0.328125f,
			//0.328125f, 0.25f,

			// dirt_edge_top_left
			0.171875f, 0.0859375f,			// 22
			0.171875f, 0.1640625f,
			0.328125f, 0.0859375f,
			//0.171875f, 0.1640625f,
			0.328125f, 0.1640625f,
			//0.328125f, 0.0859375f,

			// dirt_edge_top_right
			0.3359375f, 0.0859375f,			// 23
			0.3359375f, 0.1640625f,
			0.4921875f, 0.0859375f,
			//0.3359375f, 0.1640625f,
			0.4921875f, 0.1640625f,
			//0.4921875f, 0.0859375f,


			// dirt_rocks_1
			0.171875f, 0.16796875f,			// 24
			0.171875f, 0.24609375f,
			0.328125f, 0.16796875f,
			//0.171875f, 0.24609375f,
			0.328125f, 0.24609375f,
			//0.328125f, 0.16796875f,


			// grass_edge_bottom_left
			0.6640625f, 0.16796875f,		// 25
			0.6640625f, 0.24609375f,
			0.8203125f, 0.16796875f,
			//0.6640625f, 0.24609375f,
			0.8203125f, 0.24609375f,
			//0.8203125f, 0.16796875f,


			// grass_edge_top_left
			0.0078125f, 0.25f,				// 26
			0.0078125f, 0.328125f,
			0.1640625f, 0.25f,
			//0.0078125f, 0.328125f,
			0.1640625f, 0.328125f,
			//0.1640625f, 0.25f,

			// grass_edge_top_right
			0.171875f, 0.25f,				// 27
			0.171875f, 0.328125f,
			0.328125f, 0.25f,
			//0.171875f, 0.328125f,
			0.328125f, 0.328125f,
			//0.328125f, 0.25f,

			// rock_face
			0.3359375f, 0.25f,				// 28
			0.3359375f, 0.328125f,
			0.4921875f, 0.25f,
			//0.3359375f, 0.328125f,
			0.4921875f, 0.328125f,
			//0.4921875f, 0.25f,

			// rock_face_corner_south_east
			0.5f, 0.25f,					// 29
			0.5f, 0.328125f,
			0.65625f, 0.25f,
			//0.5f, 0.328125f,
			0.65625f, 0.328125f,
			//0.65625f, 0.25f,

			// rock_face_corner_south_west
			0.6640625f, 0.25f,				// 30
			0.6640625f, 0.328125f,
			0.8203125f, 0.25f,
			//0.6640625f, 0.328125f,
			0.8203125f, 0.328125f,
			//0.8203125f, 0.25f,

			// water
			0.5f, 0.66015625f,				// 31
			0.5f, 0.73828125f,
			0.65625f, 0.66015625f,
			//0.5f, 0.73828125f,
			0.65625f, 0.73828125f,
			//0.65625f, 0.66015625f,

			// rock_face_east
			0.828125f, 0.25f,				// 32
			0.828125f, 0.328125f,
			0.984375f, 0.25f,
			//0.828125f, 0.328125f,
			0.984375f, 0.328125f,
			//0.984375f, 0.25f,

			// rock_grass_top_south
			0.171875f, 0.578125f,			// 33
			0.171875f, 0.65625f,
			0.328125f, 0.578125f,
			//0.171875f, 0.65625f,
			0.328125f, 0.65625f,
			//0.328125f, 0.578125f,

			// rock_grass_bottom
			0.171875f, 0.33203125f,			// 34
			0.171875f, 0.41015625f,
			0.328125f, 0.33203125f,
			//0.171875f, 0.41015625f,
			0.328125f, 0.41015625f,
			//0.328125f, 0.33203125f,

			// rock_grass_bottom_left
			0.6640625f, 0.33203125f,		// 35
			0.6640625f, 0.41015625f,
			0.8203125f, 0.33203125f,
			//0.6640625f, 0.41015625f,
			0.8203125f, 0.41015625f,
			//0.8203125f, 0.33203125f,

			// rock_face_west
			0.0078125f, 0.33203125f,		// 36
			0.0078125f, 0.41015625f,
			0.1640625f, 0.33203125f,
			//0.0078125f, 0.41015625f,
			0.1640625f, 0.41015625f,
			//0.1640625f, 0.33203125f,

			// rock_grass_bottom_east
			0.3359375f, 0.33203125f,		// 37
			0.3359375f, 0.41015625f,
			0.4921875f, 0.33203125f,
			//0.3359375f, 0.41015625f,
			0.4921875f, 0.41015625f,
			//0.4921875f, 0.33203125f,

			// rock_grass_bottom_east_corner
			0.5f, 0.33203125f,				// 38
			0.5f, 0.41015625f,
			0.65625f, 0.33203125f,
			//0.5f, 0.41015625f,
			0.65625f, 0.41015625f,
			//0.65625f, 0.33203125f,


			// rock_grass_bottom_north
			0.828125f, 0.33203125f,			// 39
			0.828125f, 0.41015625f,
			0.984375f, 0.33203125f,
			//0.828125f, 0.41015625f,
			0.984375f, 0.41015625f,
			//0.984375f, 0.33203125f,

			// rock_grass_bottom_north_east_corner
			0.0078125f, 0.4140625f,			// 40
			0.0078125f, 0.4921875f,
			0.1640625f, 0.4140625f,
			//0.0078125f, 0.4921875f,
			0.1640625f, 0.4921875f,
			//0.1640625f, 0.4140625f,

			// rock_grass_bottom_north_west_corner
			0.171875f, 0.4140625f,			// 41
			0.171875f, 0.4921875f,
			0.328125f, 0.4140625f,
			//0.171875f, 0.4921875f,
			0.328125f, 0.4921875f,
			//0.328125f, 0.4140625f,

			// rock_grass_bottom_right
			0.3359375f, 0.4140625f,			// 42
			0.3359375f, 0.4921875f,
			0.4921875f, 0.4140625f,
			//0.3359375f, 0.4921875f,
			0.4921875f, 0.4921875f,
			//0.4921875f, 0.4140625f,

			// rock_grass_bottom_west
			0.5f, 0.4140625f,				// 43
			0.5f, 0.4921875f,
			0.65625f, 0.4140625f,
			//0.5f, 0.4921875f,
			0.65625f, 0.4921875f,
			//0.65625f, 0.4140625f,

			// rock_grass_bottom_west_corner
			0.6640625f, 0.4140625f,			// 44
			0.6640625f, 0.4921875f,
			0.8203125f, 0.4140625f,
			//0.6640625f, 0.4921875f,
			0.8203125f, 0.4921875f,
			//0.8203125f, 0.4140625f,

			// rock_grass_hill_top_left
			0.828125f, 0.4140625f,			// 45
			0.828125f, 0.4921875f,
			0.984375f, 0.4140625f,
			//0.828125f, 0.4921875f,
			0.984375f, 0.4921875f,
			//0.984375f, 0.4140625f,

			// rock_grass_hill_top_right
			0.0078125f, 0.49609375f,
			0.0078125f, 0.57421875f,
			0.1640625f, 0.49609375f,
			//0.0078125f, 0.57421875f,
			0.1640625f, 0.57421875f,
			//0.1640625f, 0.49609375f,

			// rock_grass_top_corner_north_east
			0.171875f, 0.49609375f,
			0.171875f, 0.57421875f,
			0.328125f, 0.49609375f,
			//0.171875f, 0.57421875f,
			0.328125f, 0.57421875f,
			//0.328125f, 0.49609375f,

			// rock_grass_top_corner_north_west
			0.3359375f, 0.49609375f,
			0.3359375f, 0.57421875f,
			0.4921875f, 0.49609375f,
			//0.3359375f, 0.57421875f,
			0.4921875f, 0.57421875f,
			//0.4921875f, 0.49609375f,

			// rock_grass_top_corner_south_east
			0.5f, 0.49609375f,
			0.5f, 0.57421875f,
			0.65625f, 0.49609375f,
			//0.5f, 0.57421875f,
			0.65625f, 0.57421875f,
			//0.65625f, 0.49609375f,

			// rock_grass_top_corner_south_west
			0.6640625f, 0.49609375f,
			0.6640625f, 0.57421875f,
			0.8203125f, 0.49609375f,
			//0.6640625f, 0.57421875f,
			0.8203125f, 0.57421875f,
			//0.8203125f, 0.49609375f,

			// rock_grass_top_east
			0.828125f, 0.49609375f,
			0.828125f, 0.57421875f,
			0.984375f, 0.49609375f,
			//0.828125f, 0.57421875f,
			0.984375f, 0.57421875f,
			//0.984375f, 0.49609375f,

			// rock_grass_top_north
			0.0078125f, 0.578125f,
			0.0078125f, 0.65625f,
			0.1640625f, 0.578125f,
			//0.0078125f, 0.65625f,
			0.1640625f, 0.65625f,
			//0.1640625f, 0.578125f,

			// rock_grass_top_west
			0.3359375f, 0.578125f,
			0.3359375f, 0.65625f,
			0.4921875f, 0.578125f,
			//0.3359375f, 0.65625f,
			0.4921875f, 0.65625f,
			//0.4921875f, 0.578125f,

			// rock_grass_water_bottom_left
			0.5f, 0.578125f,
			0.5f, 0.65625f,
			0.65625f, 0.578125f,
			//0.5f, 0.65625f,
			0.65625f, 0.65625f,
			//0.65625f, 0.578125f,

			// rock_grass_water_bottom_right
			0.6640625f, 0.578125f,
			0.6640625f, 0.65625f,
			0.8203125f, 0.578125f,
			//0.6640625f, 0.65625f,
			0.8203125f, 0.65625f,
			//0.8203125f, 0.578125f,

			// rock_grass_water_top_left
			0.828125f, 0.578125f,
			0.828125f, 0.65625f,
			0.984375f, 0.578125f,
			//0.828125f, 0.65625f,
			0.984375f, 0.65625f,
			//0.984375f, 0.578125f,

			// rock_grass_water_top_right
			0.0078125f, 0.66015625f,
			0.0078125f, 0.73828125f,
			0.1640625f, 0.66015625f,
			0.0078125f, 0.73828125f,
			0.1640625f, 0.73828125f,
			0.1640625f, 0.66015625f,

			// rock_water_left
			0.171875f, 0.66015625f,
			0.171875f, 0.73828125f,
			0.328125f, 0.66015625f,
			0.171875f, 0.73828125f,
			0.328125f, 0.73828125f,
			0.328125f, 0.66015625f,

			// rock_water_right
			0.3359375f, 0.66015625f,
			0.3359375f, 0.73828125f,
			0.4921875f, 0.66015625f,
			0.3359375f, 0.73828125f,
			0.4921875f, 0.73828125f,
			0.4921875f, 0.66015625f,

			// water_edge_bottom
			0.6640625f, 0.66015625f,
			0.6640625f, 0.73828125f,
			0.8203125f, 0.66015625f,
			0.6640625f, 0.73828125f,
			0.8203125f, 0.73828125f,
			0.8203125f, 0.66015625f,

			// water_edge_bottom_left
			0.828125f, 0.66015625f,
			0.828125f, 0.73828125f,
			0.984375f, 0.66015625f,
			0.828125f, 0.73828125f,
			0.984375f, 0.73828125f,
			0.984375f, 0.66015625f,

			// water_edge_bottom_right
			0.0078125f, 0.7421875f,
			0.0078125f, 0.8203125f,
			0.1640625f, 0.7421875f,
			0.0078125f, 0.8203125f,
			0.1640625f, 0.8203125f,
			0.1640625f, 0.7421875f,

			// water_edge_left
			0.171875f, 0.7421875f,
			0.171875f, 0.8203125f,
			0.328125f, 0.7421875f,
			0.171875f, 0.8203125f,
			0.328125f, 0.8203125f,
			0.328125f, 0.7421875f,

			// water_edge_right
			0.3359375f, 0.7421875f,
			0.3359375f, 0.8203125f,
			0.4921875f, 0.7421875f,
			0.3359375f, 0.8203125f,
			0.4921875f, 0.8203125f,
			0.4921875f, 0.7421875f,

			// water_edge_top
			0.5f, 0.7421875f,
			0.5f, 0.8203125f,
			0.65625f, 0.7421875f,
			0.5f, 0.8203125f,
			0.65625f, 0.8203125f,
			0.65625f, 0.7421875f,

			// water_edge_top_left
			0.6640625f, 0.7421875f,
			0.6640625f, 0.8203125f,
			0.8203125f, 0.7421875f,
			0.6640625f, 0.8203125f,
			0.8203125f, 0.8203125f,
			0.8203125f, 0.7421875f,

			// water_edge_top_right
			0.828125f, 0.7421875f,
			0.828125f, 0.8203125f,
			0.984375f, 0.7421875f,
			0.828125f, 0.8203125f,
			0.984375f, 0.8203125f,
			0.984375f, 0.7421875f,

			// water_over_rock
			0.0078125f, 0.82421875f,
			0.0078125f, 0.90234375f,
			0.1640625f, 0.82421875f,
			0.0078125f, 0.90234375f,
			0.1640625f, 0.90234375f,
			0.1640625f, 0.82421875f,

			// dirt_edge_top_left
			0.171875f, 0.0859375f,			// ??
			0.171875f, 0.1640625f,
			0.328125f, 0.0859375f,
			0.171875f, 0.1640625f,
			0.328125f, 0.1640625f,
			0.328125f, 0.0859375f,

			// grass_edge_bottom_right
			0.828125f, 0.16796875f,			// ??
			0.828125f, 0.24609375f,
			0.984375f, 0.16796875f,
			0.828125f, 0.24609375f,
			0.984375f, 0.24609375f,
			0.984375f, 0.16796875f



	};

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** Size of the texture coordinate data in elements. */
	private final static int TEXTURE_COORD_DATA_SIZE = 2;
    private final static int TX_STRIDE = TEXTURE_COORD_DATA_SIZE * BYTES_PER_FLOAT;


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    
    public Ground(Tile[][] tiles) {

    	
    	
    	float positions[] = {
                0f,    0f, 0f, // 0     // vertices of the square
                0f, -100f, 0f, // 1
              100f,    0f, 0f, // 2
              100f, -100f, 0f  // 3
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
			
				int pos = SQUARE_DATA_SIZE + ((tiles[i][j].getId() - 1) * TEXTURE_STRIDE);
				
				System.arraycopy(positions, 0, alldata, index, 3); // first coords (x, y, z);
				alldata[index++] += offsetX;
				alldata[index++] += offsetY;
				index++;
				
				System.arraycopy(everything, pos, alldata, index, 2); // first texture position
				index += 2;
				
				System.arraycopy(positions, 3, alldata, index, 3); // second coords (x, y, z);
				alldata[index++] += offsetX;
				alldata[index++] += offsetY;
				index++;
				
				System.arraycopy(everything, pos+2, alldata, index, 2); // second texture position
				index += 2;

				System.arraycopy(positions, 6, alldata, index, 3); // third coords (x, y, z);
				alldata[index++] += offsetX;
				alldata[index++] += offsetY;
				index++;
				
				System.arraycopy(everything, pos+4, alldata, index, 2); // third texture position
				index += 2;

				System.arraycopy(positions, 9, alldata, index, 3); // fourth coords (x, y, z);
				alldata[index++] += offsetX;
				alldata[index++] += offsetY;
				index++;
				
				System.arraycopy(everything, pos+6, alldata, index, 2); // fourth texture position
				index += 2;

				System.arraycopy(drawOrder, 0, indexes, doIndex, vertexCount);
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				indexes[doIndex++] += VERTICES_PER_OBJECT * squares;
				
				squares++;
				
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
//		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
//				GLES20.GL_FLOAT, false, vertexStride, 0);        
        
        // get handle to fragment shader's vColor member
        mColorHandle = prog.getUniformLocation("vColor");
        
        // Set color for drawing
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        // texture stuff
        
        mTextureUniformHandle = prog.getUniformLocation("u_Texture");
        mTextureCoordinateHandle = prog.getAttributeLocation("a_TexCoordinate");
        tm.setTexture(R.drawable.ground_tiles);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        // start the position at -1, -1
        Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, 0, 0, -1.0f);
        
		//for (int i = 0; i < oTiles.length; i++) {

			//Matrix.translateM(mMMatrix, 0, 0, -TILE_SIZE, 0);

			//for (int j = 0; j < oTiles[i].length; j++) {

				//Matrix.translateM(mMMatrix, 0, TILE_SIZE, 0, 0);

		        // set the texture pointer to the correct place in the buffer
				// note: web ffz tile ids started at 1, where this is 0-based


				
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
		        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawLength, GLES20.GL_UNSIGNED_SHORT, 0);

			//}

			//Matrix.translateM(mMMatrix, 0, -(oTiles[i].length * TILE_SIZE), 0, 0);

		//}

    	// IMPORTANT: Unbind from the buffer when we're done with it.
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.ground_tiles);
		tm.loadTextures();

	}
    
    
}