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

	public static void main(String[] args) {
		try {
			init();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void init() throws Exception {
		
		final ZkEmbeddedServer s = new ZkEmbeddedServer("zoo.cfg");
		
		System.out.println("Starting ZkEmbeddedServer");
		new Thread() {
			public void run() {
				try {
					s.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		Thread.sleep(20000);
		
		
		
		//Sample client API
		System.out.println("Starting ZkClient");
		ZkClient zkClient = new ZkClient();
		//zkClient.connect("localhost");
		zkClient.connect(s.getConfig().getClientPortAddress().getHostName() + ":" + s.getConfig().getClientPortAddress().getPort());
		
		
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
		
		System.out.println("Before creating ZkFolder");
		
		new ZkFolder(zkClient, "/path", null).createIfNotExists("whatever".getBytes());
		new ZkFolder(zkClient, "/path/to", null).createIfNotExists("whatever".getBytes());
		ZkFolder zkfolder1 = new ZkFolder(zkClient, "/path/to/zkfolder", zkFolderListener);
		System.out.println("ZkFolder handler created");
		zkfolder1.createIfNotExists("folder content".getBytes());
		
		System.out.println("Registered ZkListeners");
		
		for (int i = 0; i < 5; i++) { 
			Thread.sleep(5000);		
			String rand = String.valueOf(System.currentTimeMillis() % 100);		
			zkfolder1.createChildIfNotExists(rand, String.valueOf(i).getBytes());
		}
		
		Thread.sleep(3600000);
	}
	
}
