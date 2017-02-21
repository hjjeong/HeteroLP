package dbl.traverse;

import java.util.ArrayList;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.*;

import dbl.variable.*;

public class TraversalDescKorea {
	public static int cnt=0;
	
	public final Traverser getTraversalDescription(GraphDatabaseService graphdb, Node startNode, ArrayList<RelationshipType> metaPath, boolean isTest){
		
		Evaluator evaluator;
		TraversalDescription td;
		if(isTest){
			evaluator = new TestEvaluator(metaPath);
		}
		else{
			evaluator = new TestEvaluator(metaPath);
		}
		
		td = graphdb.traversalDescription()
				.evaluator(evaluator)
				.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
				.depthFirst()
				.uniqueness(Uniqueness.NONE);
		

		Traverser traverser = td.traverse(startNode);
		return traverser;
	}
	
	public final Traverser getTestTraversalDescription(GraphDatabaseService graphdb, Node startNode, ArrayList<RelationshipType> metaPath, ArrayList<Long> relIds){
		
		Evaluator evaluator;
		TraversalDescription td;
		evaluator = new LinkEliminateEvaluator(metaPath, relIds);
		
		td = graphdb.traversalDescription()
				.evaluator(evaluator)
				.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
//				.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
				.depthFirst()
				.uniqueness(Uniqueness.NONE);
			
		Traverser traverser = td.traverse(startNode);
		return traverser;
	}

	public final Traverser getBaseTraversalDescription(GraphDatabaseService graphdb, Node startNode, ArrayList<RelationshipType> metaPath, boolean isTest){
		
		Evaluator evaluator;
		TraversalDescription td;
		evaluator = new BaseEvaluator(metaPath);
		
		td = graphdb.traversalDescription()
				.evaluator(evaluator)
				.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
				.depthFirst()
				.uniqueness(Uniqueness.NONE);
			
		Traverser traverser = td.traverse(startNode);
		return traverser;
	}

	public final TraversalDescription getTraversalDescriptionForSchemaDB(GraphDatabaseService graphdb){

		Evaluator evaluator = new SchemaMetaPathEvaluator();
		TraversalDescription td = null;
		try(Transaction tx = graphdb.beginTx()){
			td = graphdb.traversalDescription()
					.evaluator(evaluator)
					.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
					.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
					.depthFirst()
					.uniqueness(Uniqueness.NONE);
			
			tx.success();
		}
		return td;
	}
	
	public final TraversalDescription getTraversalDescriptionForSymmetricSchemaDB(GraphDatabaseService graphdb, int length){

		Evaluator evaluator = new SymmetricEvaluator(length);
		TraversalDescription td = null;
		try(Transaction tx = graphdb.beginTx()){
			td = graphdb.traversalDescription()
					.evaluator(evaluator)
					.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
					.depthFirst()
					.uniqueness(Uniqueness.NONE);
			
			tx.success();
		}
		return td;
	}
	
	public final Traverser getTestPathCountDescription(GraphDatabaseService graphdb, Node startNode, ArrayList<RelationshipType> metaPath, ArrayList<Long> relIds, Node endNode){
		
		Evaluator evaluator;
		TraversalDescription td;
		evaluator = new LinkEliminatePCEvaluator(metaPath, relIds, endNode);
		
		td = graphdb.traversalDescription()
				.evaluator(evaluator)
				.relationships(RelationManagerKorea.RelTypes.DI_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.DR_SE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GE_PR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.GO_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_GE, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.PW_PW, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DI, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_DR, Direction.OUTGOING)
				.relationships(RelationManagerKorea.RelTypes.TG_PW, Direction.OUTGOING)
				.depthFirst()
				.uniqueness(Uniqueness.NONE);
			
		Traverser traverser = td.traverse(startNode);
		return traverser;
	}
	
	public final class SymmetricEvaluator implements Evaluator{
		int length;
		
		public SymmetricEvaluator(int length) {
			this.length = length;
		}
		
		@Override
		public Evaluation evaluate(Path path) {

			if(path.length() == 0)
			{
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}else if(path.length() < length)
			{
				return Evaluation.INCLUDE_AND_CONTINUE;
			}else if(path.length() == length)
			{
				return Evaluation.INCLUDE_AND_PRUNE;
			}else
			{
				return Evaluation.EXCLUDE_AND_PRUNE;
			}								
			
		}
		
	}
	
	public final class BaseEvaluator implements Evaluator{
		
		private ArrayList<RelationshipType> metaPath;
		
		public BaseEvaluator(ArrayList<RelationshipType> metaPath){
			this.metaPath = metaPath;
		}
		
		@Override
		public Evaluation evaluate(Path path) {
			// TODO Auto-generated method stub
			if(path.length()==0){
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			RelationshipType expectedType = metaPath.get(path.length()-1);
			boolean isExpectedType = path.lastRelationship().isType(expectedType);
			boolean included = path.length() == metaPath.size() && isExpectedType;
			boolean continued = path.length() < metaPath.size() && isExpectedType;
			return Evaluation.of(included, continued);
		}
		
	}
	
	public final class LinkEliminateEvaluator implements Evaluator{
		
		private ArrayList<RelationshipType> metaPath;
		private ArrayList<Long> relIds;
		
		public LinkEliminateEvaluator(ArrayList<RelationshipType> metaPath, ArrayList<Long> relIds){
			this.metaPath = metaPath;
			this.relIds = relIds;
		}
		
		@Override
		public Evaluation evaluate(Path path) {
			// TODO Auto-generated method stub
			if(path.length()==0){
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			
			boolean isTestRelationship = false;
			for(Long relId:relIds){
				if(path.lastRelationship().getId() == relId){
					isTestRelationship = true;
				}
			}
			RelationshipType expectedType = metaPath.get(path.length()-1);
			boolean isExpectedType = path.lastRelationship().isType(expectedType);
			boolean included = path.length() == metaPath.size() && isExpectedType && !isTestRelationship;
			boolean continued = path.length() < metaPath.size() && isExpectedType && !isTestRelationship;
			
			return Evaluation.of(included, continued);
		}
	}
	
	public final class LinkEliminatePCEvaluator implements Evaluator{
		
		private ArrayList<RelationshipType> metaPath;
		private ArrayList<Long> relIds;
		private Node endNode;
		
		public LinkEliminatePCEvaluator(ArrayList<RelationshipType> metaPath, ArrayList<Long> relIds, Node endNode){
			this.metaPath = metaPath;
			this.relIds = relIds;
			this.endNode = endNode;
		}
		
		@Override
		public Evaluation evaluate(Path path) {
			// TODO Auto-generated method stub
			if(path.length()==0){
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			
			boolean isTestRelationship = false;
			for(Long relId:relIds){
				if(path.lastRelationship().getId() == relId){
					isTestRelationship = true;
				}
			}
			RelationshipType expectedType = metaPath.get(path.length()-1);
			boolean isExpectedType = path.lastRelationship().isType(expectedType);
			
			boolean included = path.length() == metaPath.size() && isExpectedType && !isTestRelationship;
			if(included){
				boolean isEqualEndNode = path.endNode().equals(endNode);
				if(!isEqualEndNode){
					included = false;
				}
				
			}
			boolean continued = path.length() < metaPath.size() && isExpectedType && !isTestRelationship;
			
			return Evaluation.of(included, continued);
		}
	}

	public final class TestEvaluator implements Evaluator{
		
		private ArrayList<RelationshipType> metaPath;
		
		public TestEvaluator(ArrayList<RelationshipType> metaPath){
			this.metaPath = metaPath;
		}
		
		@Override
		public Evaluation evaluate(Path path) {
			// TODO Auto-generated method stub
			if(path.length()==0){
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			ArrayList<Long> pathNodes = new ArrayList<Long>();
			Iterator<Node> nodeItr = path.nodes().iterator();
			int idx = 0;
			while(nodeItr.hasNext()){
				pathNodes.add(nodeItr.next().getId());
				idx = idx+1;
			}
			
			RelationshipType expectedType = metaPath.get(path.length()-1);
			boolean isExpectedType = path.lastRelationship().isType(expectedType);
			boolean included = path.length() == metaPath.size() && isExpectedType;
			if(included){
				for(int i=0; i<pathNodes.size(); i++){
					if(pathNodes.get(i).equals(path.startNode().getId())){
						if(i>0 && pathNodes.get(i-1).equals(path.endNode().getId())){
							included = false;
							cnt++;
						}
						if(i<pathNodes.size()-1 && pathNodes.get(i+1).equals(path.endNode().getId())){
							included=false;
							cnt++;
						}
					}
				}
			}
			
			boolean continued = path.length() < metaPath.size() && isExpectedType;
			return Evaluation.of(included, continued);
		}
	}
	
	public final class SchemaMetaPathEvaluator implements Evaluator{

		public SchemaMetaPathEvaluator() {
		}
		
		@Override
		public Evaluation evaluate(Path path) {

			if(path.length() == 0)
			{
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}else if(path.length() < Vars.max_path_length)
			{
				return Evaluation.INCLUDE_AND_CONTINUE;
			}else if(path.length() == Vars.max_path_length)
			{
				return Evaluation.INCLUDE_AND_PRUNE;
			}else
			{
				return Evaluation.EXCLUDE_AND_PRUNE;
			}								
			
		}
	}
}
