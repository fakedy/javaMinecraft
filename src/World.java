import org.joml.Vector3f;

public class World {

    // This is where we will create our world

    static int chunkSizeX = 8;
    static int chunkSizeZ = 8;
    static int chunkSizeY = 8;


    // Let's make dumb and simple start


    public void updateWorld(){

        Chunk test = new Chunk(new Vector3f(0,0,0));

        Renderer.renderObjects.add(test);


    }

}
