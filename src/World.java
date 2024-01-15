import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import java.util.Map;
import java.util.concurrent.*;


public class World {

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ConcurrentLinkedQueue<Chunk> chunkQueue = new ConcurrentLinkedQueue<>();

    // List to keep track of Future objects



    // This is where we will create our world
    // This has become so messy

    static int chunkSizeX = 16;
    static int chunkSizeZ = 16;
    static int chunkSizeY = 64;

    static int worldSizeY = 256;

    static int worldSizeX = 16;
    static int worldSizeZ = 16;

    static int fogDist = ((chunkSizeX*worldSizeX)/2)-20;
    private int  delay = 0; // chunk generation delay, bad name ik

    private Vector3f plyPos;

    static ArrayList<Chunk> chunks = new ArrayList<>();
    Map<Vector3i, Chunk> chunkPosMap = new HashMap<>();

    private ArrayList<Vector2i> chunkPosList = new ArrayList<>();

    private Iterator<Chunk> nullNeighboursIterator;
    private ArrayList<Chunk> nullNeighboursList = new ArrayList<>();

    Iterator<Chunk> iterator;
    // Let's make dumb and simple start

    World() {

        Renderer.renderObjects = chunks;
        Skybox sky = new Skybox();
        Renderer.skybox = sky;


    }

    public void updateWorld(){

        plyPos = Game.player.position;
        Vector3i chunkWorld = getChunkCoord(plyPos);

        removeChunks();


            ArrayList<Future<?>> futures = new ArrayList<>();
            for (int x = 0; x < worldSizeX; x++) {
                for (int z = 0; z < worldSizeZ; z++) {

                    Vector3i chunkPosition = new Vector3i((x + chunkWorld.x) - (worldSizeX / 2), 0, (z + chunkWorld.z) - (worldSizeZ / 2));

                    if (!chunkPosExists(new Vector2i(chunkPosition.x, chunkPosition.z))) {
                        chunkPosList.add(new Vector2i(chunkPosition.x, chunkPosition.z));

                        synchronized (this) {
                            Future<?> future = executor.submit(() -> {
                                Chunk chunk = new Chunk(chunkPosition);
                                chunkQueue.offer(chunk); // Add to queue
                                chunkPosMap.put(chunkPosition, chunk);
                            });
                            futures.add(future);
                        }
                    }
                }
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }


        setNeighbours();
        processGeneratedChunks();
        System.out.println(chunks.size());
        }




        boolean chunkPosExists(Vector2i chunkPos){
            for(Vector2i pos : chunkPosList){
                if(chunkPos.equals(pos.x,pos.y))
                    return true;
            }
        return false;
    }


    void removeChunks(){
        plyPos = Game.player.position;
        Vector3i chunkWorld = getChunkCoord(plyPos);
        iterator = chunks.iterator();
        while(iterator.hasNext()){
            Chunk chunk = iterator.next();
            Vector2i chunkPosition = new Vector2i(chunk.position.x, chunk.position.z);
            if(Math.abs(chunkPosition.x - chunkWorld.x) > (worldSizeX/2) || (Math.abs(chunkPosition.y - chunkWorld.z)) > (worldSizeX/2)){
                chunkPosMap.remove(chunk.position);
                chunkPosListemove(chunkPosition);
                chunk.destroyMesh();
                iterator.remove();
            }
        }
    }

    void setNeighbours(){

        // this is shit

        for (Chunk chunk : chunkQueue){

            Vector3i origin = chunk.position;

            boolean isNull = false;

            Vector3i leftChunkPos = new Vector3i(origin.x - 1, origin.y, origin.z);
            Vector3i rightChunkPos = new Vector3i(origin.x + 1, origin.y, origin.z);
            Vector3i backChunkPos = new Vector3i(origin.x, origin.y, origin.z - 1);
            Vector3i frontChunkPos = new Vector3i(origin.x, origin.y, origin.z + 1);

            if(findChunk(leftChunkPos) != null){
                chunk.leftChunk = findChunk(leftChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(rightChunkPos) != null){
                chunk.rightChunk = findChunk(rightChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(backChunkPos) != null){
                chunk.backChunk = findChunk(backChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(frontChunkPos) != null){
                chunk.frontChunk = findChunk(frontChunkPos);
            } else {
                isNull = true;
            }

            if(isNull)
                nullNeighboursList.add(chunk);
        }

        // this is bad but needed at the moment.

        nullNeighboursIterator = nullNeighboursList.iterator();

        while (nullNeighboursIterator.hasNext()){

            Chunk chunk = nullNeighboursIterator.next();

            Vector3i origin = chunk.position;

            boolean isNull = false;

            Vector3i leftChunkPos = new Vector3i(origin.x - 1, origin.y, origin.z);
            Vector3i rightChunkPos = new Vector3i(origin.x + 1, origin.y, origin.z);
            Vector3i backChunkPos = new Vector3i(origin.x, origin.y, origin.z - 1);
            Vector3i frontChunkPos = new Vector3i(origin.x, origin.y, origin.z + 1);

            if(findChunk(leftChunkPos) != null){
                chunk.leftChunk = findChunk(leftChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(rightChunkPos) != null){
                chunk.rightChunk = findChunk(rightChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(backChunkPos) != null){
                chunk.backChunk = findChunk(backChunkPos);
            } else {
                isNull = true;
            }
            if(findChunk(frontChunkPos) != null){
                chunk.frontChunk = findChunk(frontChunkPos);
            } else {
                isNull = true;
            }

            if(!isNull) {
                //chunk.update(); // causes crash ;D
                nullNeighboursIterator.remove();
            }

        }
    }

    Chunk findChunk(Vector3i pos){

            return chunkPosMap.get(pos);
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
        while(!chunkQueue.isEmpty()) {
            Chunk chunk = chunkQueue.poll();
            if (chunk != null) {
                chunk.generateData();
                chunk.generateMesh(); // OpenGL operations
                chunks.add(chunk);
                //sortChunks();
            }

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
                /*
                System.out.println(chunk.leftChunk + " left chunk");
                System.out.println(chunk.rightChunk + " right chunk");
                System.out.println(chunk.frontChunk + " front chunk");
                System.out.println(chunk.backChunk + " back chunk");

                 */
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


                    // clunky, have to update each chunks neighbour then update itself again.
                    chunk.update();
                    chunk.leftChunk.update();
                    chunk.rightChunk.update();
                    chunk.frontChunk.update();
                    chunk.backChunk.update();
                    chunk.update();


                    return true;
                }
            }
        }
        return false;  // or throw an exception or handle this case as you see fit
    }


}
