import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Engine {


    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

    }

    private void start() {

        double MS_PER_UPDATE = 1000.0 / 60.0;
        double previous = System.currentTimeMillis();
        double lag = 0.0;

        Window window = new Window(1920,1080, "javaMinecraft");
        Renderer renderer = new Renderer(window);
        renderer.setupRender();
        Game game = new Game();
        ShaderCompiler shaderCompiler = new ShaderCompiler("src/resources/shaders/default_vertex.glsl", "src/resources/shaders/default_vertex.glsl");

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
