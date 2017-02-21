package dbl.variable;
import java.util.ArrayList;
import java.util.Comparator;

import org.neo4j.graphdb.RelationshipType;

public class MetaPathComparator implements Comparator<ArrayList<RelationshipType>> {

	@Override
	public int compare(ArrayList<RelationshipType> o1, ArrayList<RelationshipType> o2) {
		int result = 0;
		
		int len1 = o1.size();
		int len2 = o2.size();
		if(len1 > len2)
		{
			result = 1;
		}else if(len1 < len2) 
		{
			result = -1;
		}else
		{
			for(int i = 0; i < len1; i++)
			{
				if(!o1.get(i).toString().equals(o2.get(i).toString()))
				{
					result = o1.get(i).toString().compareTo(o2.get(i).toString());
					break;
				}
			}
		}
		
		return result;
	}

}
