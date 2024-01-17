import java.util.HashMap;
import java.util.Map;

public class Blocks {


        static Map<BlockType, TextureCoords> blockTextures = new HashMap<>();

        static {
            blockTextures.put(BlockType.GRASS, new TextureCoords(0, 0));
            blockTextures.put(BlockType.STONE, new TextureCoords(1, 0));
            blockTextures.put(BlockType.DIRT, new TextureCoords(2, 0));
            blockTextures.put(BlockType.SIDEDIRT, new TextureCoords(3, 0));
            blockTextures.put(BlockType.PLANKS, new TextureCoords(4, 0));
            blockTextures.put(BlockType.COBBLESTONE, new TextureCoords(0, 1));
            blockTextures.put(BlockType.WATER, new TextureCoords(14, 12));
            blockTextures.put(BlockType.SNOW, new TextureCoords(2, 4));
            blockTextures.put(BlockType.SIDESNOW, new TextureCoords(4, 4));
            blockTextures.put(BlockType.BEDROCK, new TextureCoords(1, 1));
            blockTextures.put(BlockType.SAND, new TextureCoords(2, 1));
            blockTextures.put(BlockType.LAVA, new TextureCoords(15, 15));
            blockTextures.put(BlockType.OBSIDIAN, new TextureCoords(5, 2));
            blockTextures.put(BlockType.HELLSTONE, new TextureCoords(7, 6));
            blockTextures.put(BlockType.RED_FLOWER, new TextureCoords(12, 0));
            blockTextures.put(BlockType.YELLOW_FLOWER, new TextureCoords(13, 0));
        }


    public enum BlockType {
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
        SAND,
        LAVA,
        OBSIDIAN,
        HELLSTONE,
        RED_FLOWER,
        YELLOW_FLOWER
    }

    static class TextureCoords {
        public int x;
        public int y;

        public int tileAcross = 16;

        public TextureCoords(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }



    public static float[] getTextureCoords(TextureCoords cords, ChunkMesh.FaceType faceType) {
        float tileWidth = 1.0f / cords.tileAcross; // prob 16
        float startX = cords.x * tileWidth;
        float startY = cords.y * tileWidth;
        float endX = startX + tileWidth;
        float endY = startY + tileWidth;

        switch (faceType) {
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
                return new float[]{
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case FRONT:
                // Assuming FRONT needs to be flipped horizontally
                return new float[]{
                        startX, endY,
                        endX, endY,
                        endX, startY,
                        endX, startY,
                        startX, startY,
                        startX, endY
                };
            case BACK:
                // Assuming BACK face orientation is same as FRONT
                return new float[]{
                        endX, endY,
                        startX, startY,
                        startX, endY,
                        startX, startY,
                        endX, endY,
                        endX, startY
                };
            case BOTTOM:
                // Assuming BOTTOM face orientation is same as TOP
                return new float[]{
                        startX, startY,
                        endX, startY,
                        endX, endY,
                        endX, endY,
                        startX, endY,
                        startX, startY
                };
            case RIGHT:
                // Assuming RIGHT face orientation is same as LEFT
                return new float[]{
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
}
