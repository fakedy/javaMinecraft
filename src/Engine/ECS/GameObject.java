package Engine.ECS;

import org.joml.Vector3f;

public class GameObject {

    private final CollisionComponent collisionComponent;
    private final RenderComponent renderComponent;
    private final RigidbodyComponent rigidbodyComponent;

    private Vector3f position;



    GameObject(){
        this.collisionComponent = null;
        this.renderComponent = null;
        this.rigidbodyComponent = null;
    }


    void setPosition(Vector3f position){
        this.position = position;
    }

    Vector3f getPosition(Vector3f position){
        return this.position;
    }








}
