import org.joml.Vector3f;

public class Game {

    public static Camera camera;
    private Player player;

    private World world;

    public Game(Camera camera){
        this.camera = camera;
    }


    public void start(){

    player = new Player();
    world = new World();


    }

    public void update(){

        player.update();
        camera.Follow(player);
        camera.update();
        world.updateWorld();

    }

}
