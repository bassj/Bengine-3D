package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;
import entities.Entity;
import entities.Light;
import toolBox.Calc;
import toolBox.Loader;
import toolBox.OpenSimplexNoise;

public class FaceNet extends Entity{
	
	public TexturedModel model;
	
	private Random rand;
	private Loader loader;
	private World world;
	private List<Light> lights;
	private OpenSimplexNoise noise;
	
	public FaceNet(Loader loader, World world, Random rand, List<Light> lights) {
		super(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		this.loader = loader;
		this.world = world;
		this.rand = rand;
		this.lights = lights;
		this.noise = new OpenSimplexNoise();
	}
	
	
	public void createFaceMap(){
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Float> shades = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		
		
		//cycle
		for(int x = 0; x < world.XSIZE; x++){
			for(int y = 0; y < world.YSIZE; y++){
				for(int z = 0; z < world.ZSIZE; z++){
					if(world.voxels[x][y][z].solid){
						Vector3f position = new Vector3f(x, y, z);
						
						//set tileset
						Vector3f tileset = world.voxels[x][y][z].tileset;
						
						float mtplr = 0.1f;
						
						float xp = 0.5f;
						if(!world.checkSolid(x + 1, y, z)) xp *= mtplr;
						float xm = -0.5f;
						if(!world.checkSolid(x - 1, y, z)) xm *= mtplr;
						float yp = 0.5f;
						if(!world.checkSolid(x, y + 1, z)) yp *= mtplr;
						float ym = -0.5f;
						if(!world.checkSolid(x, y - 1, z)) ym *= mtplr;
						float zp = 0.5f;
						if(!world.checkSolid(x, y, z + 1)) yp *= mtplr;
						float zm = -0.5f;
						if(!world.checkSolid(x, y, z - 1)) zm *= mtplr;
						
						Vector3f c1 = new Vector3f(x + xp, y + yp, z + zm);
						Vector3f c2 = new Vector3f(x + xp, y + yp, z + zp);
						Vector3f c3 = new Vector3f(x + xp, y + ym, z + zp);
						Vector3f c4 = new Vector3f(x + xp, y + ym, z + zm);
						Vector3f c5 = new Vector3f(x + xm, y + yp, z + zm);
						Vector3f c6 = new Vector3f(x + xm, y + yp, z + zp);
						Vector3f c7 = new Vector3f(x + xm, y + ym, z + zp);
						Vector3f c8 = new Vector3f(x + xm, y + ym, z + zm);
						
						//x+
						if(!world.checkSolid(x + 1, y, z)){
							Vector3f p1 = new Vector3f(c1);
							Vector3f p2 = new Vector3f(c2);
							Vector3f p3 = new Vector3f(c3);
							Vector3f p4 = new Vector3f(c4);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//x-
						if(!world.checkSolid(x - 1, y, z)){
							Vector3f p1 = new Vector3f(c6);
							Vector3f p2 = new Vector3f(c5);
							Vector3f p3 = new Vector3f(c8);
							Vector3f p4 = new Vector3f(c7);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//y+
						if(!world.checkSolid(x, y + 1, z)){
							Vector3f p1 = new Vector3f(c1);
							Vector3f p2 = new Vector3f(c5);
							Vector3f p3 = new Vector3f(c6);
							Vector3f p4 = new Vector3f(c2);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 10;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.x,
									vertices, textures, shades, indices);
						}
						
						//y-
						if(!world.checkSolid(x, y - 1, z)){
							Vector3f p1 = new Vector3f(c4);
							Vector3f p2 = new Vector3f(c3);
							Vector3f p3 = new Vector3f(c7);
							Vector3f p4 = new Vector3f(c8);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 10;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.z,
									vertices, textures, shades, indices);
						}
						
						//z+
						if(!world.checkSolid(x, y, z + 1)){
							Vector3f p1 = new Vector3f(c2);
							Vector3f p2 = new Vector3f(c6);
							Vector3f p3 = new Vector3f(c7);
							Vector3f p4 = new Vector3f(c3);
							
							int LRUD = 0;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 1000;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//z-
						if(!world.checkSolid(x, y, z - 1)){
							Vector3f p1 = new Vector3f(c5);
							Vector3f p2 = new Vector3f(c1);
							Vector3f p3 = new Vector3f(c4);
							Vector3f p4 = new Vector3f(c8);
							
							int LRUD = 0;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 1000;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(position, p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						
					}
				}
			}
		}
		
		//convert
		float[] verticesArray = new float[vertices.size() * 3];
		for(int pointer = 0; pointer < vertices.size(); pointer++){
			verticesArray[pointer * 3 + 0] = vertices.get(pointer).x;
			verticesArray[pointer * 3 + 1] = vertices.get(pointer).y;
			verticesArray[pointer * 3 + 2] = vertices.get(pointer).z;
		}
		
		float[] texturesArray = new float[textures.size() * 3];
		for(int pointer = 0; pointer < textures.size(); pointer++){
			texturesArray[pointer * 3 + 0] = textures.get(pointer).x;
			texturesArray[pointer * 3 + 1] = textures.get(pointer).y;
			texturesArray[pointer * 3 + 2] = shades.get(pointer);
		}
		
		int[] indicesArray = new int[indices.size()];
		for(int pointer = 0; pointer < indices.size(); pointer++){
			indicesArray[pointer * 1 + 0] = indices.get(pointer);
		}
		
		System.out.println("Loading model with " + indicesArray.length / 3 + "tris");
		RawModel rawModel = loader.loadToVAO(verticesArray, texturesArray, indicesArray);
		ModelTexture texture = new ModelTexture(loader.loadTexture("T4"));
		
		model = new TexturedModel(rawModel, texture);
	}
	
	
	private void createFace(Vector3f position, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, int LRUD, int tileset,
			List<Vector3f> vertices, List<Vector2f> textures, List<Float> shades, List<Integer> indices){
		int index = vertices.size();
		
		vertices.add(p1);
		vertices.add(p2);
		vertices.add(p3);
		vertices.add(p4);
		
		shades.add(calcShade(position, p1));
		shades.add(calcShade(position, p2));
		shades.add(calcShade(position, p3));
		shades.add(calcShade(position, p4));
		
		setCoords(LRUD, tileset, textures);
		
		indices.add(index + 0);
		indices.add(index + 1);
		indices.add(index + 2);
		indices.add(index + 0);
		indices.add(index + 2);
		indices.add(index + 3);
	}
	
	
	private void setCoords(int LRUD, int tileset, List<Vector2f> textures){		
		textures.add(new Vector2f(1, 0));
		textures.add(new Vector2f(0, 0));
		textures.add(new Vector2f(0, 1));
		textures.add(new Vector2f(1, 1));
	}
	
	private float calcShade(Vector3f position, Vector3f point){
		
		float totalDiffuse = 0;
		for(Light light: lights){
			Vector3f toLight = new Vector3f(light.position.x - point.x, light.position.y - point.y, light.position.z - point.z);
			float mag = Calc.calculateMagnitude(toLight);
			if(mag > light.lightDist){
				continue;
			}
			toLight = Calc.normaliseVector(toLight);
			Vector3f normal = new Vector3f(position.x - point.x, position.y - point.y, position.z - point.z);
			normal.normalise();
			float diffuse = light.brightness * (Vector3f.dot(toLight, normal) + 1f) / 2f;
			
			diffuse *= 1 - Math.pow(mag / light.lightDist, light.dropOff);
			
			Vector3f checkPos = new Vector3f(point.x + 0.5f, point.y + 0.5f, point.z + 0.5f);
			float distTest = Math.min(mag, 16);
			for(int n = 0; n < distTest * 0; n++){
				Vector3f.add(checkPos, toLight, checkPos);
				if(world.checkSolid(checkPos)){
					diffuse *= 0.4f;
					break;
				}
			}
			totalDiffuse += diffuse;
			
		}
		
		return totalDiffuse;
	}
	
	
	private int randInSet(int[] set){
		return set[rand.nextInt(set.length)];
	}

}
