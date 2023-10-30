import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Engine {


    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

    }

    double MS_PER_UPDATE = 1000.0 / 60.0;
    double previous = System.currentTimeMillis();
    double lag = 0.0;

    private void start() {


        Window window = new Window(1920,1080, "javaMinecraft");
        Camera camera = new Camera();
        Renderer renderer = new Renderer(window, camera);
        renderer.setupRender();
        ShaderCompiler shaderCompiler = new ShaderCompiler("src/resources/shaders/default_vertex.glsl", "src/resources/shaders/default_fragment.glsl");
        renderer.shader = shaderCompiler;
        Game game = new Game(camera);
        game.start();

        while ( !glfwWindowShouldClose(window.getWindowHandle()) ) {

            while(lag >= MS_PER_UPDATE){

                // update

                game.update();
                lag -= MS_PER_UPDATE;
            }

            renderer.render();

        }


    }

}
