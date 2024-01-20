import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import java.util.Map;
import java.util.concurrent.*;


public class World {

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ConcurrentLinkedQueue<Chunk> chunkDataQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Chunk> chunkMeshQueue = new ConcurrentLinkedQueue<>();

    // List to keep track of Future objects

    // This is where we will create our world
    // This has become so messy

    static int chunkSizeX = 16;
    static int chunkSizeZ = 16;
    static int chunkSizeY = 64;

    static int worldSizeY = 256;

    static int worldSizeX = 32;
    static int worldSizeZ = 32;

    static int fogDist = ((chunkSizeX*worldSizeX)/2)-20;
    private int  delay = 0; // chunk generation delay, bad name ik

    private Vector3i plyPos;

    static ArrayList<Chunk> chunks = new ArrayList<>();
    Map<Vector3i, Chunk> chunkPosMap = new HashMap<>();


    private final ArrayList<Chunk> nullNeighboursList = new ArrayList<>();

    long vertsCount = 0;

    Iterator<Chunk> iterator;
    // Let's make dumb and simple start

    World() {

        Renderer.renderObjects = chunks;
        Skybox sky = new Skybox();
        Renderer.skybox = sky;


    }



    public void updateWorld(){

        //System.out.println(vertsCount);

        removeChunks();
        plyPos = new Vector3i((int)Game.player.position.x, (int)Game.player.position.y, (int)Game.player.position.z);
        Vector3i chunkWorld = Utils.getChunkCoord(plyPos);


            ArrayList<Future<?>> futures = new ArrayList<>();
            for (int x = 0; x < worldSizeX; x++) {
                for (int z = 0; z < worldSizeZ; z++) {

                    Vector3i chunkPosition = new Vector3i((x + chunkWorld.x) - (worldSizeX / 2), 0, (z + chunkWorld.z) - (worldSizeZ / 2));
                        if (!chunkPosMap.containsKey(new Vector3i(chunkPosition.x, 0, chunkPosition.z)) && delay < 0) {
                            synchronized (this) {
                                Future<?> future = executor.submit(() -> {
                                    Chunk chunk = new Chunk(chunkPosition);
                                    chunkDataQueue.offer(chunk); // Add to queue
                                    chunkPosMap.put(chunkPosition, chunk);
                                });
                                delay = 300;
                                futures.add(future);
                            }
                        }
                        delay--;
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
        //System.out.println(chunks.size());
        }





    void removeChunks(){
        plyPos = new Vector3i((int)Game.player.position.x, (int)Game.player.position.y, (int)Game.player.position.z);
        Vector3i chunkWorld = Utils.getChunkCoord(plyPos);
        iterator = chunks.iterator();
        while(iterator.hasNext()){
            Chunk chunk = iterator.next();
            Vector2i chunkPosition = new Vector2i(chunk.position.x, chunk.position.z);
            if(Math.abs(chunkPosition.x - chunkWorld.x) > (worldSizeX/2) || (Math.abs(chunkPosition.y - chunkWorld.z)) > (worldSizeX/2)){
                nullNeighboursList.remove(chunk);
                chunkPosMap.remove(chunk.position);
                vertsCount -= chunk.mesh.transVertsAmount;
                vertsCount -= chunk.mesh.opaqueVertsAmount;
                chunk.destroyObject();
                iterator.remove();
            }
        }
    }

    private void setNeighbours(){

        // this is shit

        for (Chunk chunk : chunkDataQueue){

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

            if(isNull) {
                nullNeighboursList.add(chunk);
            } else {
                chunkMeshQueue.offer(chunk);
            }

        }

        // this is bad but needed at the moment.

        Iterator<Chunk> nullNeighboursIterator = nullNeighboursList.iterator();

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
                chunkDataQueue.offer(chunk);
                chunkMeshQueue.offer(chunk);
                nullNeighboursIterator.remove();
            }

        }
    }

    Chunk findChunk(Vector3i pos){

            return chunkPosMap.get(pos);
    }


    private void processGeneratedChunks() {
        ArrayList<Future<?>> futures = new ArrayList<>();

        while(!chunkDataQueue.isEmpty()) {
            Chunk chunk = chunkDataQueue.poll();
            if (chunk != null) {

                synchronized (this) {
                    Future<?> future = executor.submit(chunk::generateData);
                    futures.add(future);
                }


                chunks.add(chunk);


            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
                } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        createChunkMesh();
    }
    private void createChunkMesh(){
        while(!chunkMeshQueue.isEmpty()) {
            Chunk chunk = chunkMeshQueue.poll();
            chunk.mesh.generateMesh(); // OpenGL operations
            vertsCount += chunk.mesh.opaqueVertsAmount;
            vertsCount += chunk.mesh.transVertsAmount;
        }
    }







}
