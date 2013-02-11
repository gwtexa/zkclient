import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;


/** 
 * The purpose is to simplify reading operations by eliminating synchronous reads.
 * Use exists method with asynchronous callback to translate to WatchedEvent 
 * 
 * Synchronous read will be mapped to Watcher.NodeDataChanged event
 *
 */
public class ZkCallbackToWatcherAdapter implements StatCallback {
	
	private final Watcher watcher;
	
	public ZkCallbackToWatcherAdapter(Watcher watcher) {
		this.watcher = watcher;
	}
	
	/**
	 * StatCallback - follows 'exists' asynchronous read 
	 */
	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		WatchedEvent event;
		switch (rc) {
	        case Code.Ok:
	        	event = new WatchedEvent(EventType.NodeDataChanged, KeeperState.SyncConnected, path);
	        	break;
	        case Code.NoNode:
	        	event = new WatchedEvent(EventType.NodeDeleted, KeeperState.SyncConnected, path);
	        	break;
	        case Code.SessionExpired:
	        	event = new WatchedEvent(EventType.None, KeeperState.Expired, path);
	        	break;	        	
	        case Code.NoAuth:
	        	event = new WatchedEvent(EventType.None, KeeperState.AuthFailed, path);
	        	break;
	        case Code.ConnectionLoss:
	        	event = new WatchedEvent(EventType.None, KeeperState.Disconnected, path);
	        	break;
	        default:
	        	event = new WatchedEvent(EventType.None, KeeperState.Unknown, path);
	        	break;
	        	// We can try to retry for the rest of errors
	            //zk.exists(znode, true, this, null);
	            //return;
		}
		watcher.process(event);
	}

}
