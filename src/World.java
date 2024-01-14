import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class World {

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ConcurrentLinkedQueue<Chunk> chunkQueue = new ConcurrentLinkedQueue<>();

    // List to keep track of Future objects



    // This is where we will create our world

    static int chunkSizeX = 22;
    static int chunkSizeZ = 22;
    static int chunkSizeY = 64;

    static int worldSizeY = 256;

    static int worldSizeX = 24;
    static int worldSizeZ = 24;

    static int fogDist = ((chunkSizeX*worldSizeX)/2)-10;
    private int  delay = 0; // chunk generation delay, bad name ik

    private Vector3f plyPos;

    static ArrayList<Chunk> chunks = new ArrayList<>();

    private ArrayList<Vector2i> chunkPosList = new ArrayList<>();


    // Let's make dumb and simple start

    World() {

        Renderer.renderObjects = chunks;
        Skybox sky = new Skybox();
        Renderer.skybox = sky;


    }

    public void updateWorld(){

        plyPos = Game.player.position;
        Vector3i chunkWorld = getChunkCoord(plyPos);

        Iterator<Chunk> iterator = chunks.iterator();
        while(iterator.hasNext()){
            Chunk chunk = iterator.next();
            Vector2i chunkPosition = new Vector2i(chunk.position.x, chunk.position.z);
            if(Math.abs(chunkPosition.x - chunkWorld.x) > worldSizeX-1 || (Math.abs(chunkPosition.y - chunkWorld.z)) > worldSizeX-1){
                chunkPosListemove(chunkPosition);
                //System.out.println("chunk was removed at: " + chunkPosition);
                chunk.destroyMesh();
                iterator.remove();
            }

        }


            for (int x = 0; x < worldSizeX; x++){
                for (int z = 0; z < worldSizeZ; z++){

                    Vector3i chunkPosition = new Vector3i((x + chunkWorld.x) - (worldSizeX/2), 0, (z + chunkWorld.z) - (worldSizeZ/2));


                    if (!chunkPosExists(new Vector2i(chunkPosition.x, chunkPosition.z)) && delay < 1) {
                        chunkPosList.add(new Vector2i(chunkPosition.x, chunkPosition.z));
                        delay = 10;
                        synchronized (this) {
                                executor.submit(() -> {
                                    Chunk chunk = new Chunk(chunkPosition);
                                    chunkQueue.offer(chunk); // Add to queue
                                });
                        }

                    }
                    delay--;
                }

            }

        processGeneratedChunks();

        }

        boolean chunkPosExists(Vector2i chunkPos){
            for(Vector2i pos : chunkPosList){
                if(chunkPos.equals(pos.x,pos.y))
                    return true;
            }
        return false;
    }

    void chunkPosListemove(Vector2i chunkPos){

        ArrayList<Vector2i> tempList = new ArrayList<>();
        for(Vector2i pos : chunkPosList){
            if(chunkPos.equals(pos.x,pos.y)){

            } else {
                tempList.add(pos);
            }

        }

        chunkPosList = tempList;

    }

    public void processGeneratedChunks() {
        while (!chunkQueue.isEmpty()) {
            Chunk chunk = chunkQueue.poll();
            if (chunk != null) {
                chunk.generateMesh(); // OpenGL operations
                chunks.add(chunk);
                //sortChunks();
            }

        }
    }

    private void sortChunks() {
        if (!chunks.isEmpty()) {
            System.out.println("sorting");

            // Sort chunks based on their distance from the player
            chunks.sort((chunk1, chunk2) -> {
                float dist1 = plyPos.distance(chunk1.position.x, chunk1.position.y, chunk1.position.z);
                float dist2 = plyPos.distance(chunk2.position.x, chunk2.position.y, chunk2.position.z);
                return Float.compare(dist2, dist1); // Sort in descending order
            });
        }
    }



    public static Vector3i getChunkCoord(Vector3f pos) {
        int chunkCoordX = (int) Math.floor(pos.x / chunkSizeX);
        //int chunkCoordY = (int) Math.floor(pos.y / chunkSizeY);
        int chunkCoordY = 0;
        int chunkCoordZ = (int) Math.floor(pos.z / chunkSizeZ);

        return new Vector3i(chunkCoordX, chunkCoordY, chunkCoordZ);
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


    static Chunk debug;

    public static boolean findChunkByPosition(Vector3f position) {
        for (Chunk chunk : chunks) {
            if (chunk.position.equals(getChunkCoord(new Vector3f(position)))) {
                Vector3i blockCoords = getBlockCoordWithinChunk(position);
                //System.out.println(getBlockCoordWithinChunk(position));
                int blockX = blockCoords.x;
                int blockY = blockCoords.y;
                int blockZ = blockCoords.z;
                Chunk.BlockType block  = chunk.chunkData[blockX][blockY][blockZ];
                    //System.out.println("Checking block at: " + chunk.chunkData.get(blockX).get(blockZ).get(blockY));

                if(block != Chunk.BlockType.AIR && block != Chunk.BlockType.BEDROCK && !Chunk.isLiquid(block) ){
                    //System.out.println("removing block at: " + getBlockCoordWithinChunk(position));

                    if(debug != null){
                        if(!debug.equals(chunk)){
                            System.out.println("Not same chunk");
                            debug = chunk;
                        }
                    } else {
                        debug = chunk;
                    }
                    chunk.chunkData[blockX][blockY][blockZ] = Chunk.BlockType.AIR;
                    chunk.update();
                    return true;
                }
            }
        }
        return false;  // or throw an exception or handle this case as you see fit
    }

}
