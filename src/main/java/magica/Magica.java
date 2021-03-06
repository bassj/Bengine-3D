package magica;

import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import bengine.Game;
import bengine.State;
import bengine.assets.AssetLoader;
import bengine.assets.AssetManager;
import bengine.assets.CubeMap;
import bengine.assets.Model;
import bengine.assets.Shader;
import bengine.assets.Texture;
import bengine.networking.PermissionManager;
import bengine.networking.messages.DebugMessage;
import bengine.networking.messages.HandshakeMessage;
import bengine.networking.messages.NetworkMessage;
import bengine.networking.messages.ObjectMessage;
import bengine.networking.messages.RPCMessage;
import bengine.networking.serialization.ObjectParser;
import bengine.networking.serialization.serializers.CollectionSerializer;
import bengine.networking.serialization.serializers.DoubleSerializer;
import bengine.networking.serialization.serializers.FloatSerializer;
import bengine.networking.serialization.serializers.IntSerializer;
import bengine.networking.serialization.serializers.LongSerializer;
import bengine.networking.serialization.serializers.PermissionSerializer;
import bengine.networking.serialization.serializers.QuaternionfSerializer;
import bengine.networking.serialization.serializers.StringSerializer;
import bengine.networking.serialization.serializers.Vector3fSerializer;
import bengine.networking.sync.SyncedObjectManager;
import magica.entities.Chicken;
import magica.states.ChickenAnimationTestState;
import magica.states.ChickenDemoState;
import magica.states.LightingTestState;
import magica.states.MagicaGame;

public class Magica extends Game {
	
	public static final String WINDOW_TITLE = "Chicken Simulator: 2018";
	
	private int width, height;
	private boolean isFullscreen;
	
	String playerName;
	SocketAddress addr;
	
	String demo;
	
	public Magica(int width, int height, boolean isFullscreen, String demoName) {
		super();
		this.width = width;
		this.height = height;
		this.isFullscreen = isFullscreen;
		this.demo = demoName;
	}
	
	@Override
	public void onConfigure() {
		createDisplay(width, height, isFullscreen, WINDOW_TITLE);
	}
	
	@Override
	protected void onCreated() {
		AssetLoader loader = new AssetLoader(this) {

			@Override
			protected void onLoaded(AssetManager assets) {
				State newState = new MagicaGame(assets);

				switchState(newState);
			}
			
		};
		
		
		loader.addAsset("simpleShader", new Shader(new File("./assets/shader/simple.json")));
		//loader.addAsset("defaultShader", new Shader(new File("./assets/shader/default.json")));
		//loader.addAsset("chickenModel", new Model(new File("./assets/misc/chicken.fbx")));
		//loader.addAsset("barnModel", new Model(new File("./assets/misc/barn.obj")));
		loader.addAsset("sphereModel", new Model(new File("./assets/misc/sphere.fbx")));
		//loader.addAsset("planeModel", new Model(new File("./assets/misc/plane.obj")));
		loader.addAsset("cubeModel", new Model(new File("./assets/misc/cube.obj")));
		//loader.addAsset("chickenTexture", new Texture(new File("./assets/textures/chicken.png")));
		//loader.addAsset("barnTexture", new Texture(new File("./assets/textures/barn.jpg")));
		//loader.addAsset("grassTexture", new Texture(new File("./assets/textures/Grass.png")));
		loader.addAsset("shadowShader", new Shader(new File("./assets/shader/shadow.json")));
		loader.addAsset("skyboxShader", new Shader(new File("./assets/shader/skybox.json")));
		loader.addAsset("skybox", new CubeMap(new File("./assets/skybox/bluecloud.json")));
		
		
		loader.addAssets(new File("./assets/chickendemo.assets"));
		
		switchState(loader.load()); //Switch to the loading state while we load the assets.
	}

	@Override
	protected void onUpdate(float delta) {
		glfwSetWindowTitle(getWindow(), String.format("(%d FPS) : %s", (int)Math.floor(1.0 / delta), WINDOW_TITLE));
		
	}
	
	@Override
	protected void onDestroyed() {
		if (currentState != null) {
			//TODO: Once i've finished reverting all this stuff from debugging, cuz this is probably a mem leak and i don't know cuz my gpu has like 12 gb of vram, and it's not significant enough to do anything to my computer.
			currentState.onDestroyed();
		}
	}
	
	public static void main(String[] args) {
		
		//Register everything for networking.
		NetworkMessage.registerMessage(HandshakeMessage.class);
		NetworkMessage.registerMessage(ObjectMessage.class);
		NetworkMessage.registerMessage(DebugMessage.class);
		NetworkMessage.registerMessage(RPCMessage.class);
		
		ObjectParser.registerType(Integer.class, new IntSerializer());
		ObjectParser.registerType(Float.class, new FloatSerializer());
		ObjectParser.registerType(String.class, new StringSerializer());
		ObjectParser.registerType(Double.class, new DoubleSerializer());
		ObjectParser.registerType(Long.class, new LongSerializer());
		ObjectParser.registerType(ArrayList.class, new CollectionSerializer());
		ObjectParser.registerType(PermissionManager.class, new PermissionSerializer());
		ObjectParser.registerType(Vector3f.class, new Vector3fSerializer());
		ObjectParser.registerType(Quaternionf.class, new QuaternionfSerializer());
		
		SyncedObjectManager.registerTrackedType(Chicken.class);
		
		MagicaLauncher launcher = new MagicaLauncher((int width, int height, boolean isFullscreen, String demoName, String serverAddress, String name) -> {
			Magica game = new Magica(width, height, isFullscreen, demoName);
			
			game.playerName = name;
			game.addr = new InetSocketAddress(serverAddress, 2290);
			
			
			game.create();
		});
		
		launcher.setVisible(true);
	}
}
