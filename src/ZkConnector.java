import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;


//https://issues.apache.org/jira/browse/ZOOKEEPER-107

public class ZkConnector {

    ZooKeeper zooKeeper;
    
    final static String myPath = "/game_is_over";

    // To block any operation until ZooKeeper is connected. It's initialized
    // with count 1, that is, ZooKeeper connect state.
    java.util.concurrent.CountDownLatch connectedSignal = new java.util.concurrent.CountDownLatch(1);

    /**
     * Connects to ZooKeeper servers specified by hosts.
     *
     */
    public void connect(String hosts) throws IOException, InterruptedException {
	zooKeeper = new ZooKeeper(
                hosts, // ZooKeeper service hosts
                5000,  // Session timeout in milliseconds
		new Watcher() {
        	    @Override
        	    public void process(WatchedEvent event) {
        	    	// release lock if ZooKeeper is connected.
	        		if (event.getState() == KeeperState.SyncConnected) {
	        		    connectedSignal.countDown();
	        		}
        	    }
        	}
	);
	connectedSignal.await();
    }

    /**
     * Closes connection with ZooKeeper
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
    	zooKeeper.close();
    }

    /**
     * @return the zooKeeper
     */
    public ZooKeeper getZooKeeper() {
        // Verify ZooKeeper's validity
        if (null == zooKeeper || !zooKeeper.getState().equals(States.CONNECTED)){
	    throw new IllegalStateException ("ZooKeeper is not connected.");
        }
        return zooKeeper;
    }

    
    public static void main(String[] args) throws Exception {
    	ZkConnector zkc = new ZkConnector();
    	zkc.connect("localhost");
    	ZooKeeper zk = zkc.getZooKeeper();

    	zk.exists(myPath, new Watcher() {		// Anonymous Watcher
    		@Override
    		public void process(WatchedEvent event) {
    		   // check for event type NodeCreated
    	   	   boolean isNodeCreated = event.getType().equals(EventType.NodeCreated);
    		   // verify if this is the defined znode
    		   boolean isMyPath = event.getPath().equals(myPath);

    		   if (isNodeCreated && isMyPath) {
    			//<strong>TODO</strong>: send an email or whatever
    		   }
    		}
    	});

    	
    	//on other host:
//    	zk.create(
//    			myPath, 		// Path of znode
//    			null,			// Data not needed.
//    			Ids.OPEN_ACL_UNSAFE, 	// ACL, set to Completely Open.
//    			CreateMode.PERSISTENT	// Znode type, set to Persistent.
//    		);
    	
	}
    
}

	
	
	
	

	