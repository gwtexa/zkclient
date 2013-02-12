package zk.node;

import java.util.Map;

public interface ZkFolderListener {

	/**
	 * 
	 * Called when node first time fetched (on connect) or when children are created. 
	 * Note: It is not called when children are updated!
	 * 
	 * @param path - ZkFolder path
	 * @param childrenMap is znode key => znode value
	 */
	public void childrenPresent(String path, Map<String, byte[]> childrenMap);
	
	/**
	 * 
	 * @param path - ZkFolder path
	 * @param children
	 */
	public void childrenDeleted(String path, String[] children);
	
}
