package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Traverser;

import dbl.operation.QueryForGraph;
import dbl.traverse.TraversalDesc;
import dbl.variable.MetaPathInfo;
import dbl.variable.Vars;

public class TypeCorrValCalculator {

	long numOfCycle = 0;
	long numOfOnlyLinks = 0;
	long numOfOnlyPaths = 0;
	long numOfLinks = 0;
	
	boolean isTest = false;
	
	String measurement = "";
	String startNodeType = "";
	String endNodeType = "";
	String linkType = "";
	
	ArrayList<RelationshipType>  metaPath = null;
	ArrayList<Node> startNodes = null;
	

	public TypeCorrValCalculator(ArrayList<RelationshipType> metaPath, String startNodeType, String endNodeType, String linkType, String measurement) 
	{
		this.metaPath = metaPath;	
		this.startNodeType = startNodeType;
		this.endNodeType = endNodeType;
		this.linkType = linkType;
		this.measurement = measurement;
	}
	
	public MetaPathInfo calculateTypeCorrVal(GraphDatabaseService db)
	{
		startNodes = QueryForGraph.getNodesWithType(db, startNodeType);		
		Iterator<Node> iter = startNodes.iterator();
		HashMap<Long, HashSet<Long>> linksOnCycle = new HashMap<>();
		double typeCorVal = 0.0;

		TraversalDesc td = new TraversalDesc();
		MetaPathInfo metaPathObj = new MetaPathInfo(metaPath, startNodeType, endNodeType, -1.0);
		
		try(Transaction tx = db.beginTx())
		{
			while(iter.hasNext())
			{
				Node startNode = iter.next();
				HashSet<Long> endIDs = getLinks(db, startNode, endNodeType, linkType);
				HashSet<Long> endIDsOnCycle = new HashSet<>();
				
				Traverser tr = td.getTraversalDescription(db, startNode, metaPathObj.getMetaPath(), isTest);

				if(endIDs != null)
				{
					numOfLinks += endIDs.size();
					
					for(Path path : tr)
					{
						Node endNode = path.endNode();

						if(endIDs.contains(endNode.getId()))
						{
//							its metapath is a part of cycle
							numOfCycle++;
							endIDsOnCycle.add(endNode.getId());
						}else
						{
//							its metapath is a kind of only meta path(not a part of cycle)
							numOfOnlyPaths++;					
						}
					}
					
					if(endIDsOnCycle.size() > 0)
						linksOnCycle.put(startNode.getId(), endIDsOnCycle);
				}else
				{
					for(Path path : tr)
					{
						numOfOnlyPaths++;
					}
				}
			}
		}
		
		long numOfLinksOnCycle = 0;
		
//		calculate the number of only links
		for(HashSet<Long> endIDsOnCycle : linksOnCycle.values())
		{
			numOfLinksOnCycle += endIDsOnCycle.size();
		}
		
//		calculate the type correlation value
		
		numOfOnlyLinks = numOfLinks - numOfLinksOnCycle;
		if(measurement.equals(Vars.measurement_sorensen))
		{
			long denominator = numOfOnlyLinks + numOfOnlyPaths + 2*numOfCycle;
			long numerator = 2*numOfCycle;
			typeCorVal = (double) numerator/denominator;
		}else if(measurement.equals(Vars.measurement_jaccard))
		{
			long denominator = numOfOnlyLinks + numOfOnlyPaths + numOfCycle;
			long numerator = numOfCycle;
			typeCorVal = (double) numerator/denominator;
		}

		metaPathObj.setNumOfCycle(numOfCycle);
		metaPathObj.setNumOfOnlyMetaPaths(numOfOnlyPaths);
		metaPathObj.setNumOfOnlyLinks(numOfOnlyLinks);
		metaPathObj.setNumOfMetaPaths(numOfOnlyPaths + numOfCycle);
		metaPathObj.setNumOfLinks(numOfLinks);
		metaPathObj.setTypeCorrValue(typeCorVal);
		
		return metaPathObj;
	}

	public HashSet<Long> getLinks(GraphDatabaseService db, Node startNode, String endNodeType, String linktype) 
	{
		HashSet<Long> endIDs = new HashSet<>();


		try(Transaction tx = db.beginTx())
		{
			for(Relationship rel : startNode.getRelationships(Direction.OUTGOING))
			{
				Node endNode = rel.getEndNode();
				if(rel.getType().name().equals(linktype) && endNode.getProperty(Vars.propertyNodeType).toString().equals(endNodeType))
				{
					endIDs.add(endNode.getId());
				}
			}
			tx.success();
		}

		if(endIDs.size() > 0)
			return endIDs;
		else
			return null;
	}
	
	public HashMap<Long, HashSet<Long>> getLinks(GraphDatabaseService db, String endNodeType) 
	{
		Iterator<Node> iter = startNodes.iterator();
		HashMap<Long, HashSet<Long>> links = new HashMap<>();
		;

		try(Transaction tx = db.beginTx())
		{
			while(iter.hasNext())
			{
				Node startNode = iter.next();

				HashSet<Long> endIDs = new HashSet<>();

				for(Relationship rel : startNode.getRelationships(Direction.OUTGOING))
				{
					Node endNode = rel.getEndNode();
					if(endNode.getProperty("nodetype").toString().equals(endNodeType))
					{
						endIDs.add(endNode.getId());
					}
				}
			}
			tx.success();
		}

		return links;
	}
}
