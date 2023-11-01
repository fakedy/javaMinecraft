import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
public class Player {

    // horrible code, horrible horrible horrible horrible horrible

    Vector3f position = new Vector3f(0.0f,68.0f,0.0f);
    Vector3f rotation;
    Vector3f scale;

    Vector3f front = new Vector3f(0.0f,0.0f,-1.0f);
    Vector3f up = new Vector3f(0.0f,1.0f,0.0f);
    Vector3f right = new Vector3f(1.0f,0.0f,0.0f);
    Vector3f worldUp = new Vector3f(0.0f,1.0f,0.0f);
    Matrix4f view = new Matrix4f();
    // euler Angles
    float yaw = -90.0f;
    float pitch = 0.0f;
    double speed = 20.0;
    float sens =  0.1f;


    public void update(){

        double velocity = speed * Time.deltaTime();
        Vector3f tmp = new Vector3f();
        if (InputManager.keyDown(GLFW.GLFW_KEY_W)) {
            // Move forward
            position.add(front.mul((float)velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_S)) {
            // Move forward
            position.sub(front.mul((float)velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_D)) {
            // Move forward
            position.add(right.mul((float)velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_A)) {
            // Move forward
            position.sub(right.mul((float)velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_SPACE)) {
            // Move forward
            position.add(new Vector3f(0.0f,(float)velocity,0.0f));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            // Move forward
            position.add(new Vector3f(0.0f,-(float)velocity,0.0f));

        }

        float mouseX = InputManager.xoffset*sens;
        float mouseY = InputManager.yoffset*sens;

        yaw   = mouseX;
        pitch = mouseY;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (true) {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;

        }
        calculateCamera();

    }

    void calculateCamera(){

        Vector3f Front = new Vector3f();
        Front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        Front.y = (float) (Math.sin(Math.toRadians(pitch)));
        Front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front = Front.normalize();

        right = front.cross(worldUp, new Vector3f()).normalize();
        up = right.cross(front, new Vector3f()).normalize();

    }

}
