package Engine;

import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
public class InputManager {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] processedKeys = new boolean[GLFW.GLFW_KEY_LAST];

    private static boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] processedMouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private float lastX = 1920 / 2.0f;
    private float lastY = 1080 / 2.0f;
    private boolean firstMouse = true;

    public static float xoffset = 0.0f;
    public static float yoffset = 0.0f;

    private boolean menu = false;


    public InputManager(long windowHandle){

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {


            if (key >= 0 && key < GLFW.GLFW_KEY_LAST) {
                if (action == GLFW_PRESS) {
                    keys[key] = true;

                } else if (action == GLFW_RELEASE) {
                    keys[key] = false;
                    processedKeys[key] = false;
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

        glfwSetMouseButtonCallback(windowHandle,(window, button, action, mods) -> {

            if (button >= 0 && button < GLFW_MOUSE_BUTTON_LAST) {
                if (action == GLFW_PRESS) {
                    mouseButtons[button] = true;

                } else if (action == GLFW_RELEASE) {
                    mouseButtons[button] = false;
                    processedMouseButtons[button] = false;
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

    public static boolean keyPress(int key){
        if (keys[key] && !processedKeys[key]) {
            processedKeys[key] = true;
            return true;
        }

        return false;
    }

    public static boolean mouseDown(int button){
        return mouseButtons[button];
    }
    public static boolean mousePress(int button){
        if (mouseButtons[button] && !processedMouseButtons[button]) {
            processedMouseButtons[button] = true;
            return true;
        }

        return false;
    }















}
