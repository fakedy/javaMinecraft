
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

public class Renderer {

    private long window;
    private Camera camera;
    ShaderCompiler shader;
    ShaderCompiler defaultShader;
    ShaderCompiler skyboxShader;

    static ArrayList<Chunk> renderObjects = new ArrayList<>();
    static Skybox skybox;

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

        defaultShader = new ShaderCompiler("src/resources/shaders/default_vertex.glsl", "src/resources/shaders/default_fragment.glsl");
        skyboxShader = new ShaderCompiler("src/resources/shaders/skybox_vertex.glsl", "src/resources/shaders/skybox_fragment.glsl");
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        }




        public void render(){
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(camera.fieldOfView), (float) 1920 /1080, 0.01f, 1000.0f);

            Matrix4f modelMatrix = new Matrix4f().identity();
            Matrix4f viewMatrix;
            viewMatrix = camera.view;

            defaultShader.use();
            defaultShader.setInt("ourTexture", 0);
            defaultShader.setMat4("projection", projectionMatrix);
            defaultShader.setMat4("view", viewMatrix);
            defaultShader.setMat4("model", modelMatrix);

            glBindTexture(GL_TEXTURE_2D, 1);
            for (int i = 0; i < renderObjects.size(); i++){

                glBindVertexArray(renderObjects.get(i).VAO);
                glDrawArrays(GL_TRIANGLES, 0, renderObjects.get(i).verts.length);
            }



            skyboxShader.use();
            Matrix4f skyboxViewMatrix = new Matrix4f(viewMatrix);  // Create a copy of the viewMatrix
            Matrix3f upperLeft3x3 = new Matrix3f(skyboxViewMatrix);  // Convert it to 3x3 to remove translation
            skyboxViewMatrix.set(upperLeft3x3);  // Set the 4x4 matrix with the 3x3

            skyboxShader.setMat4("view", skyboxViewMatrix);
            skyboxShader.setMat4("projection", projectionMatrix);

            glDepthFunc(GL_LEQUAL);
            glBindVertexArray(skybox.VAO);
            glBindTexture(GL_TEXTURE_CUBE_MAP, 2);
            glDrawArrays(GL_TRIANGLES, 0, skybox.skyboxVertices.length);
            glDepthFunc(GL_LESS);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
    }


}