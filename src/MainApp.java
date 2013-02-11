
public class MainApp {

	public static final boolean ZK_SERVER_ENABLED = true;
	public static final String ZOO_CFG = "zoo.cfg";
	
	
	public static void main(String[] args) {
		if (ZK_SERVER_ENABLED) {
			ZkEmbeddedServer server = new ZkEmbeddedServer(ZOO_CFG);
			server.start();
			try { Thread.sleep(1000000000); } catch(Exception e) {e.printStackTrace();}

		}
	}
	
}
