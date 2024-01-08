import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;


public class World {

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ConcurrentLinkedQueue<Chunk> chunkQueue = new ConcurrentLinkedQueue<>();

    // List to keep track of Future objects



    // This is where we will create our world

    static int chunkSizeX = 16;
    static int chunkSizeZ = 16;
    static int chunkSizeY = 64;

    static int worldSizeY = 128;

    static int worldSizeX = 8;
    static int worldSizeZ = 8;

    private Vector3f plyPos;

    static ArrayList<Chunk> chunks = new ArrayList<>();


    // Let's make dumb and simple start

    World(){



        for (int x = 0; x < worldSizeX; x++){
            for (int z = 0; z < worldSizeZ; z++){

                int finalX = x;
                int finalZ = z;
                executor.submit(() -> {
                    try {
                        Chunk chunk = new Chunk(new Vector3f(finalX - (worldSizeX) / 2, 0, finalZ - (worldSizeZ) / 2));
                        chunkQueue.offer(chunk); // Add to queue
                    } catch (Exception e){
                        System.err.println("Error occurred while processing chunk: " + e.getMessage());
                        e.printStackTrace();
                    }

                });
            }
        }

        processGeneratedChunks();


        Renderer.renderObjects = chunks;
        Skybox sky = new Skybox();
        Renderer.skybox = sky;
    }


    public void updateWorld(){

        plyPos = Game.player.position;
        Vector3f chunkWorld = getChunkCoord(plyPos);

        Iterator<Chunk> iterator = chunks.iterator();


        while(iterator.hasNext()){
            Chunk chunk = iterator.next();
            Vector2f chunkV2 = new Vector2f(chunk.position.x, chunk.position.z);
            if(chunkV2.distance(chunkWorld.x, chunkWorld.z) > worldSizeX/1.5){
                chunk.destroyMesh();
                iterator.remove();
            }

        }

            for (int x = -worldSizeX/2; x < worldSizeX/2; x++){
                for (int z = -worldSizeZ/2; z < worldSizeZ/2; z++){

                    Vector3f chunkPosition = new Vector3f((chunkWorld.x)-x, 0, (chunkWorld.z)-z);
                    if (!isChunkAtPosition(chunkPosition) && new Vector2f(chunkPosition.x, chunkPosition.z).distance(chunkWorld.x, chunkWorld.z) <= worldSizeX / 1.5) {

                        executor.submit(() -> {
                            Chunk chunk = new Chunk(chunkPosition);
                            chunkQueue.offer(chunk); // Add to queue

                        });

                    }
                }

            }



        processGeneratedChunks();

        }



    public void processGeneratedChunks() {
        while (!chunkQueue.isEmpty()) {
            Chunk chunk = chunkQueue.poll();
            if (chunk != null) {
                chunk.generateMesh(); // OpenGL operations

                chunks.add(chunk);

            }
        }
    }



    private boolean isChunkAtPosition(Vector3f position) {
        for (Chunk chunk : chunks) {
            if (chunk.position.equals(position)) {
                return true;
            }
        }
        return false;
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

        if (blockCoordX < 0) {
            blockCoordX += chunkSizeX;
        }
        if (blockCoordY < 0) {
            blockCoordY += worldSizeY;
        }
        if (blockCoordZ < 0) {
            blockCoordZ += chunkSizeZ;
        }

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
                    //System.out.println("removing block at: " + getBlockCoordWithinChunk(position));
                    chunk.chunkData.get(blockX).get(blockZ).set(blockY, Chunk.BlockType.AIR);
                    chunk.update();
                    return true;
                }



            }
        }
        return false;  // or throw an exception or handle this case as you see fit
    }

}
