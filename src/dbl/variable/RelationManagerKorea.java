package dbl.variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.neo4j.graphdb.RelationshipType;


public class RelationManagerKorea {

	public static enum RelTypes implements RelationshipType
    {
		DI_DI("DI_DI"),
		DR_DI("DR_DI"),
		DR_DR("DR_DR"),
		DR_GE("DR_GE"),
		DR_PW("DR_PW"),
		DR_SE("DR_SE"),
		GE_DI("GE_DI"),
		GE_DR("GE_DR"),
		GE_GE("GE_GE"),
		GE_PR("GE_PR"),
		GO_GE("GO_GE"),
		PW_GE("PW_GE"),
		PW_PW("PW_PW"),
		TG_DI("TG_DI"),
		TG_DR("TG_DR"),
		TG_PW("TG_PW");
		
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
	
	public static HashMap<String, String> aliasToNode = new HashMap<String, String> () {{
		put("DI", "disease");
		put("DR", "drug");
		put("GE", "gene");
		put("PW", "pathway");
		put("SE", "side_effect");
		put("PR", "gene");
		put("GO", "gene_ontology");
		put("TG", "target");
    }}; 

	public static HashMap<String, String> nodeToAlias = new HashMap<String, String> () {{
		put("disease", "DI");
		put("drug", "DR");
		put("gene", "GE");
		put("pathway", "PW");
		put("side_effect", "SE");
		put("gene_ontology", "GO");
		put("target", "TG");
    }}; 

    private static ArrayList<String> nodeTypes = new ArrayList<String>() {{
    	add("disease");
    	add("gene");
    	add("drug");
    	add("pathway");
    	add("gene_ontology");
    	add("side_effect");
    	add("target");
    }};    

	public static ArrayList<String> getNodeTypes()
	{
		return nodeTypes;
	}

	private static HashMap<String, ArrayList<String>> nodesAdjacentToLink = new HashMap<String, ArrayList<String>>(){{
		put("DI_DI", new ArrayList<String>(){{add("disease"); add("disease");}});
		put("DR_DI", new ArrayList<String>(){{add("drug"); add("disease");}});
		put("DR_DR", new ArrayList<String>(){{add("drug"); add("drug");}});
		put("DR_GE", new ArrayList<String>(){{add("drug"); add("gene");}});
		put("DR_PW", new ArrayList<String>(){{add("drug"); add("pathway");}});
		put("DR_SE", new ArrayList<String>(){{add("drug"); add("side_effect");}});
		put("GE_DI", new ArrayList<String>(){{add("gene"); add("disease");}});
		put("GE_DR", new ArrayList<String>(){{add("gene"); add("drug");}});
		put("GE_GE", new ArrayList<String>(){{add("gene"); add("gene");}});
		put("GE_PR", new ArrayList<String>(){{add("gene"); add("gene");}});
		put("GO_GE", new ArrayList<String>(){{add("gene_ontology"); add("gene");}});
		put("PW_GE", new ArrayList<String>(){{add("pathway"); add("gene");}});
		put("PW_PW", new ArrayList<String>(){{add("pathway"); add("pathway");}});
		put("TG_DI", new ArrayList<String>(){{add("target"); add("disease");}});
		put("TG_DR", new ArrayList<String>(){{add("target"); add("drug");}});
		put("TG_PW", new ArrayList<String>(){{add("target"); add("pathway");}});
	}};

	public static String getRelType(String startNodeType, String endNodeType) throws Exception
	{
		String link = nodeToAlias.get(startNodeType)+"_"+nodeToAlias.get(endNodeType);
		
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
		String[] types = new String[2];

		String[] alias = relType.split("_");
		
		types[0] = aliasToNode.get(alias[0]);
		types[1] = aliasToNode.get(alias[1]);
		return types;
	}
}
