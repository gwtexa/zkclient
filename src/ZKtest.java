import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


public class ZKtest implements Watcher {

	public static void main(String[] args) throws Exception {
		new ZKtest().run();
		
	}
	
	public void run() throws Exception {
		//Watcher passed in constructor is the default watcher - client connectivity watcher
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 3000, this);
		
		
		
		List<String> list = zk.getChildren("/", false);
		System.out.println("list=" + list);
		Stat stat = new Stat();
//		byte[] b = zk.getData("/zktest", false, stat);
//		String s = new String(b);
//		System.out.println("s=" + s);
//		System.out.println("stat=" + stat);
		
		if (zk.exists("/zktest2", false) != null) {
			zk.delete("/zktest2", -1);
		}
		zk.create("/zktest2", "second zk test".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		byte[] b = zk.getData("/zktest2", false, stat);
		System.out.println("zktest2=" + new String(b));


		
		
		
		
		
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("event=" + event);
		
	}
	
}














