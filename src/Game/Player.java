package Game;

import Engine.ECS.CameraComponent;
import Engine.ECS.GameObject;
import Engine.InputManager;
import Engine.Renderer.Renderer;
import Engine.Time;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.*;
public class Player extends GameObject {

    // horrible code, horrible horrible horrible horrible horrible



    private double speed = 65.0;
    float sens =  0.1f;



    Player(){
        super();
        cameraComponent = new CameraComponent(this);
        Renderer.activeCamera = this.cameraComponent;
        Renderer.player = this;
        setPosition(new Vector3f(0.0f,80.0f,0.0f));

    }

    public void update() {

        cameraComponent.follow();

        double velocity = speed * Time.deltaTime();
        Vector3f tmp = new Vector3f();
        if (InputManager.keyDown(GLFW.GLFW_KEY_W)) {
            // Move forward
            position.add(front.mul((float) velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_S)) {
            // Move forward
            position.sub(front.mul((float) velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_D)) {
            // Move forward
            position.add(right.mul((float) velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_A)) {
            // Move forward
            position.sub(right.mul((float) velocity, tmp));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_SPACE)) {
            // Move forward
            position.add(new Vector3f(0.0f, (float) velocity, 0.0f));

        }
        if (InputManager.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            // Move forward
            position.add(new Vector3f(0.0f, -(float) velocity, 0.0f));

        }

        if (InputManager.mousePress(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {

            Vector3f rayOrigin = new Vector3f(position);
            Vector3f rayDirection = cameraComponent.castRay().normalize();
            Vector3f currentPos = rayOrigin;
            float stepSize = 0.2f;
            int maxSteps = 30;

            Vector3i tempPos;
            for (int i = 0; i < maxSteps; i++) {

                tempPos = new Vector3i(Math.round(currentPos.x),Math.round(currentPos.y),Math.round(currentPos.z));
                if (Utils.findChunkByPosition(tempPos) != null) {
                    Chunk chunk = Utils.findChunkByPosition(tempPos);
                    Vector3i pos = Utils.getBlockCoordWithinChunk(tempPos);
                    if(Utils.removeBlock(chunk, pos))
                        break;
                }
                currentPos = currentPos.add((rayDirection.mul(stepSize, new Vector3f())));

            }

        }

        if (InputManager.mousePress(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {

            // BUG placing blocks diagonally

            Blocks.BlockType block = Blocks.BlockType.PLANKS;
            Vector3f rayOrigin = new Vector3f(position);
            Vector3f rayDirection = cameraComponent.castRay().normalize();
            Vector3f currentPos = rayOrigin;
            float stepSize = 0.2f;
            int maxSteps = 30;

            Vector3f lastSafePos = new Vector3f(rayOrigin); // Track the last safe position before hitting a block

            for (int i = 0; i < maxSteps; i++) {
                Vector3f nextPos = currentPos.add(rayDirection.mul(stepSize, new Vector3f())); // Next position in float
                Vector3i nextPosInt = new Vector3i(Math.round(nextPos.x), Math.round(nextPos.y), Math.round(nextPos.z)); // Next position in int

                if (Utils.findChunkByPosition(nextPosInt) != null) {
                    Chunk chunk = Utils.findChunkByPosition(nextPosInt);
                    Vector3i blockPos = Utils.getBlockCoordWithinChunk(nextPosInt);
                    if(Utils.hitBlock(chunk, blockPos)) {
                        Vector3i lastSafePosInt = new Vector3i(Math.round(lastSafePos.x), Math.round(lastSafePos.y), Math.round(lastSafePos.z));
                        chunk = Utils.findChunkByPosition(lastSafePosInt);

                        // Place the block at the last safe position
                        if (Utils.putBlock(chunk, lastSafePosInt, block)) {
                            break;
                        }
                    } else {
                        lastSafePos = new Vector3f(nextPos); // Update lastSafePos to the current position if no hit
                    }
                }

                currentPos = nextPos; // Move the current position to the next position
            }
        }


        float mouseX = InputManager.xoffset*sens;
        float mouseY = InputManager.yoffset*sens;

        cameraComponent.yaw   = mouseX;
        cameraComponent.pitch = mouseY;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (true) {
            if (cameraComponent.pitch > 89.0f)
                cameraComponent.pitch = 89.0f;
            if (cameraComponent.pitch < -89.0f)
                cameraComponent.pitch = -89.0f;

        }
        cameraComponent.calculateCamera();

    }



}
