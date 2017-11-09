package networking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import toolBox.Assets;
import world.World;

public class Client implements Runnable{

    BufferedReader in;
    PrintWriter out;
    Socket socket;
    World world;
    
    boolean active = true;
    boolean multiplayer = false;
    
    public String name;
    
    public Client(boolean multiplayer) throws IOException{
    	this.multiplayer = multiplayer;
    	if(multiplayer){
    		setup();
    	}else{
    		in = null;
    		out = null;
    		name = "P0";
    	}
    }
    	
    public void setup() throws IOException{
    	// Make connection and initialize streams
        //String serverAddress = "10.0.1.10";
    	String serverAddress = "Bens-Laptop";
        socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        //setup connection with the server
        String line = "";
        while(!line.startsWith("CONNECTED")){
			try {
				line = in.readLine();
				if (line.startsWith("SETID")) {
					name = "P" + (int) (Math.random() * 99);
		            out.println(name);
		        }
			}catch(IOException e){
				e.printStackTrace();
			}
        }

        System.out.println("client setup done");
        
        while(!line.startsWith("START")){
        	line = in.readLine();
        }
    }
    
    public void start(World world){
    	if(!multiplayer) return;
    	
    	//connect to the world
    	this.world = world;
    	
    	//start up the reader
        new Thread(this).start();
    }
    
    public void updatePosition(String key, Vector3f position){
		sendData("p," + key + "," + position.x + "," + position.y + "," + position.z);
    }
    
    public void addPlayer(String key, Vector3f position){
		sendData("c," + key + "," + position.x + "," + position.y + "," + position.z);
    }
    
    public void sendData(String data){
    	if(!multiplayer) return;
    	out.println(data);
    }

	public void run() {
		System.out.println("Started a thread");
		while(active){
			//receive input
			try {
				String[] input = in.readLine().split(",");
				if(input[0].equalsIgnoreCase("c")){
					world.addDynEntity(input[1], new Player(new Vector3f(
							Float.parseFloat(input[2]),
							Float.parseFloat(input[3]),
							Float.parseFloat(input[4]))));
					System.out.println("added: " + input[1]);
				}
				else if(input[0].equalsIgnoreCase("p")){
					String key = input[1];
					world.dynEntities.get(key).position.x = Float.parseFloat(input[2]);
					world.dynEntities.get(key).position.y = Float.parseFloat(input[3]);
					world.dynEntities.get(key).position.z = Float.parseFloat(input[4]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
