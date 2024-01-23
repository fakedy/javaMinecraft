package Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import Engine.TextureLoader;

public class Blocks {


        static Map<BlockType, Integer> blockTextures = new HashMap<>();


        static {



            // I dont like this but whatever

        }

    public static void initBlocks() {

        // this must be moved into Game.Blocks as some kind of init world function or update textures
        String[] blockNames = TextureLoader.readDirectoryFiles("src/resources/textures/block");
        Blocks.BlockType block = null;
        for(int i = 0; i < blockNames.length; i++){
            for(Blocks.BlockType blockEnum : Blocks.BlockType.values()){
                if(Objects.equals(blockNames[i], blockEnum.toString())){
                    block = blockEnum;
                    break;
                }
            }
            if(block != null){
                Blocks.blockTextures.put(block, i);
            }
            block = null;
        }
    }


    public enum BlockType {
        AIR,
        GRASS,
        DIRT,
        STONE,
        SIDEGRASS,
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



    public static float[] getTextureCoordsFromAtlas(TextureCoords cords, ChunkMesh.FaceType faceType) {
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

    public static float[] getTextureCoords( BlockType blocktype , ChunkMesh.FaceType faceType, int lengthX, int lengthY, int lengthZ) {

        // should move this entire step into chunkMesh creation. right now i stitch it when it could be together from start.


            int textureIndex = 0;
            if(blockTextures.get(blocktype) != null){
                if(blocktype == BlockType.DIRT && faceType == ChunkMesh.FaceType.TOP){
                    textureIndex = blockTextures.get(BlockType.GRASS);
                } else {
                    textureIndex = blockTextures.get(blocktype);
                }

            }

        switch (faceType) {
            case TOP:
                return new float[]{
                        0.0f,           1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                        0.0f,           1.0f + lengthZ, textureIndex,
                        0.0f,           0.0f,           textureIndex,
                };
            case LEFT:
                return new float[]{
                        0.0f,           0.0f,           textureIndex,
                        1.0f + lengthZ, 0.0f,           textureIndex,
                        1.0f + lengthZ, 1.0f + lengthY, textureIndex,
                        1.0f + lengthZ, 1.0f + lengthY, textureIndex,
                        0.0f,           1.0f + lengthY, textureIndex,
                        0.0f,           0.0f,           textureIndex,
                };
            case FRONT:
                return new float[]{
                        0.0f,           1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                        0.0f,           0.0f,           textureIndex,
                        0.0f,           1.0f + lengthZ, textureIndex,
                };
            case BACK:
                return new float[]{
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        0.0f,           0.0f,           textureIndex,
                        0.0f,           1.0f,           textureIndex,
                        0.0f,           0.0f,           textureIndex,
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                };
            case BOTTOM:
                return new float[]{
                        0.0f,           0.0f,           textureIndex,
                        1.0f + lengthX, 0.0f,           textureIndex,
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        1.0f + lengthX, 1.0f + lengthZ, textureIndex,
                        0.0f,           1.0f + lengthZ, textureIndex,
                        0.0f,           0.0f,           textureIndex,
                };
            case RIGHT:
                return new float[]{
                        0.0f,           0.0f,           textureIndex,
                        1.0f + lengthZ, 1.0f + lengthY, textureIndex,
                        1.0f + lengthZ, 0.0f,           textureIndex,
                        1.0f + lengthZ, 1.0f + lengthY, textureIndex,
                        0.0f,           0.0f,           textureIndex,
                        0.0f,           1.0f + lengthY, textureIndex,
                };
            default:
                return null; // or some default value
        }
    }
}
