import org.lwjgl.opengl.*;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;
public class ShaderCompiler {

    // oooh wow
    // shader compilation file :)

    // Load vertex Shader from file and compile shader

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

    }

    String readFile (String path){

        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                return data;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error while trying to read file.");
            e.printStackTrace();
            //throw new RuntimeException("Error while trying to read file.");
        }

        return "";
    }



}
