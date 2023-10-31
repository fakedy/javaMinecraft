

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

        player.update();
        camera.Follow(player);
        camera.update();


    }

}
