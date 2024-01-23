package Game;

import Engine.FastNoiseLite;
import org.joml.Vector3i;

public class Terrain {

    static FastNoiseLite noise;

    static FastNoiseLite floatingIslandNoise;
    static FastNoiseLite moistNoise;


    static Blocks.BlockType[][][] initData(){

        Blocks.BlockType[][][] tempData = new Blocks.BlockType[World.chunkSizeX][World.worldSizeY][World.chunkSizeZ];

        // create empty blocks of air
        for (int x = 0; x < World.chunkSizeX; x++) {
            for (int y = 0; y < World.worldSizeY; y++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {
                    tempData[x][y][z] = Blocks.BlockType.AIR;
                }
            }
        }

        return tempData;
    }

    static{


        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

        moistNoise = new FastNoiseLite();
        moistNoise.SetSeed(234234);

        floatingIslandNoise = new FastNoiseLite();
    }



    static void shapeTerrain(Blocks.BlockType[][][] chunkData, Vector3i chunkPos){


        // modify blocks
        for (int x = 0; x < World.chunkSizeX; x++) {
            for (int y = 0; y < World.chunkSizeY; y++) {
                for (int z = 0; z < World.chunkSizeZ; z++) {

                    int noiseY = getNoiseY(x + (World.chunkSizeX * chunkPos.x), 0, z + (World.chunkSizeZ * chunkPos.z));
                    int ridgeNoiseY = getRidgeNoiseY(x + (World.chunkSizeX * chunkPos.x), 0, z + (World.chunkSizeZ * chunkPos.z));
                    int moistNoiseY = getMoistNoiseY(x + (World.chunkSizeX * chunkPos.x),0,z + (World.chunkSizeZ * chunkPos.z));
                    int noise3D = (int) getNoise3D(x + (World.chunkSizeX * chunkPos.x),y + (World.chunkSizeY * chunkPos.y),z + (World.chunkSizeZ * chunkPos.z)); // no clue why i have to cast this to int
                    int adjustedY = noiseY + y;


                    switch(getBiome(moistNoiseY)) {

                        case GRASSLANDS:

                            createGrassland(x,y,z, chunkData, chunkPos);

                            break;


                        case ROCKLANDS:

                            int noise = ridgeNoiseY + y;

                            if (noise < 64) {
                                chunkData[x][noise][z] = Blocks.BlockType.STONE;
                                chunkData[x][y][z] = Blocks.BlockType.LAVA;

                            } else if (y == World.chunkSizeY - 1 && adjustedY < 78) {
                                chunkData[x][noise][z] = Blocks.BlockType.HELLSTONE;
                            } else if (y == World.chunkSizeY - 1) {
                                chunkData[x][noise][z] = Blocks.BlockType.OBSIDIAN;
                            } else {
                                chunkData[x][noise][z] = Blocks.BlockType.OBSIDIAN;
                            }
                            // water
                            if(noise < 5 && ridgeNoiseY > 62) {
                                chunkData[x][noise][z] = Blocks.BlockType.OBSIDIAN;
                                chunkData[x][noise-1][z] = Blocks.BlockType.OBSIDIAN;
                                if(noise < 4){ // Fill with water
                                    for(int i = 0; i < 5-noise; i++){
                                        chunkData[x][noise+i][z] = Blocks.BlockType.LAVA;
                                    }

                                }
                            }

                            break;


                        case SNOWLANDS:
                            break;

                    }

                    // dumb but whatever
                    chunkData[x][0][z] = Blocks.BlockType.BEDROCK;
                    chunkData[x][1][z] = Blocks.BlockType.BEDROCK;
                    chunkData[x][2][z] = Blocks.BlockType.BEDROCK;

                }
            }
        }



    }

    private static void createGrassland(int x, int y, int z, Blocks.BlockType[][][] chunkData, Vector3i chunkPos) {

        int noiseY = getNoiseY(x + (World.chunkSizeX * chunkPos.x), 0, z + (World.chunkSizeZ * chunkPos.z));
        int adjustedIslandY = (noiseY + y/3) + 130;
        int adjustedY = (noiseY + y);
        int noise3D = (int) getNoise3D(x + (World.chunkSizeX * chunkPos.x),y + (World.chunkSizeY * chunkPos.y),z + (World.chunkSizeZ * chunkPos.z)); // no clue why i have to cast this to int
        int ridgeNoiseY = getRidgeNoiseY(x + (World.chunkSizeX * chunkPos.x), 0, z + (World.chunkSizeZ * chunkPos.z));
        int adjustedRidgeNoiseY = ridgeNoiseY + y;
        int adjustedRidgeIslandNoiseY = (-ridgeNoiseY + y/3) + 130;


        // Ground
        if(adjustedY < 78)
            chunkData[x][adjustedY][z] = Blocks.BlockType.DIRT;

        if (adjustedY < 64) {
            chunkData[x][adjustedY][z] = Blocks.BlockType.STONE;
            chunkData[x][y][z] = Blocks.BlockType.STONE;
        }

        // water
        if(noiseY < 5 && adjustedY > 62) {
            chunkData[x][adjustedY][z] = Blocks.BlockType.SAND;
            chunkData[x][adjustedY-1][z] = Blocks.BlockType.SAND;
            if(noiseY < 4){ // Fill with water
                for(int i = 0; i < 5-noiseY; i++){
                    chunkData[x][adjustedY+i][z] = Blocks.BlockType.WATER;
                }

            }
        }




        // Island
        if(noise3D < 20){
            chunkData[x][adjustedIslandY][z] = Blocks.BlockType.DIRT;
            if(adjustedIslandY < 150)
                chunkData[x][adjustedRidgeIslandNoiseY][z] = Blocks.BlockType.STONE;
        }
























        /*
        if (adjustedY < 64) {
            chunkData[x][adjustedY][z] = Game.Blocks.BlockType.STONE;
            chunkData[x][y][z] = Game.Blocks.BlockType.STONE;

        } else if (y == Game.World.chunkSizeY - 1 && adjustedY < 78) {
            chunkData[x][adjustedY][z] = Game.Blocks.BlockType.SIDEDIRT;
        } else if (y == Game.World.chunkSizeY - 1) {
            chunkData[x][adjustedY][z] = Game.Blocks.BlockType.SIDESNOW;
        } else {
            chunkData[x][adjustedY][z] = Game.Blocks.BlockType.DIRT;
        }

        // water
        if(noiseY < 5 && adjustedY > 62) {
            chunkData[x][adjustedY][z] = Game.Blocks.BlockType.SAND;
            chunkData[x][adjustedY-1][z] = Game.Blocks.BlockType.SAND;
            if(noiseY < 4){ // Fill with water
                for(int i = 0; i < 5-noiseY; i++){
                    chunkData[x][adjustedY+i][z] = Game.Blocks.BlockType.WATER;
                }

            }
        }

         */
    }


    private enum Biomes{
        GRASSLANDS,
        ROCKLANDS,
        SNOWLANDS,
    }


    private static Biomes getBiome(int noise){


        /*
        if(noise > 20)
            return Biomes.ROCKLANDS;

         */





        return Biomes.GRASSLANDS;
    }

    private static int getNoiseY(int x, int y, int z) {

        float frequency = 0.35f;
        float result = (
                1 * (noise.GetNoise(x * frequency, z * frequency) + 1))
                + 0.5f * ((noise.GetNoise(x * frequency * 2, z * frequency * 2) + 1))
                + 0.25f * ((noise.GetNoise(x * frequency * 4, z * frequency * 4) + 1));

        result = (float) Math.pow(result, 2.33f);

        return (int) Math.floor(result * 2);
    }
    private static int getMoistNoiseY(int x, int y, int z) {

        float frequency = 0.15f;
        float result = (
                1 * (moistNoise.GetNoise(x * frequency, z * frequency) + 1))
                + 0.5f * ((moistNoise.GetNoise(x * frequency * 2, z * frequency * 2) + 1))
                + 0.25f * ((moistNoise.GetNoise(x * frequency * 4, z * frequency * 4) + 1));

        result = (float) Math.pow(result, 3.24f);

        return (int) Math.floor(result * 2);
    }

    private static int getRidgeNoiseY(int x, int y, int z) {

        float frequency = 0.95f;
        float result = (
                1 * (ridgeNoise(x * frequency, z * frequency) + 1))
                + 0.5f * ((ridgeNoise(x * frequency * 2, z * frequency * 2) + 1))
                + 0.25f * ((ridgeNoise(x * frequency * 4, z * frequency * 4) + 1));

        result = (float) Math.pow(result, 2.24f);

        return (int) Math.floor(result * 2);
    }

    private static float ridgeNoise(float nx,float ny){

        return (float) (2 * (0.5 - Math.abs(0.5 - noise.GetNoise(nx, ny))));
    }

    private static float getNoise3D(int x, int y, int z){

        float frequency = 0.50f;
        float result = (
                1 * (floatingIslandNoise.GetNoise(x * frequency, y * frequency/2 , z * frequency) + 1))
                + 0.5f * ((floatingIslandNoise.GetNoise(x * frequency * 2, y * frequency, z * frequency * 2) + 1))
                + 0.25f * ((floatingIslandNoise.GetNoise(x * frequency * 4,y * frequency, z * frequency * 4) + 1));

        result = (float) Math.pow(result, 3.24f);

        return (int) Math.floor(result*2);
    }





























}
