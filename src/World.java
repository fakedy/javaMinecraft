import org.joml.Vector3f;

public class World {

    // This is where we will create our world

    static int chunkSizeX = 32;
    static int chunkSizeZ = 32;
    static int chunkSizeY = 64;

    static int worldSizeY = 256;

    static int worldSizeX = 4;
    static int worldSize = 4;


    // Let's make dumb and simple start

    World(){



        for (int x = 0; x < worldSizeX; x++){
            for (int z = 0; z < worldSize; z++){
                Renderer.renderObjects.add(new Chunk(new Vector3f(x,0,z)));
            }

        }


    }


    public void updateWorld(){




    }

}
