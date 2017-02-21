package dbl.database;

import java.util.*;
import java.util.Iterator;

import dbl.traverse.TraversalDesc;
import dbl.variable.MetaPathInfo;
import dbl.variable.RelationManager;
import dbl.variable.Vars;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;


public class SchemaDBManager {

	private GraphDatabaseService schemaDB;
	private Thread hook = null;
	private boolean dbExist = false;

	public SchemaDBManager()
	{
		System.out.println("Open Schema DB");
		openSchemaDB();

		if(!dbExist)
			try(Transaction tx = schemaDB.beginTx())
			{
				System.out.println("Create Schema DB");
				createSchema();
				tx.success();
			}			
	}

	public GraphDatabaseService getSchemaDB() 
	{
		return schemaDB;
	}

	public void openSchemaDB() 
	{
		/**
		 * @return if current db is null, then create GraphDatabaseService
		 * 			else return current db 
		 */
		if(schemaDB == null)
		{
			schemaDB = new GraphDatabaseFactory().newEmbeddedDatabase(Vars.schemaDBPath);
//			if(GlobalGraphOperations.at(schemaDB) != null)
//			{
//				dbExist = true;
//				System.out.println("Shcema DB exists");
//			}
//			else
//			{
//				System.out.println("Shcema DB does not exist");
//				dbExist = false;
//			}
			dbExist = false;
			registerShutdownHook();
		}
	}

	private void registerShutdownHook() 
	{
		Runtime.getRuntime().addShutdownHook(hook = new Thread() {
			@Override
			public void run() {
				schemaDB.shutdown();
			}
		});
	}

	private void removeShutdownHook()
	{
		Runtime.getRuntime().removeShutdownHook(hook);
		hook = null;
	}
	
	public void closeSchemaDB()
	{
		removeShutdownHook();
		schemaDB.shutdown();
		schemaDB = null;
	}

	public ArrayList<ArrayList<RelationshipType>> getMetaPathFromStartEndNodeType(String startNodeType, String endNodeType) throws Exception
	{
		/****
		 * @params startNodeType, endNodeType
		 * @return list of metaPaths which start with startNodeType and end with endNodeType
		 *         typecorrvalue of each metapath is initialized with -1.0 
		 */
		TraversalDescription td = null;
		Node startNode = null;		
		ArrayList<ArrayList<RelationshipType>> metaPaths = new ArrayList<>();
		
		try(Transaction tx = schemaDB.beginTx())
		{
			for(Node tmpNode : GlobalGraphOperations.at(schemaDB).getAllNodes())
			{
				System.out.println(tmpNode.getProperty(Vars.propertyNodeType));
				if((tmpNode.getProperty(Vars.propertyNodeType)).equals(startNodeType))
				{
					startNode = tmpNode;
				}

				if(startNode != null)
					break;
			}

			if(startNode == null)
			{
				throw new Exception(startNodeType + " there is no such node which has startNodeType");
			}

			td = (new TraversalDesc()).getTraversalDescriptionForSchemaDB(schemaDB);
			Traverser traverser =  td.traverse(startNode);

			for(Path path : traverser)
			{
				if(path.endNode().getProperty(Vars.propertyNodeType).equals(endNodeType))
				{
					ArrayList<RelationshipType> metapath = new ArrayList<RelationshipType>();

					for(Relationship rel : path.relationships())
					{
						metapath.add((RelationshipType)rel.getType());
					}
					if(!metaPaths.contains(metapath))
					{
						metaPaths.add(metapath);
//						System.out.println(metapath.toString());
					}						
				}			
			}
			tx.success();
		}


		return metaPaths;
	}
	
	public ArrayList<ArrayList<RelationshipType>> getMetaPathFromStartEndNodeType(String startNodeType, String endNodeType, int length) throws Exception
	{
		/****
		 * @params startNodeType, endNodeType
		 * @return list of metaPaths which start with startNodeType and end with endNodeType
		 *         typecorrvalue of each metapath is initialized with -1.0 
		 */
		TraversalDescription td = null;
		Node startNode = null;		
		ArrayList<ArrayList<RelationshipType>> metaPaths = new ArrayList<>();
		
		try(Transaction tx = schemaDB.beginTx())
		{
			for(Node tmpNode : GlobalGraphOperations.at(schemaDB).getAllNodes())
			{
				System.out.println(tmpNode.getProperty(Vars.propertyNodeType));
				if((tmpNode.getProperty(Vars.propertyNodeType)).equals(startNodeType))
				{
					startNode = tmpNode;
				}

				if(startNode != null)
					break;
			}

			if(startNode == null)
			{
				throw new Exception(startNodeType + " there is no such node which has startNodeType");
			}

			td = (new TraversalDesc()).getTraversalDescriptionForSymmetricSchemaDB(schemaDB, length);
			Traverser traverser =  td.traverse(startNode);

			for(Path path : traverser)
			{
				if(path.endNode().getProperty(Vars.propertyNodeType).equals(endNodeType))
				{
					ArrayList<RelationshipType> metapath = new ArrayList<RelationshipType>();

					for(Relationship rel : path.relationships())
					{
						metapath.add((RelationshipType)rel.getType());
					}
					if(!metaPaths.contains(metapath))
					{
						metaPaths.add(metapath);
//						System.out.println(metapath.toString());
					}						
				}			
			}
			tx.success();
		}


		return metaPaths;
	}
	
	public ArrayList<ArrayList<RelationshipType>> getSymmetricMetaPathFromStartNodeType(String startNodeType) throws Exception
	{
//		TraversalDescription td = (new TraversalDesc()).getTraversalDescriptionForSymmetricSchemaDB(schemaDB, startNodeType);
		TraversalDescription td = (new TraversalDesc()).getTraversalDescriptionForSchemaDB(schemaDB);
		Node startNode = null;		
		ArrayList<ArrayList<RelationshipType>> metaPaths = new ArrayList<>();
		
		try(Transaction tx = schemaDB.beginTx())
		{
			for(Node tmpNode : GlobalGraphOperations.at(schemaDB).getAllNodes())
			{
				if(tmpNode.getProperty(Vars.propertyNodeType).toString().equals(startNodeType))
				{
					startNode = tmpNode;
					break;
				}
			}
			
			if(startNode == null)
			{
				throw new Exception("there is no such node which has startNodeType");
			}
			
			Traverser traverser =  td.traverse(startNode);
			
			for(Path path : traverser)
			{
				ArrayList<RelationshipType> metapath = new ArrayList<RelationshipType>();
				
				if(path.endNode().getProperty(Vars.propertyNodeType).equals(startNodeType)){
					for(Relationship rel : path.relationships())
					{
						metapath.add((RelationshipType) rel.getType());
						System.out.print("["+rel.getType()+"]-");
					}
					System.out.println();
					metaPaths.add(metapath);
				}
			}
			tx.success();
		}
		return metaPaths;
	}
	
	public ArrayList<MetaPathInfo> getMetaPathFromStartNodeType(String startNodeType) throws Exception
	{
		TraversalDescription td = (new TraversalDesc()).getTraversalDescriptionForSchemaDB(schemaDB);
		Node startNode = null;		
		ArrayList<MetaPathInfo> metaPaths = new ArrayList<>();
		
		for(Node tmpNode : GlobalGraphOperations.at(schemaDB).getAllNodes())
		{
			if(tmpNode.getProperty(Vars.propertyNodeType).toString().equals(startNodeType))
			{
				startNode = tmpNode;
				break;
			}
		}
		
		if(startNode == null)
		{
			throw new Exception("there is no such node which has startNodeType");
		}
		
		Traverser traverser =  td.traverse(startNode);
		
		for(Path path : traverser)
		{
			ArrayList<RelationshipType> metapath = new ArrayList<RelationshipType>();
			
			for(Relationship rel : path.relationships())
			{
				metapath.add((RelationshipType) rel.getType());
			}
			metaPaths.add(new MetaPathInfo(metapath, startNodeType, path.endNode().getProperty(Vars.propertyNodeType).toString(), -1.0));
		}
		return metaPaths;
	}
	
	public void createSchema() 
	{
		 RelationManager.RelTypes[] relTypes = RelationManager.RelTypes.values();
		 ArrayList<String> nodeNames = RelationManager.getNodeTypes();
		 
//		 Add all the nodes in RelationManager class
		 Iterator<String> iterNodeNames = nodeNames.iterator();

		 while(iterNodeNames.hasNext()) 
		 {		 
			 String name;
			 name = iterNodeNames.next();
			 
//			 Make a node 4 times (4 = maximum meta path length)
			 for(int i = 0; i < Vars.max_path_length; i++)
			 {
				 Node node = this.schemaDB.createNode();
				 
				 node.setProperty(Vars.propertyNodeType, name); 
			 }
		 }
		 
//		 Create links 

		 for(int i = 0; i < relTypes.length; i++)
		 {
			 String[] nodeTypes = RelationManager.getNodeTypesWithRelType(relTypes[i]);

			 if(!nodeTypes[0].equals(nodeTypes[1]))
			 {
				 Iterator<Node> iterStartNodes = getNodeWithType(nodeTypes[0]).iterator();
				 
				 while(iterStartNodes.hasNext())
				 {
					 Node startNode = iterStartNodes.next();
					 Iterator<Node> iterEndNodes = getNodeWithType(nodeTypes[1]).iterator();
					 
					 while(iterEndNodes.hasNext())
					 {
						 Node endNode = iterEndNodes.next();
						 startNode.createRelationshipTo(endNode, relTypes[i]);
						 endNode.createRelationshipTo(startNode, relTypes[i]);
					 }
				 }
				 
			 } else // when start's type is equal to end's 
			 {
				 ArrayList<Node> nodes = getNodeWithType(nodeTypes[0]);
				
				 for(int m = 0; m < nodes.size(); m++)
				 {
					 Node startNode = nodes.get(m);
					 for(int n = m; n < nodes.size(); n++)
					 {
						 if(m != n) // only when startNode is not equal to endNode
						 {
							 Node endNode = nodes.get(n);
							 
							 startNode.createRelationshipTo(endNode, relTypes[i]);
							 endNode.createRelationshipTo(startNode, relTypes[i]);
						 }
					 }
				 }
			 }
		 }		 
	}
	
	private ArrayList<Node> getNodeWithType(String nodeType)
	{
		ArrayList<Node> nodes = new ArrayList<>();
		
		 for(Node node : GlobalGraphOperations.at(this.schemaDB).getAllNodes())
		 {
			 if(node.getProperty(Vars.propertyNodeType).equals(nodeType))
			 {
				 nodes.add(node);
			 }
		 }
		 return nodes;
	}
}
