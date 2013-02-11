import java.io.IOException;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;


public class ZkEmbeddedServer extends ZooKeeperServerMain {

	private final ServerConfig serverConfig;
	
	public ZkEmbeddedServer(String cfgFile) {
		QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
		try {
			quorumConfig.parse(cfgFile);
		} catch(Exception e) {
		    throw new RuntimeException(e);
		}
		serverConfig = new ServerConfig();
		serverConfig.readFrom(quorumConfig);
	}
	
	public void start() {
		try {
			runFromConfig(serverConfig);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}		
	
	public void shutdown() { 
		super.shutdown();
	}
	
	public static void updateZooCfg() {
		//TODO dump /zoocfg znode into zoo.cfg file
		//include more static list of active vboxes in the config file
	}

	public void createZkNodes() {
		
	}
	
	
}
