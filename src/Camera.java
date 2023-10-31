import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Vector;

public class Camera {

    Vector3f position = new Vector3f(0.0f,0.0f,0.0f);
    Vector3f front;
    Vector3f up;
    Vector3f right;
    Vector3f worldUp;
    Matrix4f view = new Matrix4f();
    float fieldOfView = 45.0f;



    public void Follow(Player player) {

        this.position = player.position;
        this.front = player.front;
        this.up = player.up;
        this.right = player.right;
        this.worldUp = player.worldUp;
        this.view = player.view;




        Vector3f tmp = new Vector3f();
        view = new Matrix4f().lookAt(position, position.add(front, tmp), up);

    }


    //shaderCompiler.setFloat("test", 1.0f);
    public void update(){


    }





}
