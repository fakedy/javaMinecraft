
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;



public class TextureLoader {


    static void loadTexture(String path) {

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
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, width.get(), height.get(), 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, data);
            } else {
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, width.get(), height.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, data);

            }
            STBImage.stbi_image_free(data);
            GL33.glActiveTexture(GL33.GL_TEXTURE0);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture);
            System.out.println(texture);
        }


    }

    static void loadCubemap(String[] paths) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            int texture;
            texture = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_CUBE_MAP, texture);


            for (int i = 0; i < paths.length; i++){

                var data = STBImage.stbi_load(paths[i], width, height, nrChannels, 0);

                if (data == null) {
                    throw new RuntimeException("Failed to load image: " + paths[i] + " " + STBImage.stbi_failure_reason());
                }



                GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_BORDER);	// set texture wrapping to GL_REPEAT (default wrapping method)
                GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_BORDER);
                GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
                GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
                // add cases for different channels
                if(nrChannels.get() == 4){
                    GL33.glTexImage2D(GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL33.GL_RGBA, width.get(), height.get(), 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, data);
                } else {
                    GL33.glTexImage2D(GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL33.GL_RGB, width.get(), height.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, data);
                }

                STBImage.stbi_image_free(data);
                width.clear();
                height.clear();
                nrChannels.clear();

                GL33.glActiveTexture(GL33.GL_TEXTURE0);


            }
            System.out.println(texture);
            GL33.glActiveTexture(GL33.GL_TEXTURE0);


        }

    }


}
