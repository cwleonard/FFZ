package com.amphibian.ffz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Ground {

	private static final int TILE_SIZE = 100;
	
	private Tile[][] oTiles;
	
	private static TextureManager tm;
	
	private final float[] mMMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] eyeMatrix = new float[16];

	private FloatBuffer everythingBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    private final int SQUARE_DATA_SIZE = 18;
    
    
    private final int vertexCount = 6; // how many vertices does it take to draw the square?
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final static int TEXTURE_STRIDE = 12;
    
	//private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
	private float everything[] = {
			
                0f, 100f, 0f,
                0f,   0f, 0f,
              100f, 100f, 0f,
                0f,   0f, 0f,
              100f,   0f, 0f,
              100f, 100f, 0f, 

			// grass
			0.3359375f, 0.16796875f,		// 0
			0.3359375f, 0.24609375f,
			0.4921875f, 0.16796875f,
			0.3359375f, 0.24609375f,
			0.4921875f, 0.24609375f,
			0.4921875f, 0.16796875f,

			// grass2
			0.5f, 0.16796875f,				// 1
			0.5f, 0.24609375f,
			0.65625f, 0.16796875f,
			0.5f, 0.24609375f,
			0.65625f, 0.24609375f,
			0.65625f, 0.16796875f,


			// dirt_edge_bottom
			0.171875f, 0.00390625f,			// 2
			0.171875f, 0.08203125f,
			0.328125f, 0.00390625f,
			0.171875f, 0.08203125f,
			0.328125f, 0.08203125f,
			0.328125f, 0.00390625f,

			// dirt_edge_bottom_left
			0.3359375f, 0.00390625f,		// 3
			0.3359375f, 0.08203125f,
			0.4921875f, 0.00390625f,
			0.3359375f, 0.08203125f,
			0.4921875f, 0.08203125f,
			0.4921875f, 0.00390625f,

			// dirt_edge_bottom_right
			0.5f, 0.00390625f,				// 4
			0.5f, 0.08203125f,
			0.65625f, 0.00390625f,
			0.5f, 0.08203125f,
			0.65625f, 0.08203125f,
			0.65625f, 0.00390625f,

			// dirt_edge_left
			0.6640625f, 0.00390625f,		// 5
			0.6640625f, 0.08203125f,
			0.8203125f, 0.00390625f,
			0.6640625f, 0.08203125f,
			0.8203125f, 0.08203125f,
			0.8203125f, 0.00390625f,

			// dirt
			0.0078125f, 0.00390625f,		// 6
			0.0078125f, 0.08203125f,
			0.1640625f, 0.00390625f,
			0.0078125f, 0.08203125f,
			0.1640625f, 0.08203125f,
			0.1640625f, 0.00390625f,

			// dirt_edge_left
			0.6640625f, 0.00390625f,		// 7
			0.6640625f, 0.08203125f,
			0.8203125f, 0.00390625f,
			0.6640625f, 0.08203125f,
			0.8203125f, 0.08203125f,
			0.8203125f, 0.00390625f,

			// dirt_edge_right
			0.828125f, 0.00390625f,			// 8
			0.828125f, 0.08203125f,
			0.984375f, 0.00390625f,
			0.828125f, 0.08203125f,
			0.984375f, 0.08203125f,
			0.984375f, 0.00390625f,

			// dirt_edge_top
			0.0078125f, 0.0859375f,			// 9
			0.0078125f, 0.1640625f,
			0.1640625f, 0.0859375f,
			0.0078125f, 0.1640625f,
			0.1640625f, 0.1640625f,
			0.1640625f, 0.0859375f,

			// dirt_edge_bottom
			0.171875f, 0.00390625f,			// 10
			0.171875f, 0.08203125f,
			0.328125f, 0.00390625f,
			0.171875f, 0.08203125f,
			0.328125f, 0.08203125f,
			0.328125f, 0.00390625f,


			// dirt_edge_top_right
			0.3359375f, 0.0859375f,			// 11
			0.3359375f, 0.1640625f,
			0.4921875f, 0.0859375f,
			0.3359375f, 0.1640625f,
			0.4921875f, 0.1640625f,
			0.4921875f, 0.0859375f,

			// dirt_grass_edge_bottom_left
			0.5f, 0.0859375f,				// 12
			0.5f, 0.1640625f,
			0.65625f, 0.0859375f,
			0.5f, 0.1640625f,
			0.65625f, 0.1640625f,
			0.65625f, 0.0859375f,

			// dirt_grass_edge_bottom_right
			0.6640625f, 0.0859375f,			// 13
			0.6640625f, 0.1640625f,
			0.8203125f, 0.0859375f,
			0.6640625f, 0.1640625f,
			0.8203125f, 0.1640625f,
			0.8203125f, 0.0859375f,

			// dirt_grass_edge_bottom_right
			0.6640625f, 0.0859375f,			// 14
			0.6640625f, 0.1640625f,
			0.8203125f, 0.0859375f,
			0.6640625f, 0.1640625f,
			0.8203125f, 0.1640625f,
			0.8203125f, 0.0859375f,

			// dirt_grass_edge_top_left
			0.828125f, 0.0859375f,			// 15
			0.828125f, 0.1640625f,
			0.984375f, 0.0859375f,
			0.828125f, 0.1640625f,
			0.984375f, 0.1640625f,
			0.984375f, 0.0859375f,

			// dirt_grass_edge_top_right
			0.0078125f, 0.16796875f,		// 16
			0.0078125f, 0.24609375f,
			0.1640625f, 0.16796875f,
			0.0078125f, 0.24609375f,
			0.1640625f, 0.24609375f,
			0.1640625f, 0.16796875f,

			// dirt_grass_edge_top_right
			0.0078125f, 0.16796875f,		// 17
			0.0078125f, 0.24609375f,
			0.1640625f, 0.16796875f,
			0.0078125f, 0.24609375f,
			0.1640625f, 0.24609375f,
			0.1640625f, 0.16796875f,

			// grass_edge_top_left
			0.0078125f, 0.25f,				// 18
			0.0078125f, 0.328125f,
			0.1640625f, 0.25f,
			0.0078125f, 0.328125f,
			0.1640625f, 0.328125f,
			0.1640625f, 0.25f,

			// water_edge_left
			0.171875f, 0.7421875f,			// 19
			0.171875f, 0.8203125f,
			0.328125f, 0.7421875f,
			0.171875f, 0.8203125f,
			0.328125f, 0.8203125f,
			0.328125f, 0.7421875f,

			// water_edge_right
			0.3359375f, 0.7421875f,			// 20
			0.3359375f, 0.8203125f,
			0.4921875f, 0.7421875f,
			0.3359375f, 0.8203125f,
			0.4921875f, 0.8203125f,
			0.4921875f, 0.7421875f,

			// grass_edge_top_right
			0.171875f, 0.25f,				// 21
			0.171875f, 0.328125f,
			0.328125f, 0.25f,
			0.171875f, 0.328125f,
			0.328125f, 0.328125f,
			0.328125f, 0.25f,

			// dirt_edge_top_left
			0.171875f, 0.0859375f,			// 22
			0.171875f, 0.1640625f,
			0.328125f, 0.0859375f,
			0.171875f, 0.1640625f,
			0.328125f, 0.1640625f,
			0.328125f, 0.0859375f,

			// dirt_edge_top_right
			0.3359375f, 0.0859375f,			// 23
			0.3359375f, 0.1640625f,
			0.4921875f, 0.0859375f,
			0.3359375f, 0.1640625f,
			0.4921875f, 0.1640625f,
			0.4921875f, 0.0859375f,


			// dirt_rocks_1
			0.171875f, 0.16796875f,			// 24
			0.171875f, 0.24609375f,
			0.328125f, 0.16796875f,
			0.171875f, 0.24609375f,
			0.328125f, 0.24609375f,
			0.328125f, 0.16796875f,


			// grass_edge_bottom_left
			0.6640625f, 0.16796875f,		// 25
			0.6640625f, 0.24609375f,
			0.8203125f, 0.16796875f,
			0.6640625f, 0.24609375f,
			0.8203125f, 0.24609375f,
			0.8203125f, 0.16796875f,


			// grass_edge_top_left
			0.0078125f, 0.25f,				// 26
			0.0078125f, 0.328125f,
			0.1640625f, 0.25f,
			0.0078125f, 0.328125f,
			0.1640625f, 0.328125f,
			0.1640625f, 0.25f,

			// grass_edge_top_right
			0.171875f, 0.25f,				// 27
			0.171875f, 0.328125f,
			0.328125f, 0.25f,
			0.171875f, 0.328125f,
			0.328125f, 0.328125f,
			0.328125f, 0.25f,

			// rock_face
			0.3359375f, 0.25f,				// 28
			0.3359375f, 0.328125f,
			0.4921875f, 0.25f,
			0.3359375f, 0.328125f,
			0.4921875f, 0.328125f,
			0.4921875f, 0.25f,

			// rock_face_corner_south_east
			0.5f, 0.25f,					// 29
			0.5f, 0.328125f,
			0.65625f, 0.25f,
			0.5f, 0.328125f,
			0.65625f, 0.328125f,
			0.65625f, 0.25f,

			// rock_face_corner_south_west
			0.6640625f, 0.25f,				// 30
			0.6640625f, 0.328125f,
			0.8203125f, 0.25f,
			0.6640625f, 0.328125f,
			0.8203125f, 0.328125f,
			0.8203125f, 0.25f,

			// water
			0.5f, 0.66015625f,				// 31
			0.5f, 0.73828125f,
			0.65625f, 0.66015625f,
			0.5f, 0.73828125f,
			0.65625f, 0.73828125f,
			0.65625f, 0.66015625f,

			// rock_face_east
			0.828125f, 0.25f,				// 32
			0.828125f, 0.328125f,
			0.984375f, 0.25f,
			0.828125f, 0.328125f,
			0.984375f, 0.328125f,
			0.984375f, 0.25f,

			// rock_grass_top_south
			0.171875f, 0.578125f,			// 33
			0.171875f, 0.65625f,
			0.328125f, 0.578125f,
			0.171875f, 0.65625f,
			0.328125f, 0.65625f,
			0.328125f, 0.578125f,

			// rock_grass_bottom
			0.171875f, 0.33203125f,			// 34
			0.171875f, 0.41015625f,
			0.328125f, 0.33203125f,
			0.171875f, 0.41015625f,
			0.328125f, 0.41015625f,
			0.328125f, 0.33203125f,

			// rock_grass_bottom_left
			0.6640625f, 0.33203125f,		// 35
			0.6640625f, 0.41015625f,
			0.8203125f, 0.33203125f,
			0.6640625f, 0.41015625f,
			0.8203125f, 0.41015625f,
			0.8203125f, 0.33203125f,

			// rock_face_west
			0.0078125f, 0.33203125f,		// 36
			0.0078125f, 0.41015625f,
			0.1640625f, 0.33203125f,
			0.0078125f, 0.41015625f,
			0.1640625f, 0.41015625f,
			0.1640625f, 0.33203125f,

			// rock_grass_bottom_east
			0.3359375f, 0.33203125f,		// 37
			0.3359375f, 0.41015625f,
			0.4921875f, 0.33203125f,
			0.3359375f, 0.41015625f,
			0.4921875f, 0.41015625f,
			0.4921875f, 0.33203125f,

			// rock_grass_bottom_east_corner
			0.5f, 0.33203125f,				// 38
			0.5f, 0.41015625f,
			0.65625f, 0.33203125f,
			0.5f, 0.41015625f,
			0.65625f, 0.41015625f,
			0.65625f, 0.33203125f,


			// rock_grass_bottom_north
			0.828125f, 0.33203125f,			// 39
			0.828125f, 0.41015625f,
			0.984375f, 0.33203125f,
			0.828125f, 0.41015625f,
			0.984375f, 0.41015625f,
			0.984375f, 0.33203125f,

			// rock_grass_bottom_north_east_corner
			0.0078125f, 0.4140625f,			// 40
			0.0078125f, 0.4921875f,
			0.1640625f, 0.4140625f,
			0.0078125f, 0.4921875f,
			0.1640625f, 0.4921875f,
			0.1640625f, 0.4140625f,

			// rock_grass_bottom_north_west_corner
			0.171875f, 0.4140625f,			// 41
			0.171875f, 0.4921875f,
			0.328125f, 0.4140625f,
			0.171875f, 0.4921875f,
			0.328125f, 0.4921875f,
			0.328125f, 0.4140625f,

			// rock_grass_bottom_right
			0.3359375f, 0.4140625f,			// 42
			0.3359375f, 0.4921875f,
			0.4921875f, 0.4140625f,
			0.3359375f, 0.4921875f,
			0.4921875f, 0.4921875f,
			0.4921875f, 0.4140625f,

			// rock_grass_bottom_west
			0.5f, 0.4140625f,				// 43
			0.5f, 0.4921875f,
			0.65625f, 0.4140625f,
			0.5f, 0.4921875f,
			0.65625f, 0.4921875f,
			0.65625f, 0.4140625f,

			// rock_grass_bottom_west_corner
			0.6640625f, 0.4140625f,			// 44
			0.6640625f, 0.4921875f,
			0.8203125f, 0.4140625f,
			0.6640625f, 0.4921875f,
			0.8203125f, 0.4921875f,
			0.8203125f, 0.4140625f,

			// rock_grass_hill_top_left
			0.828125f, 0.4140625f,			// 45
			0.828125f, 0.4921875f,
			0.984375f, 0.4140625f,
			0.828125f, 0.4921875f,
			0.984375f, 0.4921875f,
			0.984375f, 0.4140625f,

			// rock_grass_hill_top_right
			0.0078125f, 0.49609375f,
			0.0078125f, 0.57421875f,
			0.1640625f, 0.49609375f,
			0.0078125f, 0.57421875f,
			0.1640625f, 0.57421875f,
			0.1640625f, 0.49609375f,

			// rock_grass_top_corner_north_east
			0.171875f, 0.49609375f,
			0.171875f, 0.57421875f,
			0.328125f, 0.49609375f,
			0.171875f, 0.57421875f,
			0.328125f, 0.57421875f,
			0.328125f, 0.49609375f,

			// rock_grass_top_corner_north_west
			0.3359375f, 0.49609375f,
			0.3359375f, 0.57421875f,
			0.4921875f, 0.49609375f,
			0.3359375f, 0.57421875f,
			0.4921875f, 0.57421875f,
			0.4921875f, 0.49609375f,

			// rock_grass_top_corner_south_east
			0.5f, 0.49609375f,
			0.5f, 0.57421875f,
			0.65625f, 0.49609375f,
			0.5f, 0.57421875f,
			0.65625f, 0.57421875f,
			0.65625f, 0.49609375f,

			// rock_grass_top_corner_south_west
			0.6640625f, 0.49609375f,
			0.6640625f, 0.57421875f,
			0.8203125f, 0.49609375f,
			0.6640625f, 0.57421875f,
			0.8203125f, 0.57421875f,
			0.8203125f, 0.49609375f,

			// rock_grass_top_east
			0.828125f, 0.49609375f,
			0.828125f, 0.57421875f,
			0.984375f, 0.49609375f,
			0.828125f, 0.57421875f,
			0.984375f, 0.57421875f,
			0.984375f, 0.49609375f,

			// rock_grass_top_north
			0.0078125f, 0.578125f,
			0.0078125f, 0.65625f,
			0.1640625f, 0.578125f,
			0.0078125f, 0.65625f,
			0.1640625f, 0.65625f,
			0.1640625f, 0.578125f,

			// rock_grass_top_west
			0.3359375f, 0.578125f,
			0.3359375f, 0.65625f,
			0.4921875f, 0.578125f,
			0.3359375f, 0.65625f,
			0.4921875f, 0.65625f,
			0.4921875f, 0.578125f,

			// rock_grass_water_bottom_left
			0.5f, 0.578125f,
			0.5f, 0.65625f,
			0.65625f, 0.578125f,
			0.5f, 0.65625f,
			0.65625f, 0.65625f,
			0.65625f, 0.578125f,

			// rock_grass_water_bottom_right
			0.6640625f, 0.578125f,
			0.6640625f, 0.65625f,
			0.8203125f, 0.578125f,
			0.6640625f, 0.65625f,
			0.8203125f, 0.65625f,
			0.8203125f, 0.578125f,

			// rock_grass_water_top_left
			0.828125f, 0.578125f,
			0.828125f, 0.65625f,
			0.984375f, 0.578125f,
			0.828125f, 0.65625f,
			0.984375f, 0.65625f,
			0.984375f, 0.578125f,

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
	private final int mTextureCoordinateDataSize = 2;
	 
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    public Ground() {
    	
    	Matrix.setIdentityM(mMMatrix, 0);
    	
        everythingBuffer = ByteBuffer.allocateDirect(everything.length * 4)
        		.order(ByteOrder.nativeOrder()).asFloatBuffer();
        everythingBuffer.put(everything).position(0);
        
    }
    
    
    
    
    public void setTiles(Tile[][] oTiles) {
		this.oTiles = oTiles;
	}


	public void draw(StandardProgram prog, Viewport vp) {
    	
		float[] projMatrix = vp.getProjMatrix();
		float[] viewMatrix = vp.getViewMatrix();
		
        // get handle to vertex shader's vPosition member
        mPositionHandle = prog.getAttributeLocation("vPosition");
        

        everythingBuffer.position(0);
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, everythingBuffer);

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

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = prog.getUniformLocation("uMVPMatrix");

        // start the position at -1, -1
        Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, -TILE_SIZE, 0, 0);
        
		for (int i = 0; i < oTiles.length; i++) {
			
			Matrix.translateM(mMMatrix, 0, 0, -TILE_SIZE, 0);
			
			for (int j = 0; j < oTiles[i].length; j++) {
			
				Matrix.translateM(mMMatrix, 0, TILE_SIZE, 0, 0);
				
		        // Pass in the texture coordinate information
				// note: web ffz tile ids started at 1, where this is 0-based
		        everythingBuffer.position(SQUARE_DATA_SIZE + ((oTiles[i][j].getId() - 1) * TEXTURE_STRIDE));
				GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
						mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
						everythingBuffer);
		        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		        
		        // translate!
		        Matrix.multiplyMM(eyeMatrix, 0, viewMatrix, 0, mMMatrix, 0);
		        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, eyeMatrix, 0);
		        

		        // Apply the projection and view transformation
		        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		        
		        // Draw the square
		        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		        
			}
			
			Matrix.translateM(mMMatrix, 0, -(oTiles[i].length * TILE_SIZE), 0, 0);
			
		}

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
    }    

	public static void loadGLTexture(Context context) {

		tm = new TextureManager(context);
		tm.add(R.drawable.ground_tiles);
		tm.loadTextures();
		
	}
    
    
}