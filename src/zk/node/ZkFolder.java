package zk.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * ZkFolder is also a ZkFile
 *
 */
public class ZkFolder extends ZkNode implements Watcher, ChildrenCallback {

	private ZkFolderListener zkFolderListener;
	
	/**
	 * Cache previous children name list
	 */
	private List<String> prevChildren = new ArrayList<String>();
	
	public ZkFolder(ZkClient zk, String path, ZkFolderNodeListener zkFolderNodeListener) {
		super(zk, path, zkFolderNodeListener);
		init(zkFolderNodeListener);
	}

	public ZkFolder(ZkClient zk, String path, ZkFolderListener zkFolderListener) {
		super(zk, path, null);
		init(zkFolderListener);
	}
	
	private void init(ZkFolderListener zkFolderListener) {
		this.zkFolderListener = zkFolderListener;
		//fetch children async and set watch on folder
		setChildrenWatch();
		//the above may fail if folder does not exist so set also exists watch
		setWatch();		
	}
	
	
	
	public void setChildrenWatch() {
		zkClient.getZooKeeper().getChildren(path, true, this, null);
	}
	
	@Override
	public void process(WatchedEvent event) {
		super.process(event);

		Event.EventType evType = event.getType();
		assert(evType != Event.EventType.None);
			
		//If zkfolder created we should also setChildrenWatch as the one in constructor probably failed if znode didn't exist
		if (evType == Event.EventType.NodeChildrenChanged || evType == Event.EventType.NodeCreated) {
			setChildrenWatch();
		}
	}
	
	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		if (zkFolderListener != null) {
			if (rc == Code.NoNode) {
				//Do nothing, we should be notified about deleting in superclass
			} else if (rc == Code.Ok) {
				List<String> added = new ArrayList<String>(children);
				added.removeAll(prevChildren);
				List<String> removed = new ArrayList<String>(prevChildren);
				removed.removeAll(children);
				
				if (!removed.isEmpty()) {
					zkFolderListener.childrenDeleted(path, removed.toArray(new String[] {}));
				}
				if (!added.isEmpty()) {
					final Map<String, byte[]> childrenMap = new HashMap<String, byte[]>();
					for (String child : added) {
						//TODO check if Stat is needed and what we can benefit from it
						Stat stat = new Stat();
						
						//TODO check if exceptions happen here and how to handle it
						try {
							byte[] data = zkClient.getZooKeeper().getData(path + "/" + child, false, stat);
							childrenMap.put(child, data);
							
							//The code below will not work - let the library user register for children updates manually
							//We are going to fire childrenPresent() only on children existance change (create, delete)
							
							//Watch children updates - hard to do 
//							new ZkNode(zkClient, path + "/" + child, new ZkNodeListener() {
//								@Override
//								public void nodePresent(String path, byte[] b) {
//									//node created or updated first time fetched
									//Wrong below - we need to call it only on child update
//									zkFolderListener.childrenPresent(path, singleChildMap);
//								}
//								@Override
//								public void nodeDeleted(String path) {
//									//Ignore deleting node - it should be handled by getChildren watch
//								}
//							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					

					zkFolderListener.childrenPresent(path, childrenMap);
				}
				
				prevChildren = new ArrayList<String>(children);
				
			} else {
				//TODO retry
			}
		}	
		
	}
	
	
	public void createChild(String childName, byte[] b) throws KeeperException, InterruptedException {
		zkClient.getZooKeeper().create(path + "/" + childName, b, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	public void deleteChild(String childName) throws KeeperException, InterruptedException {
		zkClient.getZooKeeper().delete(path + "/" + childName, -1);
	}
	

	
	
}
