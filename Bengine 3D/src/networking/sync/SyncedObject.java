package networking.sync;

import static networking.Util.generateId;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import networking.Endpoint;
import networking.PermissionManager;
import networking.messages.RPCMessage;

public abstract class SyncedObject {
	
	private static int currentTypeId = 0;
	
	public PermissionManager visibility = new PermissionManager();
	public PermissionManager mutability = new PermissionManager();
	
	private SyncedObjectManager manager; 
	private Endpoint endpoint;
	
	
	@Retention(RetentionPolicy.RUNTIME)
	public
	@interface SyncedField {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public 
	@interface RPC {
		String value(); //Marks a function to be able to be executed over the network.
		
		public static final int SERVER_ONLY = 0;
		public static final int ALL_REMOTES = 1;
	}
	
	
	private long ownerId;
	private long objectId;
	
	public SyncedObject() {
		this.objectId = generateId();
	}
	
	public abstract void onRegistered();
	public abstract void onObjectUpdate();
	
	protected void RPC(String function, int rpcMode, Object ...params) {
		RPCMessage message = new RPCMessage(this, function, params, rpcMode);
		
		endpoint.getConnection().send(message);
	}
	
	protected boolean isLocalAuthority() {
		return (getEndpoint().getEndpointId() == getOwner() && !getEndpoint().isRemote());
	}
	
	void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	void setManager(SyncedObjectManager manager) {
		this.manager = manager;
	}
	
	public void setOwner(long owner) {
		this.ownerId = owner;
	}
	
	void setInstanceId(long instanceId) {
		this.objectId = instanceId;
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	public SyncedObjectManager getManager() {
		return manager;
	}
	
	public long getInstanceID() {
		return objectId;
	}
	
	public long getOwner() {
		return this.ownerId;
	}
	
	public abstract int getType();
	
	public static int generateTypeId() {
		return currentTypeId++;
	}
}