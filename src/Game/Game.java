package Game;

public class Game {

    public static Player player;

    private World world;

    public Game(){
    }


    public void start(){

    player = new Player();
    world = new World();


    }

    public void update(){

        player.update();
        world.updateWorld();

    }

}
