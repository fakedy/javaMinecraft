
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

import java.awt.*;
import java.util.ArrayList;

public class Renderer {

    Window window;
    private Camera camera;
    ShaderCompiler shader;
    ShaderCompiler defaultShader;
    ShaderCompiler skyboxShader;
    ShaderCompiler framebufferShader;

    static ArrayList<Chunk> renderObjects = new ArrayList<>();
    static Skybox skybox;
    int test;

    FrameBuffer framebuffer;

    Vector3f lightPos = new Vector3f(60f, 160.0f, 15.0f);

    public Renderer(Window window, Camera camera){
        this.window = window;
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
        glEnable(GL_FRAMEBUFFER_SRGB);
        //test = TextureLoader.loadTexture("src/resources/textures/terrain.png");

        // Array texture testing
        test = TextureLoader.loadArrayTextures("src/resources/textures/block");


        framebuffer = new FrameBuffer();
        defaultShader = new ShaderCompiler("src/resources/shaders/default_vertex.glsl", "src/resources/shaders/default_fragment.glsl");
        skyboxShader = new ShaderCompiler("src/resources/shaders/skybox_vertex.glsl", "src/resources/shaders/skybox_fragment.glsl");
        framebufferShader = new ShaderCompiler("src/resources/shaders/framebuffer_vertex.glsl", "src/resources/shaders/framebuffer_fragment.glsl");
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);


        framebufferShader.use();
        framebufferShader.setInt("screenTexture", 0);

        defaultShader.use();
        defaultShader.setInt("ourTexture", test);



        }


        public void render(){


            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.frameBuffFBO);

            glEnable(GL_DEPTH_TEST);

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer

            glViewport(0, 0, Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
            framebuffer.update();

            configureShaderAndMatricesSKybox();

            renderSkybox();


            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D_ARRAY, test);
            configureShaderAndMatrices(defaultShader);
            renderScene();


            // render quad



            glBindFramebuffer(GL_FRAMEBUFFER, 0);


            glDisable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT);

            framebufferShader.use();
            glBindVertexArray(framebuffer.quadVAO);
            glBindTexture(GL_TEXTURE_2D, framebuffer.texture);
            glDrawArrays(GL_TRIANGLES, 0, 6);


            glfwSwapBuffers(window.windowHandle); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
    }

    void renderScene(){
        //glBindTexture(GL_TEXTURE_2D, 1);
        for (int i = 0; i < renderObjects.size(); i++){

            glBindVertexArray(renderObjects.get(i).mesh.opaqueVAO);

            glEnable(GL_CULL_FACE);
            glDisable(GL_BLEND);
            glDrawArrays(GL_TRIANGLES, 0, renderObjects.get(i).mesh.opaqueVertsAmount);

        }

        //glBindTexture(GL_TEXTURE_2D, 1);
        for (int i = 0; i < renderObjects.size(); i++){

            glDisable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBindVertexArray(renderObjects.get(i).mesh.transVAO);

            glDrawArrays(GL_TRIANGLES, 0, renderObjects.get(i).mesh.transVertsAmount);
        }
    }

    void renderSkybox(){
        glDepthFunc(GL_LEQUAL);
        glBindVertexArray(skybox.VAO);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 2);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glDepthFunc(GL_LESS);

    }

    void configureShaderAndMatrices(ShaderCompiler shader){

        Matrix4f projectionMatrix;
        projectionMatrix = camera.proj;
        Matrix4f modelMatrix = new Matrix4f().identity();
        Matrix4f viewMatrix;
        viewMatrix = camera.view;
        float near_plane = 0.1f, far_plane = World.worldSizeX*World.chunkSizeX;
        Matrix4f lightProjection = new Matrix4f().ortho(-World.worldSizeX*World.chunkSizeX, World.worldSizeX*World.chunkSizeX, -World.worldSizeX*World.chunkSizeX, World.worldSizeX*World.chunkSizeX, near_plane, far_plane);


        lightPos = camera.position;
        /*
        if(InputManager.keyDown(GLFW_KEY_UP)){
            lightPos.add(new Vector3f(0.1f,0.0f,0.0f));
        }
        if(InputManager.keyDown(GLFW_KEY_DOWN)){
            lightPos.add(new Vector3f(-0.1f,0.0f,0.0f));
        }
        if(InputManager.keyDown(GLFW_KEY_RIGHT)){
            lightPos.add(new Vector3f(0.0f,0.0f,0.1f));
        }
        if(InputManager.keyDown(GLFW_KEY_LEFT)){
            lightPos.add(new Vector3f(0.0f,0.0f,-0.1f));
        }

         */

        if(InputManager.keyDown(GLFW_KEY_F1)){
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        }
        if(InputManager.keyDown(GLFW_KEY_F2)){
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        }

        Matrix4f lightView = new Matrix4f().lookAt(lightPos, new Vector3f(World.worldSizeX*World.chunkSizeX, 74.0f,  World.worldSizeX*World.chunkSizeZ), new Vector3f(0.0f, 1.0f,  0.0f));
        Matrix4f lightSpaceMatrix = lightProjection.mul(lightView, new Matrix4f());

        shader.use();
        shader.setInt("ourTexture", 0);
        shader.setMat4("projection", projectionMatrix);
        shader.setMat4("view", viewMatrix);
        shader.setMat4("model", modelMatrix);
        shader.setVec3("plyPos", camera.position);
        shader.setMat4("lightSpaceMatrix", lightSpaceMatrix);
        shader.setVec3("lightPos", lightPos);
        shader.setInt("fogDist", World.fogDist);
    }


    void configureShaderAndMatricesSKybox(){

        Matrix4f projectionMatrix;
        projectionMatrix = camera.proj;
        Matrix4f viewMatrix;
        viewMatrix = camera.view;

        skyboxShader.use();
        Matrix4f skyboxViewMatrix = new Matrix4f(viewMatrix);  // Create a copy of the viewMatrix
        Matrix3f upperLeft3x3 = new Matrix3f(skyboxViewMatrix);  // Convert it to 3x3 to remove translation
        skyboxViewMatrix.set(upperLeft3x3);  // Set the 4x4 matrix with the 3x3

        skyboxShader.setMat4("view", skyboxViewMatrix);
        skyboxShader.setMat4("projection", projectionMatrix);
        skyboxShader.setVec3("plyPos", camera.position);
    }


}