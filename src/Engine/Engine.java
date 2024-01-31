package Engine;

import Engine.Renderer.Renderer;
import Engine.Window.Window;
import Game.Blocks;
import Game.Game;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Engine {


    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();
    }

    private final double MS_PER_UPDATE = 1000.0 / 165.0;
    private double previous = System.currentTimeMillis();
    private double lag = 0.0;

    private void start() {


        Window window = new Window(1920,1080, "javaMinecraft");
        Renderer renderer = new Renderer(window);
        renderer.setupRender();

        Game game = new Game();
        game.start();



        InputManager inputManager = new InputManager(window.getWindowHandle());


        Blocks.initBlocks();


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
