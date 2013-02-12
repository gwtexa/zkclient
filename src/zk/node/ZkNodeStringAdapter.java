package zk.node;

abstract public class ZkNodeStringAdapter implements ZkNodeListener {

	public void nodePresent(String path, byte[] b) {
		nodeStringPresent(path, new String(b));
	}
	
	abstract public void nodeStringPresent(String path, String s);
}

