
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

public class Renderer {

    Window window;
    private Camera camera;
    ShaderCompiler shader;
    ShaderCompiler defaultShader;
    ShaderCompiler skyboxShader;
    ShaderCompiler shadowShader;

    static ArrayList<Chunk> renderObjects = new ArrayList<>();
    static Skybox skybox;
    int test;

    DepthMap depthmap;

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
        test = TextureLoader.loadTexture("src/resources/textures/terrain.png");


        depthmap = new DepthMap();
        defaultShader = new ShaderCompiler("src/resources/shaders/default_vertex.glsl", "src/resources/shaders/default_fragment.glsl");
        skyboxShader = new ShaderCompiler("src/resources/shaders/skybox_vertex.glsl", "src/resources/shaders/skybox_fragment.glsl");
        shadowShader = new ShaderCompiler("src/resources/shaders/shadow_vertex.glsl", "src/resources/shaders/shadow_fragment.glsl");
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        defaultShader.use();
        defaultShader.setInt("ourTexture", test);
        defaultShader.setInt("shadowMap", 1);

        }




        public void render(){
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


            // render to depth map

            glCullFace(GL_FRONT);
            glViewport(0, 0, depthmap.SHADOW_WIDTH, depthmap.SHADOW_HEIGHT);
            glBindFramebuffer(GL_FRAMEBUFFER, depthmap.depthMapFBO);
            glClear(GL_DEPTH_BUFFER_BIT);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, test);
            configureShaderAndMatrices(shadowShader);
            renderScene();
            glCullFace(GL_BACK);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);


            // render normally

            glViewport(0, 0, window.WINDOW_WIDTH, window.WINDOW_HEIGHT); // not working
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, test);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, depthmap.depthMap);


            configureShaderAndMatrices(defaultShader);

            renderScene();
            configureShaderAndMatricesSKybox();
            renderSkybox();


            glfwSwapBuffers(window.windowHandle); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
    }

    void renderScene(){


        //glBindTexture(GL_TEXTURE_2D, 1);
        for (int i = 0; i < renderObjects.size(); i++){

            glBindVertexArray(renderObjects.get(i).VAO);
            glDrawArrays(GL_TRIANGLES, 0, renderObjects.get(i).vertsAmount);
        }


    }

    void renderSkybox(){
        glDepthFunc(GL_LEQUAL);
        glBindVertexArray(skybox.VAO);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 2);
        glDrawArrays(GL_TRIANGLES, 0, skybox.skyboxVertices.length);
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
        shader.setInt("shadowMap", 1);
        shader.setInt("ourTexture", 0);
        shader.setMat4("projection", projectionMatrix);
        shader.setMat4("view", viewMatrix);
        shader.setMat4("model", modelMatrix);
        shader.setVec3("plyPos", camera.position);
        shader.setMat4("lightSpaceMatrix", lightSpaceMatrix);
        shader.setVec3("lightPos", lightPos);
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
    }


}