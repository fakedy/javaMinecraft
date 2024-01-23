package Engine;

import org.lwjgl.opengl.*;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.io.FileNotFoundException;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL33.*;
public class ShaderCompiler {

    // oooh wow
    // shader compilation file :)

    // Load vertex Shader from file and compile shader

    int shaderProgram;
    public ShaderCompiler(String vertexPath, String fragmentPath){
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, readFile(vertexPath));
        glCompileShader(vertexShader);

        String infoLog;
        int success;
        success = GL33.glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if(success == 0){

            infoLog = GL33.glGetShaderInfoLog(vertexShader);
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" + infoLog);

        }

        // Load fragment Shader from file and compile shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readFile(fragmentPath));
        glCompileShader(fragmentShader);
        success = GL33.glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if(success == 0){

            infoLog = GL33.glGetShaderInfoLog(fragmentShader);
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" + infoLog);

        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        success = GL33.glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == 0){

            infoLog = GL33.glGetProgramInfoLog(shaderProgram);
            System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + infoLog);

        }
        // delete it because we made our program
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glUseProgram(shaderProgram);


    }

    public void use(){

        glUseProgram(shaderProgram);

    }

    String readFile (String path){

        StringBuilder content = new StringBuilder();

        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                content.append(data).append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error while trying to read file.");
            e.printStackTrace();
            //throw new RuntimeException("Error while trying to read file.");
        }

        return content.toString();
    }

    public void setBool( String name , boolean value){
        glUniform1i(glGetUniformLocation(shaderProgram, name), value ? 1 : 0);
    }

    public void  setInt( String name, int value){
        glUniform1i(glGetUniformLocation(shaderProgram, name), value);
    }

    void setFloat(String name, float value){
        glUniform1f(glGetUniformLocation(shaderProgram, name), value);
    }

    void setVec2(String name, Vector2f value){
        glUniform2f(glGetUniformLocation(shaderProgram, name), value.x, value.y);
    }
    public void setVec3(String name, Vector3f value){
        glUniform3f(glGetUniformLocation(shaderProgram, name), value.x, value.y, value.z);
    }

    void setMat3(String name, Matrix3f value){
        MemoryStack stack = MemoryStack.stackPush();
        FloatBuffer fb = value.get(stack.mallocFloat(9));
        glUniformMatrix3fv(glGetUniformLocation(shaderProgram, name), false ,fb);
        stack.pop();
    }
    public void setMat4(String name, Matrix4f value){
        MemoryStack stack = MemoryStack.stackPush();
        FloatBuffer fb = value.get(stack.mallocFloat(16));
        glUniformMatrix4fv(glGetUniformLocation(shaderProgram, name), false ,fb);
        stack.pop();
    }


}
