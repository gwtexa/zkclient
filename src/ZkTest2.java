import java.util.Arrays;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;

import zk.node.ZkClient;
import zk.node.ZkFolder;
import zk.node.ZkFolderListener;
import zk.node.ZkFolderStringAdapter;
import zk.node.ZkNode;
import zk.node.ZkNodeListener;


public class ZkTest2 {

	public static void main(String[] args) throws Exception {
		
		final ZkEmbeddedServer s = new ZkEmbeddedServer("zoo.cfg");
		
		System.out.println("Starting ZkEmbeddedServer");
		new Thread() {
			public void run() { s.start(); };
		}.start();
		
		Thread.sleep(1000);
		
		
		
		//Sample client API
		System.out.println("Starting ZkClient");
		ZkClient zkClient = new ZkClient();
		zkClient.connect("localhost");
		
		ZkNode zkfile1 = new ZkNode(zkClient, "/path/to/zkfile", new ZkNodeListener() {
			@Override
			public void nodeDeleted(String path) {
				System.out.println("nodeDeleted: " + path);
			}
			@Override
			public void nodePresent(String path, byte[] b) {
				System.out.println("nodePresent: " + path + " = " + new String(b));
			}
		});
		
		ZkFolderListener zkFolderListener = new ZkFolderStringAdapter() {
			@Override
			public void childrenDeleted(String path, String[] children) {
				System.out.println("childrenDeleted: " + Arrays.asList(children));
			}
			@Override
			public void childrenStringPresent(String path, Map<String, String> childrenMap) {
				System.out.println("childrenPresent: " + childrenMap);
			}
		};
		ZkFolder zkfolder1 = new ZkFolder(zkClient, "/path/to/zkfolder", zkFolderListener);
		
		System.out.println("Registered ZkListeners");
		
		Thread.sleep(5000);
		
		//zkfolder1.createChild("x", "lalala".getBytes());
		zkfolder1.deleteChild("y");
		
		//zkClient.getZooKeeper().create("/path/to/zkfolder/y", "Data created by client itself".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//All callback and watcher events in single thread so one instance of adapter is enough
		//ZkCallbackToWatcherAdapter adapter = new ZkCallbackToWatcherAdapter(defaultWatcher);
		//ZkFile constructor must call async exists, 
	}
	
}
