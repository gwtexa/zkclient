import zk.node.ZkClient;
import zk.node.ZkFolder;
import zk.node.ZkFolderListener;
import zk.node.ZkNode;
import zk.node.ZkNodeListener;


public class ZkTest2 {

	public static void main(String[] args) throws Exception {
		
		//Sample client API
		
		ZkClient zkClient = new ZkClient();
		zkClient.connect("localhost");
		
		ZkNode zkfile1 = new ZkNode(zkClient, "/path/to/zkfile", new ZkNodeListener() {
			@Override
			public void nodeDeleted() {
			}
			@Override
			public void nodePresent(byte[] b) {
			}
		});
		
		ZkFolderListener zkFolderListener = new ZkFolderListener() {
			@Override
			public void childPresent(String path, byte[] data) {
				
			}
			@Override
			public void childDeleted(String path) {
				
			}
		};
		ZkFolder zkfolder1 = new ZkFolder(zkClient, "/path/to/zkfolder", zkFolderListener);
		
		//All callback and watcher events in single thread so one instance of adapter is enough
		//ZkCallbackToWatcherAdapter adapter = new ZkCallbackToWatcherAdapter(defaultWatcher);
		
		//client API:
		//zk = new ZooKeeper(..., defaultWatcher)
		//zkfile = new ZkFile(zk, defaultWatcher, path) {
		
		
		//}
		//zkfolder = new ZkFolder(zk, defaultWatcher, path)
		
		//ZkFile constructor must call async exists, 
	}
	
}
