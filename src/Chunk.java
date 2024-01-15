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

    Blocks.BlockType[][][] chunkData;

    public int opaqueVAO;

    public int opaqueVBO;

    public int transVAO;
    public int transVBO;

    public Vector3i position;


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



    private void generateTopFace(int x, int y, int z) {

        float[] verts;

        Blocks.BlockType blockType = chunkData[x - (World.chunkSizeX * position.x)][y][z - (World.chunkSizeZ * position.z)];

        if((blockType == Blocks.BlockType.WATER || blockType == Blocks.BlockType.LAVA) && chunkData[(x - (World.chunkSizeX * position.x))][y+1][((z - (World.chunkSizeZ * position.z)))] == Blocks.BlockType.AIR) {

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


        if (blockType == Blocks.BlockType.SIDEDIRT) {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(Blocks.blockTextures.get(Blocks.BlockType.GRASS), FaceType.TOP)));
        } else if (blockType == Blocks.BlockType.SIDESNOW) {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(Blocks.blockTextures.get(Blocks.BlockType.SNOW), FaceType.TOP)));
        } else if(isLiquid(blockType)){
            transVertsAmount += 6;
            transVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(Blocks.blockTextures.get(blockType), FaceType.TOP)));
        }  else {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(Blocks.blockTextures.get(blockType), FaceType.TOP)));
        }

    }



    private void generateFace(int x, int y, int z, FaceType faceType){

        Blocks.BlockType blockType = chunkData[(x - (World.chunkSizeX * position.x))][y][(z - (World.chunkSizeZ * position.z))];

        float[] verts = new float[36];

        if((isLiquid(blockType)) && chunkData[(x - (World.chunkSizeX * position.x))][y+1][(z - (World.chunkSizeZ * position.z))] == Blocks.BlockType.AIR){ // no reason to have this atm but maybe in future.
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
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(Blocks.blockTextures.get(blockType), faceType)));
        }

    }



    public Chunk(Vector3i position) {


        this.position = position;

        //System.out.println("Created chunk at: " + position);


        chunkData = Terrain.initData();

        Terrain.shapeTerrain(chunkData, position);

        //generateData();
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




                    if (chunkData[x][y][z] != Blocks.BlockType.AIR) {


                        if (( backChunk != null && z == 0 && (backChunk.chunkData[x][y][World.chunkSizeZ-1] == Blocks.BlockType.AIR || isLiquid(backChunk.chunkData[x][y][World.chunkSizeZ-1]))) || z != 0 && (chunkData[x][y][z-1] == Blocks.BlockType.AIR || (isLiquid(chunkData[x][y][z-1]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.BACK);

                        if (( frontChunk != null && z == World.chunkSizeZ - 1 && (frontChunk.chunkData[x][y][0] == Blocks.BlockType.AIR || isLiquid(frontChunk.chunkData[x][y][0]))) || z != World.chunkSizeZ - 1 && (chunkData[x][y][z+1] == Blocks.BlockType.AIR || (isLiquid(chunkData[x][y][z+1]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.FRONT);

                        if (( leftChunk != null && x == 0 && (leftChunk.chunkData[World.chunkSizeX-1][y][z] == Blocks.BlockType.AIR || isLiquid(leftChunk.chunkData[World.chunkSizeX-1][y][z]))) || x != 0 && (chunkData[x-1][y][z] == Blocks.BlockType.AIR || (isLiquid(chunkData[x-1][y][z]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.LEFT);

                        if (( rightChunk != null && x == World.chunkSizeX - 1 && (rightChunk.chunkData[0][y][z] == Blocks.BlockType.AIR || isLiquid(rightChunk.chunkData[0][y][z]))) || x != World.chunkSizeX - 1 && (chunkData[x+1][y][z] == Blocks.BlockType.AIR || (isLiquid(chunkData[x+1][y][z]) && !isLiquid(chunkData[x][y][z]))))
                            generateFace(xPos, y, zPos, FaceType.RIGHT);

                        if ((y == 0 || chunkData[x][y-1][z] == Blocks.BlockType.AIR))
                            generateFace(xPos, y, zPos, FaceType.BOTTOM);

                        // only render top if above is air
                        if ((y == chunkData[0].length || chunkData[x][y+1][z] == Blocks.BlockType.AIR) || isLiquid(chunkData[x][y+1][z]) && !isLiquid(chunkData[x][y][z]))
                            generateTopFace(xPos, y, zPos);
                    }
                }
            }
        }
    }


    public static boolean isLiquid (Blocks.BlockType block){

        return block == Blocks.BlockType.WATER || block == Blocks.BlockType.LAVA;
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
