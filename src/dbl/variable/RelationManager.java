package dbl.variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.neo4j.graphdb.RelationshipType;

public class RelationManager {

	public static enum RelTypes implements RelationshipType
    {
		bind ("bind"),
        causeDisease ("causeDisease"),
        causeSideEffect ("causeSideEffect"),
        express ("express"),
        expressIn ("expressIn"),
        hasChemicalOntology ("hasChemicalOntology"),
        hasGeneFamily ("hasGeneFamily"),
        hasGO ("hasGO"),
        hasPathway ("hasPathway"),
        hasSubstructure ("hasSubstructure"),
        proteinProteinInteraction ("proteinProteinInteraction"),
		hasDisease("hasDisease");
		
        private String text;
		
        RelTypes(String text){
        	this.text = text;
        }
        
        public String getText(){
        	return this.text;
        }
        
        public static RelTypes fromString(String text){
        	if(text!=null){
        		for(RelTypes rel:RelTypes.values()){
        			if(text.equalsIgnoreCase(rel.text)){
        				return rel;
        			}
        		}
        	}
        	return null;
        }
    }
	public static HashMap<String, String> aliasTypes = new HashMap<String, String> () {{
    	put("go", "Gene ontology");
    	put("co", "Chemical ontology");
    	put("ss", "Substructure");
    	put("tg", "Target");
    	put("ti", "Tissue");
    	put("pw", "Pathway");
    	put("di", "Disease");
    	put("cc", "Chemical Compound/Drug");
    	put("se", "Side effect");
    	put("gf", "Gene Family");
    }}; 
    private static ArrayList<String> nodeTypes = new ArrayList<String>() {{
    	add("Gene ontology");
    	add("Chemical ontology");
    	add("Substructure");
    	add("Target");
    	add("Tissue");
    	add("Pathway");
    	add("Disease");
    	add("Chemical Compound/Drug");
    	add("Side effect");
    	add("Gene Family");
    }};    

	public static ArrayList<String> getNodeTypes()
	{
		return nodeTypes;
	}
	
	private static HashMap<String, ArrayList<String>> nodesAdjacentToLink = new HashMap<String, ArrayList<String>>(){{
		put("bind", new ArrayList<String>(){{add("Target"); add("Chemical Compound/Drug");}} );
		put("causeDisease", new ArrayList<String>(){{add("Target"); add("Disease");}} );
		put("causeSideEffect", new ArrayList<String>(){{add("Chemical Compound/Drug"); add("Side effect");}} );
		put("express", new ArrayList<String>(){{add("Target"); add("Chemical Compound/Drug");}} );
		put("expressIn", new ArrayList<String>(){{add("Tissue"); add("Target");}} );
		put("hasChemicalOntology", new ArrayList<String>(){{add("Chemical Compound/Drug"); add("Chemical ontology");}} );
		put("hasGeneFamily", new ArrayList<String>(){{add("Target"); add("Gene Family");}} );
		put("hasGO", new ArrayList<String>(){{add("Target"); add("Gene ontology");}} );
		put("hasPathway", new ArrayList<String>(){{add("Target"); add("Pathway");}} );
		put("hasSubstructure", new ArrayList<String>(){{add("Chemical Compound/Drug"); add("Substructure");}} );
		put("proteinProteinInteraction", new ArrayList<String>(){{add("Target"); add("Target");}} );
		put("hasDisease", new ArrayList<String>(){{add("Disease"); add("Chemical Compound/Drug");}} );
	}};
	
	public static String getRelType(String startNodeType, String endNodeType) throws Exception
	{
		Iterator<String> itor_key =nodesAdjacentToLink.keySet().iterator();
		String link = null;
		while(itor_key.hasNext())
		{
			link = itor_key.next();
			if(nodesAdjacentToLink.get(link).contains(startNodeType) && nodesAdjacentToLink.get(link).contains(endNodeType))
				break;
		}
		
		return link;
	}
	
	public static String[] getNodeTypesWithRelType(RelTypes relType)
	{
		ArrayList<String> nodes = nodesAdjacentToLink.get(relType.name());
		String[] types = new String[2];
		
		types[0] = nodes.get(0);
		types[1] = nodes.get(1);
		
		return types;
	}
	
	public static String[] getNodeTypesWithRelType(String relType)
	{
		ArrayList<String> nodes = nodesAdjacentToLink.get(relType);
		String[] types = new String[2];

		types[0] = nodes.get(0);
		types[1] = nodes.get(1);
		return types;
	}
	
}
