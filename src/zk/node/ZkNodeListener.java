package zk.node;

public interface ZkNodeListener {

	public void nodePresent(byte[] b);
	
	public void nodeDeleted();
	
	/**
	 * Is this method needed? 
	 */
	//public void nodeDataChanged(byte[] b);
}
