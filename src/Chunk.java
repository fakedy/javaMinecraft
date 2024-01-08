import java.util.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33.*;


public class Chunk {


    public float[] verts;
    public int vertsAmount;
    ArrayList<ArrayList<ArrayList<BlockType>>> chunkData = new ArrayList<>();
    Map<BlockType, TextureCoords> blockTextures = new HashMap<>();

    private int[][][] noiseArray = new int[World.chunkSizeX][World.chunkSizeY][World.chunkSizeZ];

    public int VAO;
    public int VBO;
    public Vector3f position;
    FastNoiseLite noise;

    // Function to combine vertex positions and texture coordinates
    public Float[] combineVertexData(float[] positions, float[] texCoords) {
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
        return combined;
    }

    float[] defaultTexCoords = {
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };

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

        // Define the vertices for the top face of the block
        float[] verts = {
                -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-right
                0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-right
                -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                -0.5f + x, y + 0.5f, 0.5f + z, 0.0f, 1.0f, 0.0f,// bottom-left

        };
        // Generate the default verts array
        Float[] vertsList;
        BlockType blockType = chunkData.get(x - (int) (World.chunkSizeX * position.x)).get(z - (int) (World.chunkSizeZ * position.z)).get(y);
        if (blockType == BlockType.SIDEDIRT) {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.GRASS), FaceType.TOP));
        } else if (blockType == BlockType.SIDESNOW) {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.SNOW), FaceType.TOP));
        } else {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.TOP));
        }

        Collections.addAll(verticesList, vertsList);


    }



    private void generateFace(int x, int y, int z, FaceType faceType){

        float[] verts = new float[36];

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
                verts = new float[] {
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

        BlockType blockType = chunkData.get(x - (int) (World.chunkSizeX * position.x)).get(z - (int) (World.chunkSizeZ * position.z)).get(y);
        Float[] vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), faceType));

        Collections.addAll(verticesList, vertsList);

    }



    ArrayList<Float> verticesList = new ArrayList<>();

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

    public int getNoiseY(int x, int y, int z) {

        float frequency = 0.65f;
        float result = (
                1 * (noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency, (z + (World.chunkSizeZ * position.z)) * frequency) + 1))
                + 0.5f * ((noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 2, (z + (World.chunkSizeZ * position.z)) * frequency * 2) + 1))
                + 0.25f * ((noise.GetNoise((x + (World.chunkSizeX * position.x)) * frequency * 4, (z + (World.chunkSizeZ * position.z)) * frequency * 4) + 1));

        result = (float) Math.pow(result, 2.64f);

        return (int) Math.floor(result * 2);
    }


    public Chunk(Vector3f position) {

        blockTextures.put(BlockType.GRASS, new TextureCoords(0, 0));
        blockTextures.put(BlockType.STONE, new TextureCoords(1, 0));
        blockTextures.put(BlockType.DIRT, new TextureCoords(2, 0));
        blockTextures.put(BlockType.SIDEDIRT, new TextureCoords(3, 0));
        blockTextures.put(BlockType.PLANKS, new TextureCoords(4, 0));
        blockTextures.put(BlockType.COBBLESTONE, new TextureCoords(0, 1));
        blockTextures.put(BlockType.WATER, new TextureCoords(15, 13));
        blockTextures.put(BlockType.SNOW, new TextureCoords(2, 4));
        blockTextures.put(BlockType.SIDESNOW, new TextureCoords(4, 4));
        blockTextures.put(BlockType.BEDROCK, new TextureCoords(1, 1));

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);


        this.position = position;

        //System.out.println("Created chunk at: " + position);

        // create empty blocks of air
        for (int x = 0; x < World.chunkSizeX; x++) {
            ArrayList<ArrayList<BlockType>> xList = new ArrayList<>();
            for (int z = 0; z < World.chunkSizeZ; z++) {
                ArrayList<BlockType> zList = new ArrayList<>();
                for (int y = 0; y < World.worldSizeY; y++) {
                    zList.add(BlockType.AIR);
                }
                xList.add(zList);
            }
            chunkData.add(xList);
        }

        // modify blocks
        for (int x = 0; x < World.chunkSizeX; x++) {
            ArrayList<ArrayList<BlockType>> xList = chunkData.get(x);
            for (int z = 0; z < World.chunkSizeZ; z++) {
                ArrayList<BlockType> zList = xList.get(z);
                for (int y = 0; y < World.chunkSizeY; y++) {

                    int noiseY = getNoiseY(x, y, z);
                    noiseArray[x][y][z] = getNoiseY(x, y, z);

                    if (noiseY + y < 64) {
                        zList.set(y + noiseY, BlockType.STONE);
                    } else if (y == World.chunkSizeY - 1 && noiseY + y < 78) {
                        zList.set(y + noiseY, BlockType.SIDEDIRT);
                    } else if (y == World.chunkSizeY - 1) {
                        zList.set(y + noiseY, BlockType.SIDESNOW);
                    } else {
                        zList.set(y + noiseY, BlockType.DIRT);

                    }

                }
            }
        }


        generateData();


    }


    public void generateMesh() {

        VAO = GL33.glGenVertexArrays();
        VBO = GL33.glGenBuffers();

        // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

        // position attribute
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 0);

        glEnableVertexAttribArray(0);

        // normal attribute
        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // color attribute
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        vertsAmount = verts.length;
        verts = null;
    }

    private void generateData() {

        for (int x = 0; x < World.chunkSizeX; x++) {
            int xPos = x + (int) (World.chunkSizeX * position.x);
            for (int z = 0; z < World.chunkSizeZ; z++) {
                int zPos = z + (int) (World.chunkSizeZ * position.z);
                List<BlockType> column = chunkData.get(x).get(z);
                for (int y = 0; y < World.chunkSizeY; y++) {

                    int height = noiseArray[x][y][z];
                    int yPos = y + height;


                    if (chunkData.get(x).get(z).get(yPos) != BlockType.AIR) {

                        if (z == 0 || (z < World.chunkSizeZ && chunkData.get(x).get(z - 1).get(yPos) == BlockType.AIR))
                            generateFace(xPos, yPos, zPos, FaceType.BACK);

                        if (z == World.chunkSizeZ - 1 || (z < World.chunkSizeZ - 1 && chunkData.get(x).get(z + 1).get(yPos) == BlockType.AIR))
                            generateFace(xPos, yPos, zPos, FaceType.FRONT);

                        if (x == 0 || (x < World.chunkSizeX && chunkData.get(x - 1).get(z).get(yPos) == BlockType.AIR))
                            generateFace(xPos, yPos, zPos, FaceType.LEFT);

                        if (x == World.chunkSizeX - 1 || (x < World.chunkSizeX - 1 && chunkData.get(x + 1).get(z).get(yPos) == BlockType.AIR))
                            generateFace(xPos, yPos, zPos, FaceType.RIGHT);

                        if (yPos == height || (yPos < World.chunkSizeY + height && column.get(yPos-1) == BlockType.AIR))
                            generateFace(xPos, yPos, zPos, FaceType.BOTTOM);

                        // only render top if above is air
                        if (yPos == World.chunkSizeY - 1 + height || (yPos < World.chunkSizeY - 1 + height && column.get(yPos+1) == BlockType.AIR))
                            generateTopFace(xPos, yPos, zPos);


                    }
                }
            }


            verts = new float[verticesList.size()];
            int i = 0;
            for (Float f : verticesList) {
                verts[i++] = f; // Handling null values, replace with 0.0f
            }
        }
    }


        public void update() {
            destroyMesh();
            generateData();
            generateMesh();
        }



        public void destroyMesh () {
            GL33.glDeleteVertexArrays(VAO);
            GL33.glDeleteBuffers(VBO);
            verticesList.clear();

        }
    }
