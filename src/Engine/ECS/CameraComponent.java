package Engine.ECS;

import Game.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CameraComponent {

    private GameObject owner;

    public Vector3f position = new Vector3f(0.0f,80.0f,0.0f);

    public float yaw = -90f;
    public float pitch = 0;

    public Matrix4f view = new Matrix4f();

    public Matrix4f proj = new Matrix4f();
    float fieldOfView = 55.0f;


    public CameraComponent(GameObject owner){
        this.owner = owner;

    }



    public void follow() {

        this.position = owner.getPosition();

        Vector3f tmp = new Vector3f();
        view = new Matrix4f().lookAt(position, position.add(owner.front, tmp), owner.up);
        proj = new Matrix4f().perspective((float) Math.toRadians(fieldOfView), (float) 1920 /1080, 0.01f, 1000.0f);

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

     public void calculateCamera(){

        Vector3f Front = new Vector3f();
        Front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        Front.y = (float) (Math.sin(Math.toRadians(pitch)));
        Front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        owner.front = Front.normalize();

        owner.right = owner.front.cross(owner.worldUp, new Vector3f()).normalize();
        owner.up = owner.right.cross(owner.front, new Vector3f()).normalize();

    }


}
