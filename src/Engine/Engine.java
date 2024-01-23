package Engine;

import Engine.Renderer.Renderer;
import Engine.Window.Window;
import Game.Blocks;
import Game.Camera;
import Game.Time;
import Game.Game;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Engine {


    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

    }

    double MS_PER_UPDATE = 1000.0 / 165.0;
    double previous = System.currentTimeMillis();
    double lag = 0.0;

    private void start() {


        Window window = new Window(1920,1080, "javaMinecraft");
        Camera camera = new Camera();
        Renderer renderer = new Renderer(window, camera);
        renderer.setupRender();



        InputManager inputManager = new InputManager(window.getWindowHandle());


        Blocks.initBlocks();


        Game game = new Game(camera);
        game.start();

        while ( !glfwWindowShouldClose(window.getWindowHandle()) ) {
            double current = System.currentTimeMillis();
            double elapsed = current - previous;
            previous = current;
            lag += elapsed;
            Time.setDeltaTime(elapsed / 1000.0);
            game.update();

            /*
            while(lag >= MS_PER_UPDATE){

                // update
                //game.update();
                lag -= MS_PER_UPDATE;
            }

             */


            renderer.render();

        }


    }

}
