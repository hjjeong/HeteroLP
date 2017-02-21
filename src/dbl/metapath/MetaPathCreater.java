package dbl.metapath;
import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

import application.TypeCorrValCalculator;
import dbl.database.DBManager;
import dbl.database.SchemaDBManager;
import dbl.file.FileBufferWriter;
import dbl.file.FileLineReader;
import dbl.operation.QueryForGraph;
import dbl.variable.MetaPathInfo;
import dbl.variable.RelationManager;
import dbl.variable.Vars;
import dbl.variable.RelationManager.RelTypes;

public class MetaPathCreater {

	public static void main(String[] args) throws Exception {
		String startNodeType = null;
		String endNodeType = null;
		GraphDatabaseService db = null;
		ArrayList<MetaPathInfo> metaPaths = null;
		ArrayList<ArrayList<RelationshipType>> raw_metaPaths = null;

		db = DBManager.openDB(Vars.dbPath);

		System.out.println("Gene family count: "+QueryForGraph.findGeneFamilyNodes(db, "Target"));
		
		for(int j = 0; j < Vars.linkTypes.length; j++)
		{
			RelTypes linkType = Vars.linkTypes[j];
			String[] nodeTypes = RelationManager.getNodeTypesWithRelType(linkType);
			startNodeType = nodeTypes[0];
			endNodeType = nodeTypes[1];
			metaPaths = new ArrayList<>();
			


			FileLineReader flr = new FileLineReader(Vars.fileMetaPathOrder + linkType.name()+".txt");//Vars.fileMetaPathOrders[n]);
			raw_metaPaths = flr.getMetaPathWithOrder();


			FileBufferWriter fbwOfMetaPaths = new FileBufferWriter(Vars.fileMetaPath+ linkType.name() + ".txt");
			FileBufferWriter fbwOfMetaPaths2 = new FileBufferWriter(Vars.fileMetaPath2+ linkType.name() + ".txt");
			
			System.out.println("# of meta Path is : " + raw_metaPaths.size());
			for(int i = 0; i < raw_metaPaths.size(); i++)
			{
				TypeCorrValCalculator typeCorrValCalculator = new TypeCorrValCalculator(raw_metaPaths.get(i), startNodeType, endNodeType, linkType.name(), Vars.measurement);
				MetaPathInfo metaPath = typeCorrValCalculator.calculateTypeCorrVal(db);

				metaPaths.add(i, metaPath);
				System.out.println("Meta Path is : " + metaPaths.toString());
				fbwOfMetaPaths.writeMetaPath2(i, metaPath);
				fbwOfMetaPaths2.writeMetaPath(i, metaPath);
			}
			fbwOfMetaPaths.close();
			fbwOfMetaPaths2.close();
		}
		DBManager.closeDB();
		
	}

}
