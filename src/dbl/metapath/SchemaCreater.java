package dbl.metapath;
import java.util.ArrayList;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import dbl.database.SchemaDBManager;
import dbl.file.FileBufferWriter;
import dbl.variable.RelationManager;
import dbl.variable.Vars;
import dbl.variable.RelationManager.RelTypes;

public class SchemaCreater {

	public static void main(String[] args) throws Exception
	{
		ArrayList<ArrayList<RelationshipType>> raw_metaPaths = null;
		
		String startNodeType = null;
		String endNodeType = null;
		

		SchemaDBManager schemaDB = new SchemaDBManager();
		try(Transaction tx = schemaDB.getSchemaDB().beginTx())
		{
			for(int j = 0; j < Vars.linkTypes.length; j++)
			{
				RelTypes linkType = Vars.linkTypes[j];
				
				String[] nodeTypes = RelationManager.getNodeTypesWithRelType(linkType);
				startNodeType = nodeTypes[0];
				endNodeType = nodeTypes[1];
				System.out.println("=================" + linkType.name() + "======================");
				System.out.println("=================" + startNodeType+">" +endNodeType + "======================");
				
				raw_metaPaths = schemaDB.getMetaPathFromStartEndNodeType(startNodeType, endNodeType);
				
				FileBufferWriter fbwOfMetaPathOrder = new FileBufferWriter(Vars.fileMetaPathOrder+ linkType.name()+".txt");
				fbwOfMetaPathOrder.writeMetaPathWithOrder(raw_metaPaths);
				fbwOfMetaPathOrder.close();
			}
		}
		
		schemaDB.closeSchemaDB();
		
	}
}
