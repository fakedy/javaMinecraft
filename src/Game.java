

public class Game {

    private Camera camera;
    private Player player;

    public Game(Camera camera){
        this.camera = camera;
    }


    public void start(){

    player = new Player();

    }

    public void update(){

        camera.Follow(player);
        player.update();


    }

}
