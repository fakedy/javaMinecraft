package Engine.Renderer;


import static org.lwjgl.opengl.GL46.*;

public class SSBO {


    public int ssbo;



    SSBO(){
        ssbo = glGenBuffers();
    }


    public void bufferData(int vbo){

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, ssbo);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

    }








}
