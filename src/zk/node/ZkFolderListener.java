package zk.node;

public interface ZkFolderListener {

	public void childPresent(String path, byte[] data);
	
	public void childDeleted(String path);
}
