package zk.node;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZkNode implements Watcher, DataCallback {

	protected final String path;
	protected final ZkClient zkClient;
	protected final ZkNodeListener zkNodeListener;
	
	/**
	 * znode handler. Actual znode does not have to exists on servers 
	 */
	public ZkNode(ZkClient zkClient, String path, ZkNodeListener zkNodeListener) {
		this.zkClient = zkClient;
		this.path = path;
		this.zkNodeListener = zkNodeListener;
		zkClient.registerNode(this);
		//initial watch set
		setWatch();
		//try to fetch data async, it may fail if znode does not exists
		zkClient.getZooKeeper().getData(path, false, this, null);
	}

//	public ZkNode(ZkClient zk, String path) {
//		this(zk, path, null);
//	}
	
	public String getPath() {
		return path;
	}
	
	public void setWatch() {
		//initial watch set
		zkClient.getZooKeeper().exists(path, true, new StatCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				//TODO check rc to see if call successful - it means that watch set
				//otherwise retry
			}
		}, null);		
	}
	
	
	@Override
	public void process(WatchedEvent event) {
		Event.EventType evType = event.getType();
		
		assert(evType != Event.EventType.None);
			
		if (zkNodeListener != null) {
			if (evType == Event.EventType.NodeDeleted) {
				zkNodeListener.nodeDeleted(path);
			} else if (evType == Event.EventType.NodeCreated || evType == Event.EventType.NodeDataChanged) {
				//fetch node data and fire ZkNodeListener methods
				//Stat stat = new Stat();
				//byte[] b = zkClient.getZooKeeper().getData(path, false, stat);
				zkClient.getZooKeeper().getData(path, false, this, null);
			}
		}
		setWatch();
	}
	
	
	@Override
	public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		if (zkNodeListener != null) {
			if (rc == Code.NoNode) {
				zkNodeListener.nodeDeleted(path);
			} else if (rc == Code.Ok) {
				zkNodeListener.nodePresent(path, data);
			} else {
				//TODO retry
			}
		}
	}
	
	
	public void create(byte[] b) throws KeeperException, InterruptedException {
		zkClient.getZooKeeper().create(path, b, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	public void delete() throws KeeperException, InterruptedException {
		zkClient.getZooKeeper().delete(path, -1);
	}
	public void setData(byte[] b) throws KeeperException, InterruptedException {
		zkClient.getZooKeeper().setData(path, b, -1);
	}
	
	
	
}
