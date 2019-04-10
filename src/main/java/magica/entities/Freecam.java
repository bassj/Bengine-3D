package magica.entities;

import bengine.Scene;
import bengine.entities.Camera;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Freecam extends Entity {

    public static final int OBJECT_TYPE = generateTypeId();

    public float mouseSensitivity = 2.5f;
    private float movementSpeed = 5.0f;

    private Camera c;


    public Freecam(Vector3f position, Vector3f direction) {
        this.transform.position = position;
        this.transform.rotation.lookAlong(direction, this.transform.up());
    }

    public Freecam(Vector3f position) {
        super();
        this.transform.position = position;
    }


    public Freecam() {
        super();
    }

    @Override
    public void onCreated(Scene s) {
        this.c = s.getCamera();

        Mouse.lockCursor();
    }

    @Override
    public void onUpdate(float delta) {
        this.transform.rotation.rotateY(Mouse.getDX() * delta * -mouseSensitivity);
        this.transform.rotation.rotateLocalX(Mouse.getDY() * delta * -mouseSensitivity);

        Vector3f movement = new Vector3f();

        if (Keyboard.isKeyDown(GLFW_KEY_W)) {
            movement.add(this.transform.forwards().mul(movementSpeed * delta));
        }

        if (Keyboard.isKeyDown(GLFW_KEY_S)) {
            movement.add(this.transform.forwards().mul(-movementSpeed * delta));
        }

        if (Keyboard.isKeyDown(GLFW_KEY_A)) {
            movement.add(this.transform.right().mul(-movementSpeed * delta));
        }

        if (Keyboard.isKeyDown(GLFW_KEY_D)) {
            movement.add(this.transform.right().mul(movementSpeed * delta));
        }

        this.transform.position.add(movement);

        this.c.transform.position = this.transform.position;
        this.c.transform.rotation = this.transform.rotation;
    }

    @Override
    public void onDestroyed() {

    }

    @Override
    public void onRegistered() {

    }

    @Override
    public void onObjectUpdate() {

    }

    @Override
    public void onDeregistered() {

    }

    @Override
    public int getType() {
        return OBJECT_TYPE;
    }
}
