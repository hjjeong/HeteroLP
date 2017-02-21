package dbl.database;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import dbl.variable.Vars;

public class DBManager {

	private static GraphDatabaseService db;
	private static Thread hook = null;

	public static GraphDatabaseService getDatabase() 
	{
		return db;
	}

	public static GraphDatabaseService openDB(String dbPath) 
	{
		/**
		 * @return if current db is null, then create GraphDatabaseService
		 * 			else return current db 
		 */
		if(db == null)
		{
			db = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
			
//			registerShutdownHook();
		}
		return db;
	}

	private static void registerShutdownHook() 
	{
		Runtime.getRuntime().addShutdownHook(hook = new Thread() {
			@Override
			public void run() {
				db.shutdown();
			}
		});
	}

	private static void removeShutdownHook() 
	{
		Runtime.getRuntime().removeShutdownHook(hook);
		hook = null;
	}
	
	public static void removeDB(){
//		removeShutdownHook();
		db.shutdown();
		db = null;
	}
	public static void closeDB() 
	{
//		removeShutdownHook();
		db.shutdown();
		db = null;
	}
}
