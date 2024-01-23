package Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static org.lwjgl.opengl.GL46.*;

public class ChunkMesh {



    private ArrayList<Float> opaqueVertList;
    float[] opaqueVertArray;
    private ArrayList<Float> transVertList;

    float[] transVertArray;

    public int opaqueVertsAmount = 0;
    public int transVertsAmount = 0;
    public int opaqueVAO;

    public int opaqueVBO;

    public int transVAO;
    public int transVBO;


    private int[][][] processedFaces;

    Chunk owner;




    ChunkMesh(Chunk chunk){

        this.owner = chunk;
    }

    // Function to combine vertex positions and texture coordinates
    private Collection<? extends Float> combineVertexData(float[] positions, float[] texCoords) {
        Float[] combined = new Float[positions.length+texCoords.length];
        int index = 0;
        for (int i = 0, j = 0; i < positions.length; i += 6, j += 3) {
            combined[index++] = (positions[i]);     // x
            combined[index++] = (positions[i + 1]); // y
            combined[index++] = (positions[i + 2]); // z
            combined[index++] = (positions[i + 3]); // a
            combined[index++] = (positions[i + 4]); // b
            combined[index++] = (positions[i + 5]); // c
            combined[index++] = (texCoords[j]);     // u
            combined[index++] = (texCoords[j + 1]); // v
            combined[index++] = (texCoords[j + 2]); // i
        }
        return List.of(combined);
    }

    public enum FaceType {
        TOP, LEFT, FRONT, BACK, BOTTOM, RIGHT
    }

    public void destroyMesh () {
        glDeleteVertexArrays(opaqueVAO);
        glDeleteBuffers(opaqueVBO);
        glDeleteVertexArrays(transVAO);
        glDeleteBuffers(transVBO);
        opaqueVertsAmount = 0;
        transVertsAmount = 0;
    }

    public void generateMesh() {

        if(opaqueVertArray != null){
            opaqueVAO = glGenVertexArrays();
            glBindVertexArray(opaqueVAO);

            opaqueVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, opaqueVBO);
            glBufferData(GL_ARRAY_BUFFER, opaqueVertArray, GL_DYNAMIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // normal attribute
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            // texture attribute
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);
        }

        if(transVertArray != null) {
            transVAO = glGenVertexArrays();
            glBindVertexArray(transVAO);

            transVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, transVBO);
            glBufferData(GL_ARRAY_BUFFER, transVertArray, GL_DYNAMIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // normal attribute
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            // color attribute
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);


            glBindVertexArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            transVertArray = null;
            opaqueVertArray = null;
            transVertList.clear();
            opaqueVertList.clear();

        }

    }

    public void generateData(){
        processedFaces = new int[World.chunkSizeX][World.worldSizeY][World.chunkSizeZ];


        opaqueVertList = new ArrayList<>();
        transVertList  = new ArrayList<>();

        faceCull();

        processedFaces = null;

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


    private void generateFace(int x, int y, int z, FaceType faceType, int lengthX, int lengthY, int lengthZ){

        Blocks.BlockType blockType = owner.chunkData[(x - (World.chunkSizeX * owner.position.x))][y][(z - (World.chunkSizeZ * owner.position.z))];

        float[] verts = new float[36];

        if((Chunk.isLiquid(blockType)) && owner.chunkData[(x - (World.chunkSizeX * owner.position.x))][y+1][(z - (World.chunkSizeZ * owner.position.z))] == Blocks.BlockType.AIR){ // no reason to have this atm but maybe in future.
            switch (faceType) {

                case FRONT:
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x + lengthX, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x + lengthX, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x + lengthX, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };
                    break;
                case LEFT:
                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.35f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.35f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.35f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f, // top-right

                    };
                    break;
                case RIGHT:
                    verts = new float[]{
                            0.5f + x, 0.35f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z , 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.35f + y, -0.5f + z , 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z , 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.35f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f,// bottom-left

                    };
                    break;
                case BACK:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x+ lengthX, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x+ lengthX, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x+ lengthX, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };
                    break;
                case BOTTOM:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,   // top-right
                            0.5f + x+ lengthX, -0.5f + y, -0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f, // top-left
                            0.5f + x+ lengthX, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x+ lengthX, -0.5f + y, 0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, -0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f,  // top-right

                    };
                    break;
            }

        } else {

            switch (faceType) {
                case FRONT:
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x+ lengthX, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x+ lengthX, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x+ lengthX, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };
                    break;
                case LEFT:
                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.5f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.5f + y, 0.5f + z + lengthZ, -1.0f, 0.0f, 0.0f, // top-right

                    };
                    break;
                case RIGHT:
                    verts = new float[]{
                            0.5f + x, 0.5f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.5f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z + lengthZ, 1.0f, 0.0f, 0.0f,// bottom-left

                    };
                    break;
                case BACK:
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x+ lengthX , 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x+ lengthX, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x+ lengthX, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };
                    break;
                case BOTTOM:
                    verts = new float[]{
                            // Bottom face
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x + lengthX, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            0.5f + x + lengthX, -0.5f + y, 0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f, // bottom-right
                            0.5f + x + lengthX, -0.5f + y, 0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, 0.5f + z + lengthZ, 0.0f, -1.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f // bottom-left
                    };
                    break;
            }
        }




        if(owner.isLiquid(blockType)){ // disabled water because why would we need water sides atm aaaaaaaaaaaaaaaaaaaa
            /*
            transVertsAmount += 6;
            opaqueBuffer.put(combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), faceType)));

             */
        } else {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(blockType,faceType, lengthX, lengthY,lengthZ)));
        }

    }


    private void generateTopFace(int x, int y, int z, int lengthX, int lengthY, int lengthZ) {

        float[] verts;

        Blocks.BlockType blockType = owner.chunkData[x - (World.chunkSizeX * owner.position.x)][y][z - (World.chunkSizeZ * owner.position.z)];

        if((blockType == Blocks.BlockType.WATER || blockType == Blocks.BlockType.LAVA) && owner.chunkData[(x - (World.chunkSizeX * owner.position.x))][y+1][((z - (World.chunkSizeZ * owner.position.z)))] == Blocks.BlockType.AIR) {

            verts = new float[]{
                    -0.5f + x, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    0.5f + x + lengthX , y + 0.35f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-right
                    0.5f + x + lengthX, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-right
                    0.5f + x + lengthX, y + 0.35f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-right
                    -0.5f + x, y + 0.35f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    -0.5f + x, y + 0.35f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-left

            };

        } else {

            verts = new float[]{
                    -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    0.5f + x + lengthX, y + 0.5f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-right
                    0.5f + x + lengthX, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-right
                    0.5f + x + lengthX, y + 0.5f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-right
                    -0.5f + x, y + 0.5f, -0.5f + z, 0.0f, 1.0f, 0.0f,// top-left
                    -0.5f + x, y + 0.5f, 0.5f + z+ lengthZ, 0.0f, 1.0f, 0.0f,// bottom-left

            };

        }


        // Define the vertices for the top face of the block

        // Generate the default verts array



        if(owner.isLiquid(blockType)){
            transVertsAmount += 6;
            transVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(blockType,ChunkMesh.FaceType.TOP, lengthX,lengthY,lengthZ)));
        }  else {
            opaqueVertsAmount += 6;
            opaqueVertList.addAll(combineVertexData(verts, Blocks.getTextureCoords(blockType,ChunkMesh.FaceType.TOP, lengthX,lengthY,lengthZ)));

        }

    }

    private void faceCull(){


        for (int y = 0; y < World.worldSizeY; y++) {
            for (int x = 0; x < World.chunkSizeX; x++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {

                    if (owner.chunkData[x][y][z] != Blocks.BlockType.AIR) {

                        if (( owner.backChunk != null && z == 0 && (owner.backChunk.chunkData[x][y][World.chunkSizeZ-1] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.backChunk.chunkData[x][y][World.chunkSizeZ-1]))) || z != 0 && (owner.chunkData[x][y][z-1] == Blocks.BlockType.AIR || (Chunk.isLiquid(owner.chunkData[x][y][z-1]) && !Chunk.isLiquid(owner.chunkData[x][y][z]))))
                            greedBackFace(x,y,z);

                        if (( owner.frontChunk != null && z == World.chunkSizeZ - 1 && (owner.frontChunk.chunkData[x][y][0] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.frontChunk.chunkData[x][y][0]))) || z != World.chunkSizeZ - 1 && (owner.chunkData[x][y][z+1] == Blocks.BlockType.AIR || (Chunk.isLiquid(owner.chunkData[x][y][z+1]) && !Chunk.isLiquid(owner.chunkData[x][y][z]))))
                            greedFrontFace(x,y,z);

                        if (( owner.leftChunk != null && x == 0 && (owner.leftChunk.chunkData[World.chunkSizeX-1][y][z] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.leftChunk.chunkData[World.chunkSizeX-1][y][z]))) || x != 0 && (owner.chunkData[x-1][y][z] == Blocks.BlockType.AIR || (Chunk.isLiquid(owner.chunkData[x-1][y][z]) && !Chunk.isLiquid(owner.chunkData[x][y][z]))))
                            greedLeftFace(x,y,z);

                        if (( owner.rightChunk != null && x == World.chunkSizeX - 1 && (owner.rightChunk.chunkData[0][y][z] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.rightChunk.chunkData[0][y][z]))) || x != World.chunkSizeX - 1 && (owner.chunkData[x+1][y][z] == Blocks.BlockType.AIR || (Chunk.isLiquid(owner.chunkData[x+1][y][z]) && !Chunk.isLiquid(owner.chunkData[x][y][z]))))
                            greedRightFace(x,y,z);

                        if ((y != 0 && owner.chunkData[x][y-1][z] == Blocks.BlockType.AIR))
                            greedBotFace(x,y,z);

                        // only render top if above is air
                        if ((owner.chunkData[x][y+1][z] == Blocks.BlockType.AIR) || Chunk.isLiquid(owner.chunkData[x][y+1][z]) && !Chunk.isLiquid(owner.chunkData[x][y][z])){
                            greedTopFace(x, y, z);
                        }



                    }
                }
            }
        }
    }



    private void greedTopFace(int x, int y, int z){

        final int TOP_FACE = 16;    // 10000
        if((processedFaces[x][y][z] & TOP_FACE) == 0){
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthX = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block

            for(int xOffset = 1; xOffset < World.chunkSizeX - x; xOffset++){
                if((processedFaces[x + xOffset][y][z] & TOP_FACE) != 0){
                    break;
                }

                if (block == owner.chunkData[x + xOffset][y][z] && (owner.chunkData[x+ xOffset][y+1][z] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x+ xOffset][y+1][z]))){ // while next block is same type as one before
                    lengthX++;
                } else{
                    break;
                }
            }

            int lengthZ = World.chunkSizeZ - z; // Initialize to maximum possible length

            // Iterate over each x in the width
            for (int xOffset = 0; xOffset <= lengthX; xOffset++) {
                int depthZ = 0; // Reset for each x

                // Determine depth for this x
                for (int zOffset = 1; z + zOffset < World.chunkSizeZ; zOffset++) {
                    if((processedFaces[x + xOffset][y][z + zOffset] & TOP_FACE) != 0){
                        break;
                    }
                    if (block == owner.chunkData[x + xOffset][y][z + zOffset] && (owner.chunkData[x + xOffset][y + 1][z + zOffset] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x + xOffset][y + 1][z + zOffset]))) {
                        depthZ++;
                    } else {
                        break; // Break if conditions are not met
                    }
                }

                // Update overall lengthZ (minimum depth)
                if (depthZ < lengthZ) {
                    lengthZ = depthZ;
                }
            }

            generateTopFace(xPos, y, zPos, lengthX ,0,lengthZ);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= lengthX; i++){
                for(int j = 0; j <= lengthZ; j++){
                    processedFaces[x + i][y][z+j] |= TOP_FACE;
                }
            }
        }
    }

    private void greedBotFace(int x, int y, int z){

        final int BOTTOM_FACE = 32;    // 100000
        if((processedFaces[x][y][z] & BOTTOM_FACE) == 0){
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthX = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block

            for(int xOffset = 1; xOffset < World.chunkSizeX - x; xOffset++){
                if((processedFaces[x + xOffset][y][z] & BOTTOM_FACE) != 0){
                    break;
                }

                if(y == 0)
                    break;
                if (block == owner.chunkData[x + xOffset][y][z] && (owner.chunkData[x+ xOffset][y-1][z] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x+ xOffset][y-1][z]))){ // while next block is same type as one before
                    lengthX++;
                } else{
                    break;
                }
            }

            int lengthZ = World.chunkSizeZ - z; // Initialize to maximum possible length

            // Iterate over each x in the width
            for (int xOffset = 0; xOffset <= lengthX; xOffset++) {
                int depthZ = 0; // Reset for each x

                // Determine depth for this x
                for (int zOffset = 1; z + zOffset < World.chunkSizeZ; zOffset++) {
                    if((processedFaces[x + xOffset][y][z + zOffset] & BOTTOM_FACE) != 0){
                        break;
                    }
                    if(y == 0)
                        break;
                    if (block == owner.chunkData[x + xOffset][y][z + zOffset] && (owner.chunkData[x + xOffset][y - 1][z + zOffset] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x + xOffset][y - 1][z + zOffset]))) {
                        depthZ++;
                    } else {
                        break; // Break if conditions are not met
                    }
                }

                // Update overall lengthZ (minimum depth)
                if (depthZ < lengthZ) {
                    lengthZ = depthZ;
                }
            }

            generateFace(xPos, y, zPos, FaceType.BOTTOM,lengthX+0 ,0,lengthZ+0);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= lengthX; i++){
                for(int j = 0; j <= lengthZ; j++){
                    processedFaces[x + i][y][z+j] |= BOTTOM_FACE;
                }
            }
        }
    }

    private void greedFrontFace(int x, int y, int z){

        final int FRONT_FACE = 4;    // 00001
        if((processedFaces[x][y][z] & FRONT_FACE) == 0) {
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthX = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block

            for (int xOffset = 1; xOffset < World.chunkSizeX - x; xOffset++) {
                if ((processedFaces[x + xOffset][y][z] & FRONT_FACE) != 0) {
                    break;
                }

                // Shitty hack when at chunk border
                if(x+xOffset == 16)
                    break;
                if(z == 15)
                    break;
                if (block == owner.chunkData[x + xOffset][y][z] && (owner.chunkData[x + xOffset][y][z+1] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x + xOffset][y][z+1]))) { // while next block is same type as one before
                    lengthX++;
                } else {
                    break;
                }
            }
            generateFace(xPos, y, zPos, FaceType.FRONT,lengthX ,0,0);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= lengthX; i++){
                for(int j = 0; j <= 0; j++){
                    processedFaces[x + i][y][z+j] |= FRONT_FACE;
                }
            }
        }

    }

    private void greedBackFace(int x, int y, int z){

        final int BACK_FACE = 8;    // 1000
        if((processedFaces[x][y][z] & BACK_FACE) == 0) {
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthX = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block

            for (int xOffset = 1; xOffset < World.chunkSizeX - x; xOffset++) {
                if ((processedFaces[x + xOffset][y][z] & BACK_FACE) != 0) {
                    break;
                }

                // Shitty hack when at chunk border
                if(z == 0)
                    break;
                if (block == owner.chunkData[x + xOffset][y][z] && (owner.chunkData[x + xOffset][y][z-1] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x + xOffset][y][z-1]))) { // while next block is same type as one before
                    lengthX++;
                } else {
                    break;
                }
            }
            generateFace(xPos, y, zPos, FaceType.BACK,lengthX ,0,0);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= lengthX; i++){
                for(int j = 0; j <= 0; j++){
                    processedFaces[x + i][y][z+j] |= BACK_FACE;
                }
            }
        }

    }

    private void greedRightFace(int x, int y, int z){

        // This one feels weird, should be able to greed more sideways??

        final int RIGHT_FACE = 1;   // 0001
        if((processedFaces[x][y][z] & RIGHT_FACE) == 0) {
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthZ = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block


            for (int zOffset = 1; zOffset < World.chunkSizeZ - x; zOffset++) {
                if(z == 15)
                    break;
                if(z + zOffset == 16)
                    break;
                if ((processedFaces[x][y][z + zOffset] & RIGHT_FACE) != 0) {
                    break;
                }
                if (block == owner.chunkData[x][y][z + zOffset] && (owner.chunkData[x + 1][y][z + zOffset] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x+1][y][z + zOffset]))) { // while next block is same type as one before
                    lengthZ++;
                } else {
                    break;
                }
            }
            generateFace(xPos, y, zPos, FaceType.RIGHT,0 ,0,lengthZ);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= 0; i++){
                for(int j = 0; j <= lengthZ; j++){
                    processedFaces[x + i][y][z+j] |= RIGHT_FACE;
                }
            }
        }

    }

    private void greedLeftFace(int x, int y, int z){

        // This one feels weird, should be able to greed more sideways??

        final int LEFT_FACE = 2;    // 0010
        if((processedFaces[x][y][z] & LEFT_FACE) == 0) {
            int xPos = x + (World.chunkSizeX * owner.position.x); // local to world cords
            int zPos = z + (World.chunkSizeZ * owner.position.z); // local to world cords

            int lengthZ = 0; // set at 0 because if we on first block we shouldnt add.

            Blocks.BlockType block = owner.chunkData[x][y][z]; // the starting block


            for (int zOffset = 1; zOffset < World.chunkSizeZ - x; zOffset++) {
                if(x == 0)
                    break;
                if(z + zOffset == 16)
                    break;
                if ((processedFaces[x][y][z + zOffset] & LEFT_FACE) != 0) {
                    break;
                }
                if (block == owner.chunkData[x][y][z + zOffset] && (owner.chunkData[x - 1][y][z + zOffset] == Blocks.BlockType.AIR || Chunk.isLiquid(owner.chunkData[x-1][y][z + zOffset]))) { // while next block is same type as one before
                    lengthZ++;
                } else {
                    break;
                }
            }
            generateFace(xPos, y, zPos, FaceType.LEFT,0 ,0,lengthZ);   // generate face from xPos to xPos + lengthX same with z
            for(int i = 0; i <= 0; i++){
                for(int j = 0; j <= lengthZ; j++){
                    processedFaces[x + i][y][z+j] |= LEFT_FACE;
                }
            }
        }

    }
}
