package Engine.ECS;

import org.joml.Vector3f;

public class GameObject {

    public CollisionComponent collisionComponent = null;
    public RenderComponent renderComponent = null;
    public RigidbodyComponent rigidbodyComponent = null;
    public CameraComponent cameraComponent = null;

    protected Vector3f position;
    protected Vector3f rotation;

    protected Vector3f front = new Vector3f(0.0f,0.0f,-1.0f);
    protected Vector3f up = new Vector3f(0.0f,1.0f,0.0f);
    protected Vector3f right = new Vector3f(1.0f,0.0f,0.0f);
    protected Vector3f worldUp = new Vector3f(0.0f,1.0f,0.0f);



    protected GameObject(){

    }


    public Vector3f getPosition(){
        return this.position;
    }

    public void setPosition(Vector3f position){
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }








}
