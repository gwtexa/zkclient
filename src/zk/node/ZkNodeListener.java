package zk.node;

public interface ZkNodeListener {

	/**
	 * 
	 * Called when node first time fetched (on connect), created or updated
	 *  
	 * @param path - ZkNode path
	 * @param b
	 */
	public void nodePresent(String path, byte[] b);
	
	
	/**
	 * @param path - ZkNode path
	 */
	public void nodeDeleted(String path);
	
	/**
	 * Is this method needed? 
	 */
	//public void nodeDataChanged(String path, byte[] b);
}
