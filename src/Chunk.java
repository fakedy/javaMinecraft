import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;



public class Chunk {

    public float[] verts;

    private void generateTopFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                -0.5f+x,  y+0.5f, -0.5f+z,  0.0f, 1.0f, // top-left
                0.5f+x,  y+0.5f,  0.5f+z,  1.0f, 0.0f, // bottom-right
                0.5f+x,  y+0.5f, -0.5f+z,  1.0f, 1.0f, // top-right
                0.5f+x,  y+0.5f,  0.5f+z,  1.0f, 0.0f, // bottom-right
                -0.5f+x,  y+0.5f, -0.5f+z,  0.0f, 1.0f, // top-left
                -0.5f+x,  y+0.5f,  0.5f+z,  0.0f, 0.0f  // bottom-left

        };
        verticesList.addAll(List.of(verts));

    }
    private void generateFrontFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                // Front face
                -0.5f+x, -0.5f+y,  0.5f+z,  0.0f, 0.0f, // bottom-left
                0.5f+x, -0.5f+y,  0.5f+z,  1.0f, 0.0f, // bottom-right
                0.5f+x,  0.5f+y,  0.5f+z,  1.0f, 1.0f, // top-right
                0.5f+x,  0.5f+y,  0.5f+z,  1.0f, 1.0f, // top-right
                -0.5f+x,  0.5f+y,  0.5f+z,  0.0f, 1.0f, // top-left
                -0.5f+x, -0.5f+y,  0.5f+z,  0.0f, 0.0f, // bottom-left

        };
        verticesList.addAll(List.of(verts));

    }
    private void generateLeftFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                // Left face
                -0.5f+x,  0.5f+y,  0.5f+z,  1.0f, 1.0f, // top-right
                -0.5f+x ,  0.5f+y, -0.5f+z,  0.0f, 1.0f, // top-left
                -0.5f+x , -0.5f+y, -0.5f+z,  0.0f, 0.0f, // bottom-left
                -0.5f+x , -0.5f+y, -0.5f+z  ,  0.0f, 0.0f, // bottom-left
                -0.5f+x, -0.5f+y,  0.5f+z,  1.0f, 0.0f, // bottom-right
                -0.5f+x ,  0.5f+y,  0.5f+z,  1.0f, 1.0f, // top-right

        };
        verticesList.addAll(List.of(verts));

    }
    private void generateRightFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                0.5f+x,  0.5f+y,  0.5f+z,  0.0f, 1.0f, // top-left
                0.5f+x, -0.5f+y, -0.5f+z,  1.0f, 0.0f, // bottom-right
                0.5f+x,  0.5f+y, -0.5f+z,  1.0f, 1.0f, // top-right
                0.5f+x, -0.5f+y, -0.5f+z,  1.0f, 0.0f, // bottom-right
                0.5f+x,  0.5f+y,  0.5f+z,  0.0f, 1.0f, // top-left
                0.5f+x, -0.5f+y,  0.5f+z,  0.0f, 0.0f, // bottom-left

        };
        verticesList.addAll(List.of(verts));

    }
    private void generateBackFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                -0.5f+x, -0.5f+y, -0.5f+z,  0.0f, 0.0f, // Bottom-left
                0.5f+x,  0.5f+y, -0.5f+z,  1.0f, 1.0f, // top-right
                0.5f+x, -0.5f+y, -0.5f+z,  1.0f, 0.0f, // bottom-right
                0.5f+x,  0.5f+y, -0.5f+z,  1.0f, 1.0f, // top-right
                -0.5f+x, -0.5f+y, -0.5f+z,  0.0f, 0.0f, // bottom-left
                -0.5f+x,  0.5f+y, -0.5f+z,  0.0f, 1.0f, // top-left

        };
        verticesList.addAll(List.of(verts));

    }
    private void generateBotFace(int x,int y,int z){
        ArrayList<Float> vertices = new ArrayList<>();

        float topY = y + 1.0f;  // top face Y-coordinate

        // Define the vertices for the top face of the block
        Float [] verts ={
                -0.5f+x, -0.5f+y, -0.5f+z,  0.0f, 1.0f, // top-right
                0.5f+x, -0.5f+y, -0.5f+z,  1.0f, 1.0f, // top-left
                0.5f+x, -0.5f+y,  0.5f+z   ,  1.0f, 0.0f, // bottom-left
                0.5f+x, -0.5f+y,  0.5f+z,  1.0f, 0.0f, // bottom-left
                -0.5f+x, -0.5f+y,  0.5f+z,  0.0f, 0.0f, // bottom-right
                -0.5f+x, -0.5f+y, -0.5f+z,  0.0f, 1.0f, // top-right

        };
        verticesList.addAll(List.of(verts));

    }

    ArrayList<Float> verticesList = new ArrayList<>();

   private enum BlockType{
       AIR,
       GRASS,
       DIRT,
       STONE,
   }
    ArrayList<ArrayList<ArrayList<BlockType>>> chunkData = new ArrayList<>();

    public int VAO;
    public Vector3f position;

    // Create and configure FastNoise object


    // Gather noise data


    public Chunk (Vector3f position){

        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);


        this.position = position;

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
                    zList.set(y+(int)Math.floor((noise.GetNoise(x+(World.chunkSizeX*position.x), z+(World.chunkSizeZ*position.z))+1)*10), BlockType.GRASS);
                }
            }
        }



        for (int x = 0; x < World.chunkSizeX; x++){
            for (int z = 0; z < World.chunkSizeZ; z++){
                for (int y = 0; y < World.chunkSizeY; y++){

                    int height = (int)Math.floor((noise.GetNoise(x+(World.chunkSizeX*position.x), z+(World.chunkSizeZ*position.z))+1)*10);
                    if(chunkData.get(x).get(z).get(y+height) != BlockType.AIR) {




                        if(z == 0 || (z < World.chunkSizeZ - 1 && chunkData.get(x).get(z-1).get(y+height) == BlockType.AIR))
                            generateBackFace(x+(int)(World.chunkSizeX*position.x),y+height,z+ (int)(World.chunkSizeZ*position.z));

                        if(z == World.chunkSizeZ - 1 || (z < World.chunkSizeZ - 1 && chunkData.get(x).get(z+1).get(y+height) == BlockType.AIR))
                            generateFrontFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));


                        if(x == 0 || (x < World.chunkSizeX - 1 && chunkData.get(x-1).get(z).get(y+height) == BlockType.AIR))
                            generateLeftFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        if(x == World.chunkSizeX - 1 || (x < World.chunkSizeX - 1 && chunkData.get(x+1).get(z).get(y+height) == BlockType.AIR))
                            generateRightFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        if(y+height == height || (y+height < World.chunkSizeY - 1+height && chunkData.get(x).get(z).get(y-1+height) == BlockType.AIR))
                            generateBotFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                        // only render top if above is air
                        if(y+height == World.chunkSizeY - 1+height || (y+height < World.chunkSizeY - 1+height && chunkData.get(x).get(z).get(y+1+height) == BlockType.AIR))
                            generateTopFace(x+(int)(World.chunkSizeX*position.x),y+height,z + (int)(World.chunkSizeZ*position.z));

                    }
                }
            }
        }

        verts = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            verts[i] = verticesList.get(i);
        }

        generateMesh();

    }


    private void generateMesh(){

        VAO = GL33.glGenVertexArrays();
        int VBO = GL33.glGenBuffers();
        // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

        // position attribute
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 5 * Float.BYTES, 0);

        glEnableVertexAttribArray(0);
        // color attribute
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }




}
