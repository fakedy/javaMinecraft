import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {


// here we will setup the game window

    private long windowHandle;

    public void run(){

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");


        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public Window(int width, int height, String title) {
        // Initialize and create the window here
        // Set windowHandle value
        GLFWErrorCallback.createPrint(System.err).set();

        // if GLFW fails to init
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // window is visible
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // window is resizable

        // create window
        windowHandle = glfwCreateWindow(width, height, title, NULL,NULL);
        // if Window doesn't create
        if(windowHandle == NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Key callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(windowHandle);

        // v-sync
        glfwSwapInterval(0);

        glfwShowWindow(windowHandle);

    }

    public long getWindowHandle(){
        return windowHandle;
    }

}
