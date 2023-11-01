
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

public class Renderer {

    private long window;
    private Camera camera;
    ShaderCompiler shader;

    static ArrayList<Chunk> renderObjects = new ArrayList<>();

    public Renderer(Window window, Camera camera){
        this.window = window.getWindowHandle();
        this.camera = camera;
    }

    public void setupRender (){
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();



        glEnable(GL_DEPTH_TEST);

        }




        public void render(){
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(camera.fieldOfView), (float) 1920 /1080, 0.01f, 1000.0f);

            Matrix4f modelMatrix = new Matrix4f().identity();
            Matrix4f viewMatrix;
            viewMatrix = camera.view;

            shader.setMat4("projection", projectionMatrix);
            shader.setMat4("view", viewMatrix);
            shader.setMat4("model", modelMatrix);

            for (int i = 0; i < renderObjects.size(); i++){

                glBindVertexArray(renderObjects.get(i).VAO);
                glDrawArrays(GL_TRIANGLES, 0, 36);
            }
            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
    }


}