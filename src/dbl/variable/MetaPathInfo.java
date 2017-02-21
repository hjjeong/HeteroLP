package dbl.variable;

import java.util.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;


public class MetaPathInfo {

	/* *
	 * MetaPath consists of sequence of links type
	 * 
	 * */
	
	ArrayList<RelationshipType> metaPath = null;
	String startNodeType = null;
	String endNodeType = null;	
	double typeCorrValue = 0.0;
	String seperator = ">";
	long numOfCycle = 0;
	long numOfOnlyMetaPaths = 0;
	long numOfMetaPaths = 0;
	long numOfOnlyLinks = 0;
	long numOfLinks = 0;
	
	public MetaPathInfo() 
	{
		this.metaPath = null;
		this.startNodeType = "";
		this.endNodeType = "";
		this.typeCorrValue = 0.0;
	}	
	
	public MetaPathInfo(ArrayList<RelationshipType> metapath, String startNodeType, String endNodeType, double typeCorrValue) 
	{
		this.metaPath = metapath;
		this.startNodeType = startNodeType;
		this.endNodeType = endNodeType;
		this.typeCorrValue = typeCorrValue;		
	}

	public MetaPathInfo(ArrayList<RelationshipType> metapath, Node startNode, Node endNode, double typeCorrValue) 
	{
		this.metaPath = metapath;
		this.startNodeType = (String) startNode.getProperty(Vars.propertyNodeType);
		this.endNodeType = (String) endNode.getProperty(Vars.propertyNodeType);
		this.typeCorrValue = typeCorrValue;		
	}

	@Override
	public int hashCode()
	{
		return metaPath.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		/** 
		 *  @params Object obj 
		 *  @return true if obj has same metapath with this object
		 */
		
		// when obj has a type of either MetaPath or List<RelationshipType>, check the types of meta path. 
		if(this == obj)
			return true;

		if(obj instanceof MetaPathInfo)
		{
			MetaPathInfo metaPathClass_obj = (MetaPathInfo) obj;
			// only when the start node type and end node type are same as obj's, have same length 
			if(startNodeType.equals(metaPathClass_obj.getStartNodeType()) && endNodeType.equals(metaPathClass_obj.getEndNodeType()) && (metaPath.size()== metaPathClass_obj.getMetaPath().size()))
			{
				ArrayList<RelationshipType> metaPath_obj = metaPathClass_obj.getMetaPath();

				if(metaPath.equals(metaPath_obj))
					return true;
				else 
					return false;
				
			}else
				return false;


		}else if(obj instanceof List) 
		{
			if(metaPath.equals((ArrayList<RelationshipType>) obj))
				return true;
			else
				return false;
		}else
			return false;
	}

	public int getLength() 
	{
		// count the number of relationship == add 1 on the number of links 
		return metaPath.size();
	}
	
	@Override
	public String toString()
	{
		String relSeq = "";

		if(metaPath.size() > 0) 
		{
			Iterator<RelationshipType> itrMetaPath = metaPath.iterator();
			RelationshipType relType = itrMetaPath.next();
			relSeq = relType.name();
			while(itrMetaPath.hasNext())
			{
				relSeq = relSeq + seperator + itrMetaPath.next().name();
			}
		}else
		{
			System.out.println("No Meta Path");
			relSeq = null;
		}
		
		return relSeq;
	}

	public String getStartNodeType() 
	{	
		if(startNodeType != "")
			return startNodeType;

		return null;
	}

	public String getEndNodeType() 
	{
		if(endNodeType != "")
			return endNodeType.toString();

		return null;
	}

	public ArrayList<RelationshipType> getMetaPath() 
	{
		return metaPath;
	}

	public void setMetaPath(ArrayList<RelationshipType> metapath) 
	{
		if(!metapath.isEmpty() && this.metaPath.isEmpty())
		{
			System.out.println("initialize meta path");
			this.metaPath = metapath;
		}
		else if(!metapath.isEmpty())
		{
			System.out.println("replace meta path with parameter");
			this.metaPath = metapath;
		}else if(!this.metaPath.isEmpty())
		{
			System.out.println("parameter is empty");
		}else
		{
			this.metaPath = null;
		}
	}

	public void setTypeCorrValue(double value) 
	{

		this.typeCorrValue = value;
	}

	public double getTypeCorrValue() 
	{
		return typeCorrValue;
	}
	public long getNumOfCycle() {
		return numOfCycle;
	}

	public void setNumOfCycle(long numOfCycle) {
		this.numOfCycle = numOfCycle;
	}

	public long getNumOfOnlyMetaPaths() {
		return numOfOnlyMetaPaths;
	}

	public void setNumOfOnlyMetaPaths(long numOfOnlyMetaPaths) {
		this.numOfOnlyMetaPaths = numOfOnlyMetaPaths;
	}

	public long getNumOfMetaPaths() {
		return numOfMetaPaths;
	}

	public void setNumOfMetaPaths(long numOfMetaPaths) {
		this.numOfMetaPaths = numOfMetaPaths;
	}

	public long getNumOfOnlyLinks() {
		return numOfOnlyLinks;
	}

	public void setNumOfOnlyLinks(long numOfOnlyLinks) {
		this.numOfOnlyLinks = numOfOnlyLinks;
	}

	public long getNumOfLinks() {
		return numOfLinks;
	}

	public void setNumOfLinks(long numOfLinks) {
		this.numOfLinks = numOfLinks;
	}

}
