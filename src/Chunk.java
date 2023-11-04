import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;



public class Chunk {


    public float[] verts;
    Map<BlockType, TextureCoords> blockTextures = new HashMap<>();

    // Function to combine vertex positions and texture coordinates
    public List<Float> combineVertexData(float[] positions, float[] texCoords) {
        List<Float> combined = new ArrayList<>();
        for (int i = 0, j = 0; i < positions.length; i += 6, j += 2) {
            combined.add(positions[i]);     // x
            combined.add(positions[i + 1]); // y
            combined.add(positions[i + 2]); // z
            combined.add(positions[i + 3]); // a
            combined.add(positions[i + 4]); // b
            combined.add(positions[i + 5]); // c
            combined.add(texCoords[j]);     // u
            combined.add(texCoords[j + 1]); // v
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
    public float[] getTextureCoords(TextureCoords cords, FaceType faceType){
        float tileWidth = 1.0f / cords.tileAcross; // prob 16
        float startX = cords.x * tileWidth;
        float startY = cords.y * tileWidth;
        float endX = startX + tileWidth;
        float endY = startY + tileWidth;

        switch(faceType) {
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
                return new float[] {
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case FRONT:
                // Assuming FRONT needs to be flipped horizontally
                return new float[] {
                        startX, endY,
                        endX, endY,
                        endX, startY,
                        endX, startY,
                        startX, startY,
                        startX, endY
                };
            case BACK:
                // Assuming BACK face orientation is same as FRONT
                return new float[] {
                        endX, endY,
                        startX, startY,
                        startX, endY,
                        startX, startY,
                        endX, endY,
                        endX, startY
                };
            case BOTTOM:
                // Assuming BOTTOM face orientation is same as TOP
                return new float[] {
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case RIGHT:
                // Assuming RIGHT face orientation is same as LEFT
                return new float[] {
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

    private void generateTopFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                -0.5f+x,  y+0.5f, -0.5f+z, 0.0f,  1.0f,  0.0f,// top-left
                0.5f+x,  y+0.5f,  0.5f+z,  0.0f,  1.0f,  0.0f,// bottom-right
                0.5f+x,  y+0.5f, -0.5f+z,  0.0f,  1.0f,  0.0f,// top-right
                0.5f+x,  y+0.5f,  0.5f+z,  0.0f,  1.0f,  0.0f,// bottom-right
                -0.5f+x,  y+0.5f, -0.5f+z, 0.0f,  1.0f,  0.0f,// top-left
                -0.5f+x,  y+0.5f,  0.5f+z, 0.0f,  1.0f,  0.0f,// bottom-left

        };
        // Generate the default verts array
        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);
        if(blockType == BlockType.SIDEDIRT) {
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.GRASS), FaceType.TOP));
        } else if(blockType == BlockType.SIDESNOW){
            vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(BlockType.SNOW), FaceType.TOP));
        } else {
             vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.TOP));
        }

        verticesList.addAll(vertsList);

    }
    private void generateFrontFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                // Front face
                -0.5f+x, -0.5f+y,  0.5f+z, 0.0f,  0.0f,  1.0f, // bottom-left
                0.5f+x, -0.5f+y,  0.5f+z,  0.0f,  0.0f,  1.0f,// bottom-right
                0.5f+x,  0.5f+y,  0.5f+z,  0.0f,  0.0f,  1.0f,// top-right
                0.5f+x,  0.5f+y,  0.5f+z,  0.0f,  0.0f,  1.0f,// top-right
                -0.5f+x,  0.5f+y,  0.5f+z, 0.0f,  0.0f,  1.0f,// top-left
                -0.5f+x, -0.5f+y,  0.5f+z, 0.0f,  0.0f,  1.0f,// bottom-left

        };
        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);
        vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.FRONT));
        verticesList.addAll(vertsList);

    }
    private void generateLeftFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                // Left face
                -0.5f+x,  0.5f+y,  0.5f+z,  -1.0f,  0.0f,  0.0f, // top-right
                -0.5f+x ,  0.5f+y, -0.5f+z, -1.0f,  0.0f,  0.0f, // top-left
                -0.5f+x , -0.5f+y, -0.5f+z, -1.0f,  0.0f,  0.0f, // bottom-left
                -0.5f+x , -0.5f+y, -0.5f+z, -1.0f,  0.0f,  0.0f,// bottom-left
                -0.5f+x, -0.5f+y,  0.5f+z,  -1.0f,  0.0f,  0.0f,// bottom-right
                -0.5f+x ,  0.5f+y,  0.5f+z, -1.0f,  0.0f,  0.0f, // top-right

        };
        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);
        vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.LEFT));
        verticesList.addAll(vertsList);

    }
    private void generateRightFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                0.5f+x,  0.5f+y,  0.5f+z, 1.0f,  0.0f,  0.0f, // top-left
                0.5f+x, -0.5f+y, -0.5f+z, 1.0f,  0.0f,  0.0f,  // bottom-right
                0.5f+x,  0.5f+y, -0.5f+z, 1.0f,  0.0f,  0.0f, // top-right
                0.5f+x, -0.5f+y, -0.5f+z, 1.0f,  0.0f,  0.0f, // bottom-right
                0.5f+x,  0.5f+y,  0.5f+z, 1.0f,  0.0f,  0.0f, // top-left
                0.5f+x, -0.5f+y,  0.5f+z, 1.0f,  0.0f,  0.0f,// bottom-left

        };
        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);
        vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.RIGHT));
        verticesList.addAll(vertsList);

    }
    private void generateBackFace(int x,int y,int z){

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                -0.5f+x, -0.5f+y, -0.5f+z, 0.0f,  0.0f,  -1.0f,  // Bottom-left
                0.5f+x,  0.5f+y, -0.5f+z,  0.0f,  0.0f,  -1.0f, // top-right
                0.5f+x, -0.5f+y, -0.5f+z,  0.0f,  0.0f,  -1.0f,// bottom-right
                0.5f+x,  0.5f+y, -0.5f+z,  0.0f,  0.0f,  -1.0f,// top-right
                -0.5f+x, -0.5f+y, -0.5f+z, 0.0f,  0.0f,  -1.0f, // bottom-left
                -0.5f+x,  0.5f+y, -0.5f+z, 0.0f,  0.0f,  -1.0f, // top-left

        };

        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);

        vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.BACK));

        verticesList.addAll(vertsList);

    }
    private void generateBotFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        float [] verts ={
                -0.5f+x, -0.5f+y, -0.5f+z, 0.0f,  -1.0f,  0.0f,   // top-right
                0.5f+x, -0.5f+y, -0.5f+z,  0.0f,  -1.0f,  0.0f, // top-left
                0.5f+x, -0.5f+y,  0.5f+z,  0.0f,  -1.0f,  0.0f, // bottom-left
                0.5f+x, -0.5f+y,  0.5f+z,  0.0f,  -1.0f,  0.0f,// bottom-left
                -0.5f+x, -0.5f+y,  0.5f+z, 0.0f,  -1.0f,  0.0f, // bottom-right
                -0.5f+x, -0.5f+y, -0.5f+z, 0.0f,  -1.0f,  0.0f,  // top-right

        };
        List<Float> vertsList;
        BlockType blockType = chunkData.get(x-(int)(World.chunkSizeX*position.x)).get(z-(int)(World.chunkSizeZ*position.z)).get(y);
        vertsList = combineVertexData(verts, getTextureCoords(blockTextures.get(blockType), FaceType.BOTTOM));

        verticesList.addAll(vertsList);

    }

    ArrayList<Float> verticesList = new ArrayList<>();

   public enum BlockType{
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

    ArrayList<ArrayList<ArrayList<BlockType>>> chunkData = new ArrayList<>();

    public int VAO;
    public int VBO;
    public Vector3f position;
    FastNoiseLite noise;


    // Gather noise data

    public int getNoiseY(int x, int y, int z){

        float frequency = 0.65f;
        float result = (
                1 * (noise.GetNoise((x+(World.chunkSizeX*position.x))*frequency, (z+(World.chunkSizeZ*position.z))*frequency)+1))
                + 0.5f * ((noise.GetNoise((x+(World.chunkSizeX*position.x))*frequency*2, (z+(World.chunkSizeZ*position.z))*frequency*2)+1))
                + 0.25f * ((noise.GetNoise((x+(World.chunkSizeX*position.x))*frequency*4, (z+(World.chunkSizeZ*position.z))*frequency*4)+1));

        result = (float)Math.pow(result, 2.64f);

        return (int)Math.floor(result*2);
    }


    public Chunk (Vector3f position){

        blockTextures.put(BlockType.GRASS, new TextureCoords(0,0));
        blockTextures.put(BlockType.STONE, new TextureCoords(1,0));
        blockTextures.put(BlockType.DIRT, new TextureCoords(2,0));
        blockTextures.put(BlockType.SIDEDIRT, new TextureCoords(3,0));
        blockTextures.put(BlockType.PLANKS, new TextureCoords(4,0));
        blockTextures.put(BlockType.COBBLESTONE, new TextureCoords(0,1));
        blockTextures.put(BlockType.WATER, new TextureCoords(15,13));
        blockTextures.put(BlockType.SNOW, new TextureCoords(2,4));
        blockTextures.put(BlockType.SIDESNOW, new TextureCoords(4,4));
        blockTextures.put(BlockType.BEDROCK, new TextureCoords(1,1));

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);


        this.position = position;

        //System.out.println("Created chunk at: " + position);

        // create empty blocks of air
        for (int x = 0; x < World.chunkSizeX; x++){
            ArrayList<ArrayList<BlockType>> xList = new ArrayList<>();
            for (int z = 0; z < World.chunkSizeZ; z++){
                ArrayList<BlockType> zList = new ArrayList<>();
                for (int y = 0; y < World.worldSizeY; y++){
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

                    int noiseY = getNoiseY(x,y,z);

                    if(noiseY + y < 64){
                        zList.set(y+noiseY, BlockType.STONE);
                    } else if (y == World.chunkSizeY-1 && noiseY + y < 78){
                        zList.set(y+noiseY, BlockType.SIDEDIRT);
                    } else if(y == World.chunkSizeY-1){
                        zList.set(y+noiseY, BlockType.SIDESNOW);
                    } else {
                        zList.set(y+noiseY, BlockType.DIRT);

                    }

                }
            }
        }



        generateData();


        generateMesh();

    }


    private void generateMesh(){

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
    }

    private void generateData(){
        for (int x = 0; x < World.chunkSizeX; x++){
            for (int z = 0; z < World.chunkSizeZ; z++){
                for (int y = 0; y < World.chunkSizeY; y++){

                    int height = getNoiseY(x,y,z);
                    if(chunkData.get(x).get(z).get(y+height) != BlockType.AIR) {

                        if(z == 0 || (z < World.chunkSizeZ && chunkData.get(x).get(z-1).get(y+height) == BlockType.AIR))
                            generateBackFace(x+(int)(World.chunkSizeX*position.x),y+height,z+ (int)(World.chunkSizeZ*position.z));

                        if(z == World.chunkSizeZ - 1 || (z < World.chunkSizeZ - 1 && chunkData.get(x).get(z+1).get(y+height) == BlockType.AIR))
                            generateFrontFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));


                        if(x == 0 || (x < World.chunkSizeX && chunkData.get(x-1).get(z).get(y+height) == BlockType.AIR))
                            generateLeftFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        if(x == World.chunkSizeX - 1 || (x < World.chunkSizeX - 1 && chunkData.get(x+1).get(z).get(y+height) == BlockType.AIR))
                            generateRightFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        if(y+height == height  || (y+height < World.chunkSizeY+height && chunkData.get(x).get(z).get(y-1+height) == BlockType.AIR))
                            generateBotFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        // only render top if above is air
                        if(y+height == World.chunkSizeY - 1+height || (y+height < World.chunkSizeY - 1+height && chunkData.get(x).get(z).get(y+1+height) == BlockType.AIR))
                            generateTopFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                    }
                }
            }
        }

        // memory leak?
        verts = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            verts[i] = verticesList.get(i);
        }
    }
    public void update(){

        destroyMesh();
        generateData();
        generateMesh();
    }

    public void destroyMesh(){

        GL33.glDeleteVertexArrays(VAO);
        GL33.glDeleteBuffers(VBO);
        verticesList.clear();

    }




}
