package zk.node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 
 * This is the ZooKeeper wrapper. 
 * The central client object that aggregates ZooKeeper and DefaultWatcher and provides utility methods like synchronous connect
 * 
 * With ZkFolder and ZkNode it simplifies ZK interface for 'listening only' clients. 
 * For example when they want to be notified with full configuration at start and notified later with    
 * Configuration is 
 *  
 *   
 */
public class ZkClient {
	
	private ZooKeeper zk;
	private DefaultWatcher defaultWatcher = new DefaultWatcher();
	
	
	public void registerNode(ZkNode zkNode) {
		defaultWatcher.registerNode(zkNode);
	}
	
	public ZooKeeper getZooKeeper() {
		return zk;
	}
	
    // To block any operation until ZooKeeper is connected. It's initialized
    // with count 1, that is, ZooKeeper connect state.
    java.util.concurrent.CountDownLatch connectedSignal = new java.util.concurrent.CountDownLatch(1);

    /**
     * Connects to ZooKeeper servers specified by hosts.
     *
     */
    public void connect(String hosts) throws IOException, InterruptedException {
		zk = new ZooKeeper(
		            hosts, // ZooKeeper service hosts
		            10000,  // Session timeout in milliseconds
		            defaultWatcher
		);
		connectedSignal.await();
    }

    /**
     * Closes connection with ZooKeeper
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
    	zk.close();
    }	
	
	
	
	/**
	 * Default watcher-dispatcher where ZkNodes register themselves for changes on particular znode. 
	 * This is notification only. The burden of fetching updated data or children is on ZkNodes.
	 * DefaultWatcher centralises connection status handling and dispatches events to proper objects.
	 * 
	 */
	private class DefaultWatcher implements Watcher {

		private Map<String, ZkNode> watchedNodes = new HashMap<String, ZkNode>(); 
		
		/**
		 * We want only one place where connection events will be handled. So there should only one default watcher implementation
		 * it should be also a dispatcher where custom listeners should register
		 */
		@Override
		public void process(WatchedEvent event) {
			if (event.getType() == Event.EventType.None) {
	    		if (event.getState() == KeeperState.SyncConnected) {
	    			// release lock if ZooKeeper is connected.
	    		    connectedSignal.countDown();
//	    		} else if (event.getState() == KeeperState.Expired) {
//	    			//TODO reconnect
	    		} else {
	    			new RuntimeException("Client not connected").printStackTrace();
	    		}
			} else {
				//dispatch to watched nodes based on path
				watchedNodes.get(event.getPath()).process(event);
			}
		}
		
		
		public void registerNode(ZkNode zkNode) {
			watchedNodes.put(zkNode.getPath(), zkNode);
		}

	}

	
}
