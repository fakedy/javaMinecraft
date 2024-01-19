
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class TextureLoader {


    static int loadTexture(String path) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            var data = STBImage.stbi_load(path, width, height, nrChannels, 0);


            if (data == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            int texture;
            texture = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_BORDER);	// set texture wrapping to GL_REPEAT (default wrapping method)
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_BORDER);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
            // add cases for different channels
            if(nrChannels.get() == 4){
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_SRGB_ALPHA, width.get(), height.get(), 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, data);
            } else {
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_SRGB, width.get(), height.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, data);

            }
            STBImage.stbi_image_free(data);
            GL33.glActiveTexture(GL33.GL_TEXTURE0);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture);
            System.out.println(texture);
            return texture;
        }

    }

    static void loadCubemap(String[] paths) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            int texture;
            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_CUBE_MAP, texture);


            for (int i = 0; i < paths.length; i++){

                var data = STBImage.stbi_load(paths[i], width, height, nrChannels, 0);

                if (data == null) {
                    throw new RuntimeException("Failed to load image: " + paths[i] + " " + STBImage.stbi_failure_reason());
                }



                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                // add cases for different channels
                if(nrChannels.get() == 4){
                    glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_SRGB_ALPHA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
                } else {
                    glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_SRGB, width.get(), height.get(), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
                }

                STBImage.stbi_image_free(data);
                width.clear();
                height.clear();
                nrChannels.clear();

            }
            System.out.println(texture);
        }
    }

    static int loadArrayTextures(String textureDirectory){


        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            String[] texturePaths;

            texturePaths = readDirectoryPaths(textureDirectory);

            int texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D_ARRAY, texture);

            // check first image
            var firstImage = STBImage.stbi_load(texturePaths[0], width, height, nrChannels, 0);
            if (firstImage == null) {
                throw new RuntimeException("Failed to load first image: " + texturePaths[0] + " " + STBImage.stbi_failure_reason());
            }
            STBImage.stbi_image_free(firstImage);

            glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_SRGB8_ALPHA8, width.get(0), height.get(0), texturePaths.length);

            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);


            for (int i = 0; i < texturePaths.length; i++) {
                width.clear();
                height.clear();
                nrChannels.clear();
                var data = STBImage.stbi_load(texturePaths[i], width, height, nrChannels, 0);
                if (data == null) {
                    throw new RuntimeException("Failed to load image: " + texturePaths[i] + " " + STBImage.stbi_failure_reason());
                }

                if(nrChannels.get() == 4){
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width.get(0), height.get(0), 1,  GL_RGBA, GL_UNSIGNED_BYTE, data);
                    System.out.println(glGetError());
                } else {
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width.get(0), height.get(0), 1,  GL_RGB, GL_UNSIGNED_BYTE, data);
                    System.out.println(glGetError());
                }

                STBImage.stbi_image_free(data);
            }
            System.out.println(texture);
            return texture;
        }
    }

    private static String[] readDirectoryPaths(String textureDirectory){

        ArrayList<String> texturePaths = new ArrayList<>();

        try {
            Files.list(Paths.get(textureDirectory)).forEach(path -> texturePaths.add(textureDirectory + "/" + path.getFileName()));
        } catch(IOException e){
            System.err.println("An error while reading file names has occured: " + e.getMessage());
        }

        String[] tempArr = new String[texturePaths.size()];

        for(int i = 0; i < texturePaths.size(); i++){
            tempArr[i] = texturePaths.get(i);
        }

        return tempArr;
    }

    static String[] readDirectoryFiles(String textureDirectory){

        ArrayList<String> texturePaths = new ArrayList<>();

        try {
            Files.list(Paths.get(textureDirectory)).forEach(path -> texturePaths.add(String.valueOf(path.getFileName()).split("\\.")[0].toUpperCase()));
        } catch(IOException e){
            System.err.println("An error while reading file names has occured: " + e.getMessage());
        }

        String[] tempArr = new String[texturePaths.size()];

        for(int i = 0; i < texturePaths.size(); i++){
            tempArr[i] = texturePaths.get(i);
            System.out.println(tempArr[i]);
        }

        return tempArr;
    }


}
