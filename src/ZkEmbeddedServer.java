import java.io.IOException;

import org.apache.zookeeper.server.DatadirCleanupManager;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;


public class ZkEmbeddedServer extends QuorumPeerMain {

	private final QuorumPeerConfig config;
	
	public ZkEmbeddedServer(String cfgFile) throws ConfigException {

        config = new QuorumPeerConfig();
        config.parse(cfgFile);


		
		
//		initializeAndRun(new String[] {cfgFile});
//		QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
//		try {
//			quorumConfig.parse(cfgFile);
//		} catch(Exception e) {
//		    throw new RuntimeException(e);
//		}
//		serverConfig = new ServerConfig();
//		serverConfig.readFrom(quorumConfig);
	}
	
	public void start() {
		try {
	        // Start and schedule the the purge task
	        DatadirCleanupManager purgeMgr = new DatadirCleanupManager(config
	                .getDataDir(), config.getDataLogDir(), config
	                .getSnapRetainCount(), config.getPurgeInterval());
	        purgeMgr.start();

	        runFromConfig(config);			
			
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}		
	
	public static void updateZooCfg() {
		//TODO dump /zoocfg znode into zoo.cfg file
		//include more static list of active vboxes in the config file
	}

	public void createZkNodes() {
		
	}

	public QuorumPeerConfig getConfig() {
		return config;
	}

	
	
	
}
