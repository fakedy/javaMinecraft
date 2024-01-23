package Game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

    public Vector3f position = new Vector3f(0.0f,0.0f,0.0f);
    Vector3f front;
    Vector3f up;
    Vector3f right;
    Vector3f worldUp;
    public Matrix4f view = new Matrix4f();
    public Matrix4f proj = new Matrix4f();
    float fieldOfView = 55.0f;



    public void Follow(Player player) {

        this.position = player.position;
        this.front = player.front;
        this.up = player.up;
        this.right = player.right;
        this.worldUp = player.worldUp;
        this.view = player.view;




        Vector3f tmp = new Vector3f();
        view = new Matrix4f().lookAt(position, position.add(front, tmp), up);
        proj = new Matrix4f().perspective((float) Math.toRadians(fieldOfView), (float) 1920 /1080, 0.01f, 1000.0f);

    }


    //shaderCompiler.setFloat("test", 1.0f);
    public void update(){


    }

    public Vector3f castRay(){

        Vector4f rayClip = new Vector4f(0.0f, 0.0f, -1.0f, 1.0f);


        Matrix4f inverseProjection = proj.invert(new Matrix4f());
        Vector4f rayEye = inverseProjection.transform(rayClip);
        rayEye.z = -1.0f;

        Matrix4f inverseView =  view.invert(new Matrix4f());
        Vector4f rayWorld = inverseView.transform(new Vector4f(rayEye.x, rayEye.y, rayEye.z,0.0f)).normalize();
        return new Vector3f(rayWorld.x,rayWorld.y,rayWorld.z);
    }





}
