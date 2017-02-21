package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Traverser;

import dbl.operation.QueryForGraph;
import dbl.operation.SymmetricClustering;
import dbl.traverse.TraversalDesc;
import dbl.variable.Vars;


public class FeatureVectorConstructor {
	
	public Map<Long, long[]> getFeatureVectorForLRMWithColleague(GraphDatabaseService graphdb, Node startNode, ArrayList<Node> endNodeList, 
			ArrayList<ArrayList<RelationshipType>> metaPathList, 
			ArrayList<ArrayList<RelationshipType>> symmetricStartMetaPaths, 
			ArrayList<ArrayList<RelationshipType>> symmetricEndMetaPaths,
			boolean isTest, int n){

		Map<Long, long[]> featureVector = new HashMap<>(); // EndNode + # of MetaPath + start level 0~3+end level 0~3 +label
		TraversalDesc traversalDesc = new TraversalDesc();
		for(Node endNode:endNodeList){
			long[] feature = new long[metaPathList.size()+9];
			featureVector.put(endNode.getId(), feature);
		}
			
		try(Transaction tx = graphdb.beginTx()){
			for(int i=0; i<metaPathList.size(); i++){
				for(Node endNode: endNodeList){
					ArrayList<Long> relIds = QueryForGraph.getRelationIds(graphdb, startNode, endNode, Vars.linkTypes[n]);
					Traverser traverser = traversalDesc.getTestPathCountDescription(graphdb, startNode, metaPathList.get(i), relIds, endNode);
					for(Path path:traverser)
					{
						Node pathendNode = path.endNode();
						if(pathendNode.equals(endNode)){
							long[] feature = featureVector.get(endNode.getId());
							feature[i] = feature[i]+1;
							featureVector.put(endNode.getId(), feature);
						}
					}
				}
			}
		}
		
		SymmetricClustering symmetriClustering = new SymmetricClustering();

		for(Node endNode:endNodeList){
			Map<Integer, Set<Node>> startClusterList = symmetriClustering.getSymmetricClusterRelParam(graphdb, startNode, endNode, symmetricStartMetaPaths, Vars.linkTypes[n]);
			Map<Integer, Set<Node>> endClusterList = symmetriClustering.getSymmetricClusterRelParam(graphdb, endNode, startNode, symmetricEndMetaPaths, Vars.linkTypes[n]);
			
			
			long[] feature = featureVector.get(endNode.getId());
			
			//startNode level 0 colleague (neighbor of target)
			
			if(QueryForGraph.hasRelationWith(graphdb, startNode, endNode, Vars.linkTypes[n])){
				feature[feature.length-9] = QueryForGraph.getDegree(graphdb, endNode, Vars.linkTypes[n])-1;
			}
			else{
				feature[feature.length-9] = QueryForGraph.getDegree(graphdb, endNode, Vars.linkTypes[n]);
			}
		
			//startNode level1~3 colleague
			for(int i=1; i<=3; i++){
				long startCommonNeighborCnt = 0;
				for(Node startNeighbor:startClusterList.get(i)){
					if(QueryForGraph.hasRelationWith(graphdb, startNeighbor, endNode)){
						startCommonNeighborCnt++;
					}
				}
				feature[feature.length-9+i] = startCommonNeighborCnt;
			}
			
			//endNode level 0 colleague (neighbor of source)
			if(QueryForGraph.hasRelationWith(graphdb, startNode, endNode, Vars.linkTypes[n])){
				feature[feature.length-5] = QueryForGraph.getDegree(graphdb, startNode, Vars.linkTypes[n])-1;
			}
			else{
				feature[feature.length-5] = QueryForGraph.getDegree(graphdb, startNode, Vars.linkTypes[n]);
			}
			
			//endNode level 1~3 colleagues
			for(int j=1; j<=3; j++){
				long endCommonNeighborCnt = 0;
				for(Node endNeighbor:endClusterList.get(j)){
					if(QueryForGraph.hasRelationWith(graphdb, startNode, endNeighbor)){
						endCommonNeighborCnt++;
					}
				}
				feature[feature.length-5+j] = endCommonNeighborCnt;
			}
						
			if(QueryForGraph.hasRelationWith(graphdb, startNode, endNode, Vars.linkTypes[n])){

				feature[feature.length-1] = 1;
			}
			else{
				feature[feature.length-1] = 0;
			}

			featureVector.put(endNode.getId(), feature);				
		}
		
		return featureVector;
	}
}
