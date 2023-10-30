import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    Vector3f position;
    float fieldOfView = 45.0f;




    public void Follow(Player player) {

        this.position = player.position();

    }


    //shaderCompiler.setFloat("test", 1.0f);
    public void update(){


    }




}
