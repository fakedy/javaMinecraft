import java.util.*;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL46.*;


public class Chunk {

    public float[] opaqueVerts;

    public float[] transVerts;

    public int opaqueVertsAmount = 0;
    public int transVertsAmount = 0;

    ArrayList<Float> opaqueVerticesList = new ArrayList<>();
    ArrayList<Float> transVerticesList = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<BlockType>>> chunkData = new ArrayList<>();
    Map<BlockType, TextureCoords> blockTextures = new HashMap<>();

    public int VAO;
    public int opaqueVBO;

    public Vector3i position;
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

        BlockType blockType = chunkData.get(x - (World.chunkSizeX * position.x)).get(z - (World.chunkSizeZ * position.z)).get(y);

        if(blockType == BlockType.WATER && chunkData.get(x - (World.chunkSizeX * position.x)).get(z - (World.chunkSizeZ * position.z)).get(y+1) == BlockType.AIR) {

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
        Float[] vertsList;

        if (blockType == BlockType.SIDEDIRT) {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.GRASS), FaceType.TOP));
        } else if (blockType == BlockType.SIDESNOW) {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.SNOW), FaceType.TOP));
        } else {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.TOP));
        }
        if(blockType == BlockType.WATER){
            Collections.addAll(transVerticesList, vertsList);
            transVertsAmount += 6;
        } else {
            Collections.addAll(opaqueVerticesList, vertsList);
            opaqueVertsAmount += 6;
        }

    }



    private void generateFace(int x, int y, int z, FaceType faceType){

        BlockType blockType = chunkData.get(x - (World.chunkSizeX * position.x)).get(z - (World.chunkSizeZ * position.z)).get(y);

        float[] verts = new float[36];

        if(blockType == BlockType.WATER && chunkData.get(x - (World.chunkSizeX * position.x)).get(z - (World.chunkSizeZ * position.z)).get(y+1) == BlockType.AIR){ // no reason to have this atm but maybe in future.
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


        Float[] vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), faceType));

        if(blockType == BlockType.WATER){ // disabled water because why would we need water sides atm aaaaaaaaaaaaaaaaaaaa
            //Collections.addAll(transVerticesList, vertsList);
            //transVertsAmount += 6;
        } else {
            Collections.addAll(opaqueVerticesList, vertsList);
            opaqueVertsAmount += 6;
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
                ArrayList<BlockType> yList = xList.get(z);
                for (int y = 0; y < World.chunkSizeY; y++) {

                    int noiseY = getNoiseY(x, y, z);
                    int adjustedY = noiseY + y;


                    if (adjustedY < 64) {
                        yList.set(adjustedY, BlockType.STONE);
                        yList.set(y, BlockType.STONE);

                    } else if (y == World.chunkSizeY - 1 && adjustedY < 78) {
                        yList.set(adjustedY, BlockType.SIDEDIRT);
                    } else if (y == World.chunkSizeY - 1) {
                        yList.set(adjustedY, BlockType.SIDESNOW);
                    } else {
                        yList.set(adjustedY, BlockType.DIRT);
                    }

                    // water
                    if(noiseY < 5 && adjustedY > 62) {
                        yList.set(adjustedY, BlockType.SAND);
                        yList.set(adjustedY-1, BlockType.SAND);
                        if(noiseY < 4){ // Fill with water
                            for(int i = 0; i < 5-noiseY; i++){
                                yList.set(adjustedY+i, BlockType.WATER);
                            }

                        }
                    }

                    // dumb but whatever
                    yList.set(0, BlockType.BEDROCK);
                    yList.set(1, BlockType.BEDROCK);
                    yList.set(2, BlockType.BEDROCK);

                }
            }
        }


        generateData();


    }


    public void generateMesh() {


        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        opaqueVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, opaqueVBO);
        glBufferData(GL_ARRAY_BUFFER, opaqueVerts, GL_DYNAMIC_DRAW);

        GL46.glVertexAttribBinding(0,0);

        // position attribute
        glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // normal attribute
        glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // color attribute
        glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);


        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        opaqueVerts = null;
        transVerts = null;
    }

    private void generateData() {

            faceCull();


            opaqueVerts = new float[opaqueVerticesList.size()+transVerticesList.size()];
            int i = 0;
            for (Float f : opaqueVerticesList) {
                opaqueVerts[i++] = f;
            }

            for (Float f : transVerticesList) {
                opaqueVerts[i++] = f;
            }



    }

    private void faceCull(){

        for (int x = 0; x < chunkData.size(); x++) {
            int xPos = x + (World.chunkSizeX * position.x);
            for (int z = 0; z < chunkData.get(0).size(); z++) {
                int zPos = z + (World.chunkSizeZ * position.z);
                List<BlockType> column = chunkData.get(x).get(z);
                for (int y = 0; y < column.size(); y++) {

                    boolean notWater = column.get(y) != BlockType.WATER;

                    // there are probably uncessary checks here and i somehow got caves under mountains without it being intended.
                    if (column.get(y) != BlockType.AIR) {
                        if (z == 0 || (chunkData.get(x).get(z - 1).get(y) == BlockType.AIR || (chunkData.get(x).get(z - 1).get(y) == BlockType.WATER && notWater)))
                            generateFace(xPos, y, zPos, FaceType.BACK);

                        if (z == World.chunkSizeZ - 1 || chunkData.get(x).get(z + 1).get(y) == BlockType.AIR || (chunkData.get(x).get(z + 1).get(y) == BlockType.WATER && notWater))
                            generateFace(xPos, y, zPos, FaceType.FRONT);

                        if (x == 0 || chunkData.get(x - 1).get(z).get(y) == BlockType.AIR || (chunkData.get(x - 1).get(z).get(y) == BlockType.WATER && notWater))
                            generateFace(xPos, y, zPos, FaceType.LEFT);

                        if (x == World.chunkSizeX - 1 || chunkData.get(x + 1).get(z).get(y) == BlockType.AIR || (chunkData.get(x + 1).get(z).get(y) == BlockType.WATER && notWater))
                            generateFace(xPos, y, zPos, FaceType.RIGHT);

                        if ((y == 0 || column.get(y - 1) == BlockType.AIR))
                            generateFace(xPos, y, zPos, FaceType.BOTTOM);

                        // only render top if above is air
                        if ((y == column.size() || column.get(y + 1) == BlockType.AIR) || (column.get(y + 1) == BlockType.WATER && notWater))
                            generateTopFace(xPos, y, zPos);
                    }
                }
            }
        }
    }




    public void update() {
            destroyMesh();
            generateData();
            generateMesh();
        }



        public void destroyMesh () {
            glDeleteVertexArrays(VAO);
            glDeleteBuffers(opaqueVBO);
            opaqueVerticesList.clear();
            transVerticesList.clear();
            opaqueVertsAmount = 0;
            transVertsAmount = 0;

        }
    }
