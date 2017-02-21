package dbl.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Traverser;

import dbl.traverse.TraversalDesc;
import dbl.variable.Vars;

public class SymmetricClustering {
	
	public Map<Integer,Set<Node>> getSymmetricClusterRelParam (GraphDatabaseService db, Node targetNode, Node linkNode, 
			ArrayList<ArrayList<RelationshipType>> symmetricMetaPaths, RelationshipType linkType){
		Map<Node, Integer> cluster = new HashMap<Node, Integer>();
		ArrayList<Long> relIds = QueryForGraph.getRelationIds(db, targetNode, linkNode, linkType);
		
		try(Transaction tx = db.beginTx()) {
			TraversalDesc traversalDesc = new TraversalDesc();
			
			for(ArrayList<RelationshipType> metaPath:symmetricMetaPaths){
				Traverser traverser = traversalDesc.getTestTraversalDescription(db, targetNode, metaPath, relIds);
				for(Path path:traverser){
					if(cluster.containsKey(path.endNode())){
						cluster.put(path.endNode(), cluster.get(path.endNode())+1);
					}
					else{
						cluster.put(path.endNode(), 1);
					}
				}
			}
			tx.success();
			tx.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Integer, Set<Node>> finalClusterList = new HashMap<>();
		for(int i=1; i<=3; i++){
			Set<Node> finalCluster = new HashSet<Node>();
			for(Node candidate:cluster.keySet()){
				if(candidate.getId()!=targetNode.getId()){
					if(cluster.containsKey(candidate) && cluster.get(candidate)>=i){
						finalCluster.add(candidate);
					}
				}
			}
			finalClusterList.put(i, finalCluster);
		}
		
		return finalClusterList;
	}
}
