import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
public class InputManager {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];


    private float lastX = 1920 / 2.0f;
    private float lastY = 1080 / 2.0f;
    private boolean firstMouse = true;

    static float xoffset = 0.0f;
    static float yoffset = 0.0f;

    private boolean menu = false;


    public InputManager(long windowHandle){

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {


            if (key >= 0 && key < GLFW.GLFW_KEY_LAST) {
                if (action == GLFW_PRESS) {
                    keys[key] = true;
                } else if (action == GLFW_RELEASE) {
                    keys[key] = false;
                }
            }

            // will be moved and fixed later
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_PRESS ){

                menu = !menu;

                if (menu == true){
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                } else {
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                }

            }

        });

        glfwSetCursorPosCallback(windowHandle, (window, xposIn, yposIn) -> {

            if (menu != true) {

                float xpos = (float) xposIn;
                float ypos = (float) yposIn;

                if (firstMouse) {
                    lastX = xpos;
                    lastY = ypos;
                    firstMouse = false;
                }

                xoffset = xpos - 0;
                yoffset = 0 - ypos; // reversed since y-coordinates go from bottom to top

                lastX = xpos;
                lastY = ypos;
            }
        });




    }

    public static boolean keyDown(int key){

        return keys[key];
    }













}
