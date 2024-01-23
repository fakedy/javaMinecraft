package Engine.Renderer;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.*;


public class DepthMap {



    public int depthMapFBO;
    public int SHADOW_WIDTH = 8172, SHADOW_HEIGHT = 8172;
    public int depthMap;

    DepthMap(){

        depthMapFBO = GL33.glGenFramebuffers();
         // shadow resolution
        depthMap = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, depthMap);
        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL33.GL_DEPTH_COMPONENT, GL33.GL_FLOAT, 0L );
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_BORDER);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_BORDER);
        float[] borderColor = { 1.0f, 1.0f, 1.0f, 1.0f };
        GL33.glTexParameterfv(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_BORDER_COLOR, borderColor);

        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, depthMapFBO);
        GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_TEXTURE_2D, depthMap, 0);
        GL33.glDrawBuffer(GL33.GL_NONE);
        GL33.glReadBuffer(GL33.GL_NONE);
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);



    }

}
