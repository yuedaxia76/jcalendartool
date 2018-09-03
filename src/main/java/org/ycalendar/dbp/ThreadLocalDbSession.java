package org.ycalendar.dbp;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
public class ThreadLocalDbSession {



	/**
	 *  
	 */
	private static final ThreadLocal<Map<Object, ConWrapper>> resources = new ThreadLocal<Map<Object, ConWrapper>>() {
		protected Map<Object, ConWrapper> initialValue() {
			return new HashMap<Object, ConWrapper>();
		}
	};

	public static Map<Object, ConWrapper> getResourceMap() {
		return resources.get();
	}

	public static boolean hasResource(Object key) {
		return getResourceMap().containsKey(key);
	}

	public static void bindResource(Object key, Connection value) {
		if (hasResource(key)) {
			throw new IllegalStateException("Already a value for key [" + key + "] bound to thread");
		}
		getResourceMap().put(key, new ConWrapper(value));

	}

	public static Connection currentSession(Object key) {
		ConWrapper con = getResourceMap().get(key);
		if (con == null) {

			throw new IllegalStateException("No value for key  [" + key + "] bound to thread");

		}

		return con.con;

	}

	public static Connection closeSession(Object key) {

		if (!hasResource(key)) {
			throw new IllegalStateException("No value for key [" + key + "] bound to thread");
		}
		return getResourceMap().remove(key).con;
	}

	public static class ConWrapper {
		public final Connection con;
		public int count = 0;
		public boolean rollbackOnly=false;

		public ConWrapper(Connection con) {
			this.con = con;
		}
	}


}
