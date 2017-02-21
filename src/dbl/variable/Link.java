package dbl.variable;
import org.neo4j.graphdb.Node;


public class Link {
	
	private Node startNode;
	private Node endNode;
	
	public Link(Node startNode, Node endNode){
		this.startNode = startNode;
		this.endNode = endNode;
	}
	
	public Node getStartNode() {
		return startNode;
	}
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	public Node getEndNode() {
		return endNode;
	}
	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}	
}
