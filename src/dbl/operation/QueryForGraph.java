package dbl.operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import dbl.variable.Vars;


public class QueryForGraph {
	
	public static boolean hasRelationWith (GraphDatabaseService db, Node startNode, Node endNode){
		boolean hasRelation = false;

		try(Transaction tx = db.beginTx()){
			for(Relationship r:startNode.getRelationships(Direction.OUTGOING))
			{
				if(r.getEndNode().equals(endNode))
				{
					hasRelation = true;
					return true;
				}
			}
			tx.success();
			tx.close();
		}
		return hasRelation;
	}
	
		
	public static boolean hasRelationWith (GraphDatabaseService db, Node startNode, Node endNode, RelationshipType linkType){
		boolean hasRelation = false;
		try(Transaction tx = db.beginTx()){
			for(Relationship r:startNode.getRelationships(linkType, Direction.OUTGOING))
			{
				if(r.getEndNode().equals(endNode))
					{
						hasRelation = true;
						return true;
					}
			}
		}
		return hasRelation;
	}
	
	public static void deleteNodes(GraphDatabaseService db, String nodeType, int expectedTop){
		HashMap<Node, Integer> degreeMap = new HashMap<Node, Integer>();
		ArrayList<Node> typeNodes = getNodesWithType(db, nodeType);

		try(Transaction tx = db.beginTx())
		{
		   for(Node n:typeNodes)
		   {
	           if(!n.hasProperty(Vars.propertyNodeType))
	              continue;
	           degreeMap.put(n, n.getDegree(Direction.OUTGOING));
	       }
	       System.out.println(degreeMap.size());   
	    	
	       ArrayList<Node> it = (ArrayList<Node>) sortByValue(degreeMap);
	       for(int i = expectedTop; i<it.size(); i++){
	    	   Node node = it.get(i);
//	    	   System.out.println("node id: "+node.getId());	 
		       Iterator<Relationship> relIter = node.getRelationships().iterator();
		       while(relIter.hasNext()){
		    	   Relationship rel = relIter.next();
		    	   rel.delete();
//		    	   System.out.println("rel id: "+rel.getId());
		    	   
			   }
			   node.delete();
	       }
	     tx.success();  
		 tx.close(); 
	    }
	}
	
	public static String deleteEdge(GraphDatabaseService db, Node startNode, Node endNode){
		String relInfo = null;
		try(Transaction tx = db.beginTx()){
			Iterator<Relationship> relIter = startNode.getRelationships().iterator();
			while(relIter.hasNext()){
				Relationship rel = relIter.next();
				if(rel.getEndNode().equals(endNode)){
					relInfo = rel.getProperty("relationshipID").toString();
					rel.delete();
				}
			}
			
			Iterator<Relationship> relIterIngoing = endNode.getRelationships().iterator();
			while(relIterIngoing.hasNext()){
				Relationship rel = relIterIngoing.next();
				if(rel.getEndNode().equals(startNode)){
					relInfo = rel.getProperty("relationshipID").toString();
					rel.delete();
				}
			}
			
			tx.success();
			tx.close();
		}
		return relInfo;
	}
	
	public static ArrayList<Long> getRelationIds(GraphDatabaseService db, Node startNode, Node endNode, RelationshipType linkType){
		ArrayList<Long> relationIds = new ArrayList<Long>();
		try(Transaction tx = db.beginTx()){
			Iterable<Relationship> startRels = startNode.getRelationships(Direction.OUTGOING, linkType);
			Iterable<Relationship> endRels = endNode.getRelationships(Direction.OUTGOING, linkType);
			for(Relationship startRel:startRels){
				if(startRel.getEndNode().equals(endNode)){
					relationIds.add(startRel.getId());
				}
			}
			for(Relationship endRel:endRels){
				if(endRel.getEndNode().equals(startNode)){
					relationIds.add(endRel.getId());
				}
			}
		}
		return relationIds;
	}
	
	public static void insertEdge(GraphDatabaseService db, Node startNode, Node endNode, RelationshipType linkType, String property){
		try(Transaction tx = db.beginTx()){
			Relationship relationshipOutgoing = startNode.createRelationshipTo(endNode, linkType);
			relationshipOutgoing.setProperty("relationshipID", property);
			Relationship relationshipIngoing = endNode.createRelationshipTo(startNode, linkType);
			relationshipIngoing.setProperty("relationshipID", property);
			tx.success();
			tx.close();
		}
		
	}
	
	public static int getDegree(GraphDatabaseService db, Node node, RelationshipType linkType){
		int degree = 0;
		try(Transaction tx = db.beginTx()){
			degree = node.getDegree(linkType, Direction.OUTGOING);
		}
		return degree;
	}
	
	public static ArrayList<Node> getNodesWithType(GraphDatabaseService db, String nodeType)
	{
		ArrayList<Node> nodes = new ArrayList<>();
		
		try(Transaction tx = db.beginTx())
		{
			
			for(Node node :  GlobalGraphOperations.at(db).getAllNodes())
			{
				if(node.hasProperty(Vars.propertyNodeType)){
					if(node.getProperty(Vars.propertyNodeType).toString().equals(nodeType))
					{
						nodes.add(node);
					}
				}
			}
			tx.success();
		}
		
		return nodes;
	}
	
	public static ArrayList<Node> findNodeList (GraphDatabaseService db, String expectedNodeType,int expectedTop)
	{
		ArrayList<Node> typeNodes = getNodesWithType(db, expectedNodeType);
		ArrayList<Node> sampleNodes = new ArrayList<Node>();
		ArrayList<Integer> idx = new ArrayList<>();
		
		if(typeNodes.size()<expectedTop)
			expectedTop = typeNodes.size();
		
		Random rand = new Random();
		while(idx.size()<expectedTop){
			int n = rand.nextInt(typeNodes.size());
			if(idx.size()!=0 && idx.contains(n))
				continue;
			else
				idx.add(n);
		}
		for(int i=0; i<idx.size(); i++){
			sampleNodes.add(typeNodes.get(idx.get(i)));
		}
		System.out.println("Extracted sample nodes size: " + sampleNodes.size());
		return sampleNodes;
	}
	
	public static ArrayList<Node> findTopDegreeNodeListForType (GraphDatabaseService db, String expectedNodeType, int expectedTop)
   {
      HashMap<Node, Integer> degreeMap = new HashMap<Node, Integer>();
      ArrayList<Node> typeNodes = getNodesWithType(db, expectedNodeType);

      try(Transaction tx = db.beginTx())
      {
         for(Node n:typeNodes)
         {
            if(!n.hasProperty(Vars.propertyNodeType))
               continue;
            degreeMap.put(n, n.getDegree(Direction.OUTGOING));
         }
         System.out.println(degreeMap.size());   
      }

      ArrayList<Node> topDegreeNodes = new ArrayList<Node>();
      int idx = 0;
      try(Transaction tx = db.beginTx())
      {    
         Iterator it = sortByValue(degreeMap).iterator();
         for(int i=0; i<expectedTop; i++)
         {
            if(it.hasNext())
            {
               Node sortedNode = (Node) it.next();
               if(sortedNode.getDegree(Direction.OUTGOING)>1){
                  topDegreeNodes.add(sortedNode);
               }         
               idx = idx+1;
   
               //System.out.println(sortedNode + ":" + sortedNode.getDegree());
            }
         }
      }
      System.out.println("Extracted top degree nodes: " + topDegreeNodes.size());
      return topDegreeNodes;
   }
	

	public static ArrayList<Node> findTopDegreeNodeListForTypeWithLink (GraphDatabaseService db, String expectedNodeType, 
	         int expectedTop, RelationshipType relType)
	   {
	      HashMap<Node, Integer> degreeMap = new HashMap<Node, Integer>();
	      ArrayList<Node> typeNodes = getNodesWithType(db, expectedNodeType);

	      try(Transaction tx = db.beginTx())
	      {
	         for(Node n:typeNodes)
	         {
	            if(!n.hasProperty(Vars.propertyNodeType))
	               continue;
	            degreeMap.put(n, n.getDegree(relType,Direction.OUTGOING));
	         }
	         System.out.println(degreeMap.size()); 
	      }

	      ArrayList<Node> topDegreeNodes = new ArrayList<>();
	      int idx = 0;
	      
	      try(Transaction tx = db.beginTx())
	      {    
	         Iterator it = sortByValue(degreeMap).iterator();
	         for(int i=0; i<expectedTop; i++)
	         {
	            if(it.hasNext())
	            {
	               Node sortedNode = (Node) it.next();
	               if(sortedNode.getDegree(relType,Direction.OUTGOING)>1){
	                  topDegreeNodes.add(sortedNode);
	               }      
	               idx = idx+1;
	   
	               //System.out.println(sortedNode + ":" + sortedNode.getDegree());
	            }
	         }
	      }
	      System.out.println("Extracted top degree nodes: " + topDegreeNodes.size());
	      return topDegreeNodes;
	   }
	
	public static List sortByValue(Map unsortedMap)
	{
		List<String> list = new ArrayList();
		list.addAll(unsortedMap.keySet());

//		System.out.println("Original nodes: "+list.size());
		Collections.sort(list,new Comparator(){
			public int compare(Object o1,Object o2){
				Object v1 = unsortedMap.get(o1);
				Object v2 = unsortedMap.get(o2);

				return ((Comparable) v1).compareTo(v2);
			}
		});

		Collections.reverse(list); // If you erase '//', then Asc
		return list;
	}
	
	public static int findGeneFamilyNodes(GraphDatabaseService db, String nodeType)
   {
//      ArrayList<Node> nodes = new ArrayList<>();
      int cnt = 0;
      try(Transaction tx = db.beginTx())
      {
         
         for(Node node :  GlobalGraphOperations.at(db).getAllNodes())
         {
            if(node.hasProperty(Vars.propertyNodeType)){
               if(node.getProperty(Vars.propertyNodeType).toString().equals(nodeType))
               {
                  if(node.getProperty("nodeID").toString().contains("http://chem2bio2rdf.org/hgnc/resource/gene_family")){
                     node.setProperty(Vars.propertyNodeType, "Gene Family");
                     cnt++;
                  }
               }
            }
         }
         tx.success();
      }
      
      return cnt;
   }
}
