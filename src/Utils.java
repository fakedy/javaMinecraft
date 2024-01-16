import org.joml.Vector3f;
import org.joml.Vector3i;

public class Utils {






    public static Vector3i getChunkCoord(Vector3i pos) {
        int chunkCoordX = (int) Math.floor((double) pos.x / World.chunkSizeX);
        //int chunkCoordY = (int) Math.floor(pos.y / chunkSizeY);
        int chunkCoordY = 0;
        int chunkCoordZ = (int) Math.floor((double) pos.z / World.chunkSizeZ);

        return new Vector3i(chunkCoordX, chunkCoordY, chunkCoordZ);
    }



    public static Vector3i getBlockCoordWithinChunk(Vector3i pos) {
        int blockCoordX = pos.x % World.chunkSizeX;
        int blockCoordY = pos.y % World.worldSizeY;
        int blockCoordZ = pos.z % World.chunkSizeZ;

        if (blockCoordX < 0) {
            blockCoordX += World.chunkSizeX;
        }
        if (blockCoordY < 0) {
            blockCoordY += World.worldSizeY;
        }
        if (blockCoordZ < 0) {
            blockCoordZ += World.chunkSizeZ;
        }

        return new Vector3i (blockCoordX, blockCoordY, blockCoordZ);
    }




    public static Chunk findChunkByPosition(Vector3i position) {
        for (Chunk chunk : World.chunks) {
            if (chunk.position.equals(getChunkCoord(new Vector3i(position)))) {

                return chunk;
            }
        }
        return null;
    }


    static boolean removeBlock(Chunk chunk, Vector3i blockPos){

        Vector3i blockCoords = getBlockCoordWithinChunk(blockPos);

        int blockX = blockCoords.x;
        int blockY = blockCoords.y;
        int blockZ = blockCoords.z;
        Blocks.BlockType block  = chunk.chunkData[blockX][blockY][blockZ];
        System.out.println(block);

        if(block != Blocks.BlockType.AIR && block != Blocks.BlockType.BEDROCK && !Chunk.isLiquid(block) ){
            chunk.chunkData[blockX][blockY][blockZ] = Blocks.BlockType.AIR;
            // clunky, have to update each chunks neighbour then update itself again.
            chunk.update();
            chunk.leftChunk.update();
            chunk.rightChunk.update();
            chunk.frontChunk.update();
            chunk.backChunk.update();
            chunk.update();
            return true;
        } else{
            return false;
        }
    }

    static boolean putBlock(Chunk chunk, Vector3i blockPos, Blocks.BlockType handBlock) {

        Vector3i blockCoords = getBlockCoordWithinChunk(blockPos);

        int blockX = blockCoords.x;
        int blockY = blockCoords.y;
        int blockZ = blockCoords.z;
        Blocks.BlockType block = chunk.chunkData[blockX][blockY][blockZ];

        if (!Chunk.isLiquid(block)) {

            chunk.chunkData[blockX][blockY][blockZ] = handBlock;
            // clunky, have to update each chunks neighbour then update itself again.
            chunk.update();
            chunk.leftChunk.update();
            chunk.rightChunk.update();
            chunk.frontChunk.update();
            chunk.backChunk.update();
            chunk.update();
            return true;
        } else {
            return false;
        }


    }
    static boolean hitBlock(Chunk chunk, Vector3i blockPos){
        Vector3i blockCoords = getBlockCoordWithinChunk(blockPos);

        int blockX = blockCoords.x;
        int blockY = blockCoords.y;
        int blockZ = blockCoords.z;
        Blocks.BlockType block = chunk.chunkData[blockX][blockY][blockZ];

        return block != Blocks.BlockType.AIR && !Chunk.isLiquid(block);
    }

}
