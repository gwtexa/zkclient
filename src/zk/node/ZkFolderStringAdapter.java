package zk.node;

import java.util.HashMap;
import java.util.Map;


abstract public class ZkFolderStringAdapter implements ZkFolderListener {

		//childrenMap is znode key => znode value
		public void childrenPresent(String path, Map<String, byte[]> childrenMap) {
			Map<String, String> ss = new HashMap<String, String>();
			for (String key: childrenMap.keySet()) {
				ss.put(key, new String(childrenMap.get(key)));
			}
			childrenStringPresent(path, ss);
		}
		
		abstract public void childrenStringPresent(String path, Map<String, String> childrenMap);
}
