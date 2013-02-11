package zk.node;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZkNode implements Watcher {

	protected final String path;
	protected final ZkClient zk;
	protected final ZkNodeListener zkNodeListener;
	
	public ZkNode(ZkClient zk, String path, ZkNodeListener zkNodeListener) {
		this.zk = zk;
		this.path = path;
		this.zkNodeListener = zkNodeListener;
		zk.registerNode(this);
	}

//	public ZkNode(ZkClient zk, String path) {
//		this(zk, path, null);
//	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public void process(WatchedEvent event) {
		//fetch node data and fire ZkNodeListener methods
		
	}
}
