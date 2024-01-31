package Game;

import org.joml.Vector3i;


public class Chunk {


    Chunk rightChunk;
    Chunk leftChunk;
    Chunk frontChunk;
    Chunk backChunk;

    Blocks.BlockType[][][] chunkData;
    public ChunkMesh mesh;

    private Vector3i position;

    public Chunk(Vector3i position) {


        this.position = position;

        //System.out.println("Created chunk at: " + position);


        chunkData = Terrain.initData();

        Terrain.shapeTerrain(chunkData, position);
        mesh = new ChunkMesh(this);

        //generateData();
    }

    public Vector3i getPosition() {
        return position;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }


    void generateData() {

        mesh.generateData();
    }

    public static boolean isLiquid (Blocks.BlockType block){

        return block == Blocks.BlockType.WATER || block == Blocks.BlockType.LAVA;
    }

    public void update() {
        mesh.destroyMesh();
        generateData();
        mesh.generateMesh();
    }


    public void destroyObject(){
        mesh.destroyMesh();
        backChunk = null;
        frontChunk = null;
        rightChunk = null;
        leftChunk = null;
    }

}
