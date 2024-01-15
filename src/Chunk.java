import java.util.*;

import org.joml.Vector3i;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



public class Chunk {


    Chunk rightChunk;
    Chunk leftChunk;
    Chunk frontChunk;
    Chunk backChunk;


    ArrayList<Float> opaqueVertList;
    float[] opaqueVertArray;
    ArrayList<Float> transVertList;

    float[] transVertArray;

    public int opaqueVertsAmount = 0;
    public int transVertsAmount = 0;


    //ArrayList<ArrayList<ArrayList<BlockType>>> chunkData = new ArrayList<>();

    BlockType[][][] chunkData = new BlockType[World.chunkSizeX][World.worldSizeY][World.chunkSizeZ];
    Map<BlockType, TextureCoords> blockTextures = new HashMap<>();

    public int opaqueVAO;

    public int opaqueVBO;

    public int transVAO;
    public int transVBO;

    public Vector3i position;
    FastNoiseLite noise;

    FastNoiseLite moistNoise;

    // Function to combine vertex positions and texture coordinates
    private Collection<? extends Float> combineVertexData(float[] positions, float[] texCoords) {
        Float[] combined = new Float[positions.length+texCoords.length];
        int index = 0;
        for (int i = 0, j = 0; i < positions.length; i += 6, j += 2) {
            combined[index++] = (positions[i]);     // x
            combined[index++] = (positions[i + 1]); // y
            combined[index++] = (positions[i + 2]); // z
            combined[index++] = (positions[i + 3]); // a
            combined[index++] = (positions[i + 4]); // b
            combined[index++] = (positions[i + 5]); // c
            combined[index++] = (texCoords[j]);     // u
            combined[index++] = (texCoords[j + 1]); // v
        }
        return List.of(combined);
    }

    public enum FaceType {
        TOP, LEFT, FRONT, BACK, BOTTOM, RIGHT
    }

    public float[] getTextureCoords(TextureCoords cords, FaceType faceType) {
        float tileWidth = 1.0f / cords.tileAcross; // prob 16
        float startX = cords.x * tileWidth;
        float startY = cords.y * tileWidth;
        float endX = startX + tileWidth;
        float endY = startY + tileWidth;

        switch (faceType) {
            case TOP:
                // Assuming TOP needs standard orientation
                return new float[]{
                        startX, endY,
                        endX, startY,
                        endX, endY,
                        endX, startY,
                        startX, endY,
                        startX, startY
                };
            case LEFT:
                // Assuming LEFT needs to be flipped vertically
                return new float[]{
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case FRONT:
                // Assuming FRONT needs to be flipped horizontally
                return new float[]{
                        startX, endY,
                        endX, endY,
                        endX, startY,
                        endX, startY,
                        startX, startY,
                        startX, endY
                };
            case BACK:
                // Assuming BACK face orientation is same as FRONT
                return new float[]{
                        endX, endY,
                        startX, startY,
                        startX, endY,
                        startX, startY,
                        endX, endY,
                        endX, startY
                };
            case BOTTOM:
                // Assuming BOTTOM face orientation is same as TOP
                return new float[]{
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case RIGHT:
                // Assuming RIGHT face orientation is same as LEFT
                return new float[]{
                        startX, startY,
                        endX, endY,
                        endX, startY,
                        endX, endY,
                        startX, startY,
                        startX, endY
                };
            default:
                return null; // or some default value
        }
    }

    private void generateTopFace(int x, int y, int z) {

        float[] verts;

        BlockType blockType = chunkData[x - (World.chunkSizeX * position.x)][y][z - (World.chunkSizeZ * position.z)];

        if((blockType == BlockType.WATER || blockType == BlockType.LAVA) && chunkData[(x - (World.chunkSizeX * position.x))][y+1][((z - (World.chunkSizeZ * position.z)))] == BlockType.AIR) {

            verts = new float[]{
                    -0.5f + x, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    0.5f + x, y + 0.35f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                    0.5f + x, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-right
                    0.5f + x, y + 0.35f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                    -0.5f + x, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    -0.5f + x, y + 0.35f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-left

            };

        } else {

            verts = new float[]{
                    -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                    0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-right
                    0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                    -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    -0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-left

            };

        }


        // Define the vertices for the top face of the block

        // Generate the default verts array


        if (blockType == BlockType.SIDEDIRT) {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.GRASS), FaceType.TOP)));
        } else if (blockType == BlockType.SIDESNOW) {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.SNOW), FaceType.TOP)));
        } else if(isLiquid(blockType)){
            transVertsAmount += 6;
            transVertList.addAll(combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.TOP)));
        }  else {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.TOP)));
        }

    }



    private void generateFace(int x, int y, int z, FaceType faceType){

        BlockType blockType = chunkData[(x - (World.chunkSizeX * position.x))][y][(z - (World.chunkSizeZ * position.z))];

        float[] verts = new float[36];

        if((isLiquid(blockType)) && chunkData[(x - (World.chunkSizeX * position.x))][y+1][(z - (World.chunkSizeZ * position.z))] == BlockType.AIR){ // no reason to have this atm but maybe in future.
            switch (faceType) {

                case FRONT:
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };
                    break;
                case LEFT:
                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.35f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.35f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.35f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right

                    };
                    break;
                case RIGHT:
                    verts = new float[]{
                            0.5f + x, 0.35f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.35f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.35f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f,// bottom-left

                    };
                    break;
                case BACK:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };
                    break;
                case BOTTOM:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,   // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,  // top-right

                    };
                    break;
            }

        } else {

            switch (faceType) {
                case FRONT:
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };
                    break;
                case LEFT:
                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right

                    };
                    break;
                case RIGHT:
                    verts = new float[]{
                            0.5f + x, 0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f,// bottom-left

                    };
                    break;
                case BACK:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };
                    break;
                case BOTTOM:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,   // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,  // top-right
                    };
                    break;
            }
        }




        if(isLiquid(blockType)){ // disabled water because why would we need water sides atm aaaaaaaaaaaaaaaaaaaa
            /*
            transVertsAmount += 6;
            opaqueBuffer.put(combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), faceType)));

             */
        } else {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), faceType)));
        }

    }


    public enum BlockType {
        AIR,
        GRASS,
        DIRT,
        STONE,
        SIDEDIRT,
        PLANKS,
        COBBLESTONE,
        WATER,
        SNOW,
        SIDESNOW,
        BEDROCK,
        SAND,
        LAVA,
        OBSIDIAN,
        HELLSTONE,
    }

    private enum Biomes{
        GRASSLANDS,
        ROCKLANDS,
        SNOWLANDS,
    }

    public class TextureCoords {
        public int x;
        public int y;

        public int tileAcross = 16;

        public TextureCoords(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    // Gather noise data

    private int getNoiseY(int x, int y, int z) {

        float frequency = 0.35f;
        float result = (
                1 * (noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency, (z + (World.chunkSizeZ * position.z)) * frequency) + 1))
                + 0.5f * ((noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 2, (z + (World.chunkSizeZ * position.z)) * frequency * 2) + 1))
                + 0.25f * ((noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 4, (z + (World.chunkSizeZ * position.z)) * frequency * 4) + 1));

        result = (float) Math.pow(result, 2.33f);

        return (int) Math.floor(result * 2);
    }
    private int getMoistNoiseY(int x, int y, int z) {

        float frequency = 0.15f;
        float result = (
                1 * (moistNoise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency, (z + (World.chunkSizeZ * position.z)) * frequency) + 1))
                + 0.5f * ((moistNoise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 2, (z + (World.chunkSizeZ * position.z)) * frequency * 2) + 1))
                + 0.25f * ((moistNoise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 4, (z + (World.chunkSizeZ * position.z)) * frequency * 4) + 1));

        result = (float) Math.pow(result, 3.24f);

        return (int) Math.floor(result * 2);
    }

    private int getRidgeNoiseY(int x, int y, int z) {

        float frequency = 0.15f;
        float result = (
                1 * (ridgeNoise((x + (World.chunkSizeX * position.x)) * frequency, (z + (World.chunkSizeZ * position.z)) * frequency) + 1))
                + 0.5f * ((ridgeNoise((x + (World.chunkSizeX * position.x)) * frequency * 2, (z + (World.chunkSizeZ * position.z)) * frequency * 2) + 1))
                + 0.25f * ((ridgeNoise((x + (World.chunkSizeX * position.x)) * frequency * 4, (z + (World.chunkSizeZ * position.z)) * frequency * 4) + 1));

        result = (float) Math.pow(result, 3.24f);

        return (int) Math.floor(result * 2);
    }

    private float ridgeNoise(float nx,float ny){

        return (float) (2 * (0.5 - Math.abs(0.5 - noise.GetNoise(nx, ny))));
    }




    public Chunk(Vector3i position) {

        blockTextures.put(BlockType.GRASS, new TextureCoords(0, 0));
        blockTextures.put(BlockType.STONE, new TextureCoords(1, 0));
        blockTextures.put(BlockType.DIRT, new TextureCoords(2, 0));
        blockTextures.put(BlockType.SIDEDIRT, new TextureCoords(3, 0));
        blockTextures.put(BlockType.PLANKS, new TextureCoords(4, 0));
        blockTextures.put(BlockType.COBBLESTONE, new TextureCoords(0, 1));
        blockTextures.put(BlockType.WATER, new TextureCoords(14, 12));
        blockTextures.put(BlockType.SNOW, new TextureCoords(2, 4));
        blockTextures.put(BlockType.SIDESNOW, new TextureCoords(4, 4));
        blockTextures.put(BlockType.BEDROCK, new TextureCoords(1, 1));
        blockTextures.put(BlockType.SAND, new TextureCoords(2, 1));
        blockTextures.put(BlockType.LAVA, new TextureCoords(15, 15));
        blockTextures.put(BlockType.OBSIDIAN, new TextureCoords(5, 2));
        blockTextures.put(BlockType.HELLSTONE, new TextureCoords(7, 6));

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

        moistNoise = new FastNoiseLite();
        moistNoise.SetSeed(234234);


        this.position = position;

        //System.out.println("Created chunk at: " + position);

        // create empty blocks of air
        for (int x = 0; x < World.chunkSizeX; x++) {
            for (int y = 0; y < World.worldSizeY; y++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {
                    chunkData[x][y][z] = BlockType.AIR;
                }
            }
        }


        // modify blocks
        for (int x = 0; x < World.chunkSizeX; x++) {
            for (int y = 0; y < World.chunkSizeY; y++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {
                    int noiseY = getNoiseY(x, 0, z);
                    int ridgeNoiseY = getRidgeNoiseY(x, 0, z);
                    int moistNoiseY = getMoistNoiseY(x,0,z);
                    int adjustedY = noiseY + y;


                    switch(getBiome(moistNoiseY)){

                        case GRASSLANDS:

                            if (adjustedY < 64) {
                                chunkData[x][adjustedY][z] = BlockType.STONE;
                                chunkData[x][y][z] = BlockType.STONE;

                            } else if (y == World.chunkSizeY - 1 && adjustedY < 78) {
                                chunkData[x][adjustedY][z] = BlockType.SIDEDIRT;
                            } else if (y == World.chunkSizeY - 1) {
                                chunkData[x][adjustedY][z] = BlockType.SIDESNOW;
                            } else {
                                chunkData[x][adjustedY][z] = BlockType.DIRT;
                            }

                            // water
                            if(noiseY < 5 && adjustedY > 62) {
                                chunkData[x][adjustedY][z] = BlockType.SAND;
                                chunkData[x][adjustedY-1][z] = BlockType.SAND;
                                if(noiseY < 4){ // Fill with water
                                    for(int i = 0; i < 5-noiseY; i++){
                                        chunkData[x][adjustedY+i][z] =BlockType.WATER;
                                    }

                                }
                            }
                            break;

                        case ROCKLANDS:

                            int noise = ridgeNoiseY + y;

                            if (noise < 64) {
                                chunkData[x][noise][z] = BlockType.STONE;
                                chunkData[x][y][z] = BlockType.LAVA;

                            } else if (y == World.chunkSizeY - 1 && adjustedY < 78) {
                                chunkData[x][noise][z] = BlockType.HELLSTONE;
                            } else if (y == World.chunkSizeY - 1) {
                                chunkData[x][noise][z] = BlockType.OBSIDIAN;
                            } else {
                                chunkData[x][noise][z] = BlockType.OBSIDIAN;
                            }
                            // water
                            if(noise < 5 && ridgeNoiseY > 62) {
                                chunkData[x][noise][z] = BlockType.OBSIDIAN;
                                chunkData[x][noise-1][z] = BlockType.OBSIDIAN;
                                if(noise < 4){ // Fill with water
                                    for(int i = 0; i < 5-noise; i++){
                                        chunkData[x][noise+i][z] = BlockType.LAVA;
                                    }

                                }
                            }

                            break;


                        case SNOWLANDS:
                            break;

                    }

                    // dumb but whatever
                    chunkData[x][0][z] = BlockType.BEDROCK;
                    chunkData[x][1][z] = BlockType.BEDROCK;
                    chunkData[x][2][z] = BlockType.BEDROCK;

                }
            }
        }


        //generateData();


    }

    private Biomes getBiome(int noise){


        /*
        if(noise > 20)
            return Biomes.ROCKLANDS;

         */





        return Biomes.GRASSLANDS;
    }


    public void generateMesh() {

        if(opaqueVertArray != null){
            opaqueVAO = glGenVertexArrays();
            glBindVertexArray(opaqueVAO);

            opaqueVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, opaqueVBO);
            glBufferData(GL_ARRAY_BUFFER, opaqueVertArray, GL_DYNAMIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // normal attribute
            glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            // color attribute
            glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);
        }






        if(transVertArray != null) {
            transVAO = glGenVertexArrays();
            glBindVertexArray(transVAO);

            transVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, transVBO);
            glBufferData(GL_ARRAY_BUFFER, transVertArray, GL_DYNAMIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // normal attribute
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            // color attribute
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);


            glBindVertexArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            transVertArray = null;
            opaqueVertArray = null;
            transVertList.clear();
            opaqueVertList.clear();

        }

    }




    void generateData() {


        opaqueVertList = new ArrayList<>();
        transVertList  = new ArrayList<>();

        faceCull();

        int i = 0;
        opaqueVertArray = new float[opaqueVertList.size()];
        for( Float vert : opaqueVertList){
            opaqueVertArray[i] = vert;
            i++;
        }
        i = 0;

        transVertArray = new float[transVertList.size()];
        for( Float vert : transVertList){
            transVertArray[i] = vert;
            i++;
        }
    }

    private void faceCull(){


        for (int x = 0; x < World.chunkSizeX; x++) {
            int xPos = x + (World.chunkSizeX * position.x);
            for (int y = 0; y < World.worldSizeY; y++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {
                    int zPos = z + (World.chunkSizeZ * position.z);




                    if (chunkData[x][y][z] != BlockType.AIR) {


                        if (( backChunk != null && z == 0 && backChunk.chunkData[x][y][World.chunkSizeZ-1] == BlockType.AIR) || z != 0 && (chunkData[x][y][z-1] == BlockType.AIR || (isLiquid(chunkData[x][y][z-1]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.BACK);

                        if (( frontChunk != null && z == World.chunkSizeZ - 1 && frontChunk.chunkData[x][y][0] == BlockType.AIR) || z != World.chunkSizeZ - 1 && (chunkData[x][y][z+1] == BlockType.AIR || (isLiquid(chunkData[x][y][z+1]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.FRONT);

                        if (( leftChunk != null && x == 0 && leftChunk.chunkData[World.chunkSizeX-1][y][z] == BlockType.AIR) || x != 0 && (chunkData[x-1][y][z] == BlockType.AIR || (isLiquid(chunkData[x-1][y][z]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.LEFT);

                        if (( rightChunk != null && x == World.chunkSizeX - 1 && rightChunk.chunkData[0][y][z] == BlockType.AIR) || x != World.chunkSizeX - 1 && (chunkData[x+1][y][z] == BlockType.AIR || (isLiquid(chunkData[x+1][y][z]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.RIGHT);

                        if ((y == 0 || chunkData[x][y-1][z] == BlockType.AIR))
                            generateFace(xPos, y, zPos, FaceType.BOTTOM);

                        // only render top if above is air
                        if ((y == chunkData[0].length || chunkData[x][y+1][z] == BlockType.AIR) || isLiquid(chunkData[x][y+1][z]) && !isLiquid(chunkData[x][y][z]))
                            generateTopFace(xPos, y, zPos);
                    }
                }
            }
        }
    }


    public static boolean isLiquid (BlockType block){

        return block == BlockType.WATER || block == BlockType.LAVA;
    }




    public void update() {
            destroyMesh();
            generateData();
            generateMesh();
        }



        public void destroyMesh () {
            glDeleteVertexArrays(opaqueVAO);
            glDeleteBuffers(opaqueVBO);
            glDeleteVertexArrays(transVAO);
            glDeleteBuffers(transVBO);
            opaqueVertsAmount = 0;
            transVertsAmount = 0;
        }

        public void destroyObject(){
        destroyMesh();
            backChunk = null;
            frontChunk = null;
            rightChunk = null;
            leftChunk = null;
        }

    }
