import org.joml.Vector3f;
import org.joml.Vector3i;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class World {

    // This is where we will create our world

    static int chunkSizeX = 16;
    static int chunkSizeZ = 16;
    static int chunkSizeY = 64;

    static int worldSizeY = 256;

    static int worldSizeX = 16;
    static int worldSize = 16;

    static ArrayList<Chunk> chunks = new ArrayList<>();


    // Let's make dumb and simple start

    World(){

        for (int x = 0; x < worldSizeX; x++){
            for (int z = 0; z < worldSize; z++){

                chunks.add(new Chunk(new Vector3f(x,0,z)));

            }

        }
        Renderer.renderObjects = chunks;
        Skybox sky = new Skybox();
        Renderer.skybox = sky;
    }


    public void updateWorld(){




    }

    public static Vector3f getChunkCoord(Vector3f pos) {
        int chunkCoordX = (int) Math.floor(pos.x / chunkSizeX);
        //int chunkCoordY = (int) Math.floor(pos.y / chunkSizeY);
        int chunkCoordY = 0;
        int chunkCoordZ = (int) Math.floor(pos.z / chunkSizeZ);


        return new Vector3f(chunkCoordX, chunkCoordY, chunkCoordZ);
    }

    public static Vector3i getBlockCoordWithinChunk(Vector3f pos) {
        int blockCoordX = (int) pos.x % chunkSizeX;
        int blockCoordY = (int) pos.y % worldSizeY;
        int blockCoordZ = (int) pos.z % chunkSizeZ;

        return new Vector3i(blockCoordX, blockCoordY, blockCoordZ);
    }

    public static boolean findChunkByPosition(Vector3f position) {
        for (Chunk chunk : chunks) {
            if (chunk.position.equals(getChunkCoord(new Vector3f(position)))) {
                Vector3i blockCoords = getBlockCoordWithinChunk(position);
                //System.out.println(getBlockCoordWithinChunk(position));
                int blockX = blockCoords.x;
                int blockY = blockCoords.y;
                int blockZ = blockCoords.z;
                    //System.out.println("Checking block at: " + chunk.chunkData.get(blockX).get(blockZ).get(blockY));
                if(chunk.chunkData.get(blockX).get(blockZ).get(blockY) != Chunk.BlockType.AIR){
                    System.out.println("removing block at: " + getBlockCoordWithinChunk(position));
                    chunk.chunkData.get(blockX).get(blockZ).set(blockY, Chunk.BlockType.AIR);
                    chunk.update();
                    return true;
                }

            }
        }
        return false;  // or throw an exception or handle this case as you see fit
    }

}
