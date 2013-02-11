package zk.node;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * ZkFolder is also a ZkFile
 *
 */
public class ZkFolder extends ZkNode implements Watcher {

	private ZkFolderListener zkFolderListener;
	
	public ZkFolder(ZkClient zk, String path, ZkFolderNodeListener zkFolderNodeListener) {
		super(zk, path, zkFolderNodeListener);
	}

	public ZkFolder(ZkClient zk, String path, ZkFolderListener zkFolderListener) {
		super(zk, path, null);
		this.zkFolderListener = zkFolderListener;
	}
	
	
	@Override
	public void process(WatchedEvent event) {
		//fetch node data and fire ZkNodeListener methods
		
	}

	
}
