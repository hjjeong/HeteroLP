package application;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import dbl.data.DataConstructor;
import dbl.database.DBManager;
import dbl.database.SchemaDBManager;
import dbl.file.FileBufferWriter;
import dbl.file.FileLineReader;
import dbl.operation.QueryForGraph;
import dbl.variable.MetaPathInfo;
import dbl.variable.RelationManager;
import dbl.variable.RelationManager.RelTypes;
import dbl.variable.Vars;
import dbl.variable.Vars.NodeSampling;

public class Main {

	static ArrayList<ArrayList<RelationshipType>> raw_metaPaths = null;
	static ArrayList<MetaPathInfo> metaPaths = null;
	static GraphDatabaseService db = null; 
	static String startNodeType = null; 
	static String endNodeType = null;
	
	static SchemaDBManager schemaDBManager;
	
	public static void main(String[] args) throws Exception {

		metaPaths = new ArrayList<>();
		schemaDBManager = new SchemaDBManager();
		
		//		<open database>
		db = DBManager.openDB(Vars.dbPath);
	
		System.out.println("----------dbPath: "+Vars.dbPath+"----------");
		System.out.println("Gene family count: "+QueryForGraph.findGeneFamilyNodes(db, "Target"));
	    System.out.println("----------------Delete Chemical Nodes-----------------");
	    QueryForGraph.deleteNodes(db, "Chemical Compound/Drug", 50000);
	
		for(int n=0; n<Vars.linkTypes.length; n++){
			RelTypes linkType = Vars.linkTypes[n];
			String[] nodeTypes = RelationManager.getNodeTypesWithRelType(linkType);
			startNodeType = nodeTypes[0];
			endNodeType = nodeTypes[1];
			metaPaths = new ArrayList<>();

			System.out.println("--------startNode type: "+startNodeType+" endNodeType: "+endNodeType+"-------");
			System.out.println(Vars.fileNameLRMs[n]);
			
			//		read from the file with order and construct metaPath with order 
			FileLineReader flr = new FileLineReader(Vars.fileMetaPathOrder + linkType.name()+".txt");//Vars.fileMetaPathOrders[n]);
			raw_metaPaths = flr.getMetaPathWithOrder();

			if(Vars.wannaCalTypeCorVal)
			{
				//			if you want to calculate type correlation value for each meta path	

				FileBufferWriter fbwOfMetaPaths = new FileBufferWriter(Vars.fileMetaPath);
				System.out.println("# of meta Path is : " + raw_metaPaths.size());
				for(int i = 0; i < raw_metaPaths.size(); i++)
				{
					TypeCorrValCalculator typeCorrValCalculator = new TypeCorrValCalculator(raw_metaPaths.get(i), startNodeType, endNodeType, linkType.name(), Vars.measurement);
					MetaPathInfo metaPath = typeCorrValCalculator.calculateTypeCorrVal(db);

					metaPaths.add(i, metaPath);
					System.out.println("Meta Path is : " + metaPaths.toString());
					fbwOfMetaPaths.writeMetaPath(i, metaPath);
				}
				fbwOfMetaPaths.close();
				//			write the result on the file


			}else 

			{
				//			else if you want to read the type correlation value from the file

			}

			System.out.println("calculate LRM start");
			calculateLRM(raw_metaPaths,n);
			System.out.println("calculate LRM end");
		}
		DBManager.closeDB();
	}

	private static void calculateLRM(ArrayList<ArrayList<RelationshipType>> metaPathList, int n){

		ArrayList<Node> startNodes;
		ArrayList<Node> endNodes;
		RelTypes linkType = Vars.linkTypes[n];

		String[] nodeTypes = RelationManager.getNodeTypesWithRelType(linkType);

		if(Vars.startNodeSampling == NodeSampling.TopDegreeSampling) {
			startNodes = QueryForGraph.findTopDegreeNodeListForTypeWithLink(db, nodeTypes[0], Vars.topDegreeStartNode, linkType);
//			startNodes = QueryForGraph.findTopDegreeNodeListForType(db, nodeTypes[0], Vars.topDegreeStartNode);
		}
		else if(Vars.startNodeSampling==NodeSampling.RandomSampling){
			startNodes = QueryForGraph.findNodeList(db, nodeTypes[0], Vars.topDegreeStartNode);
		}
		else{
			startNodes = QueryForGraph.getNodesWithType(db, nodeTypes[0]);
		}

		if(Vars.endNodeSampling == NodeSampling.TopDegreeSampling) {
			endNodes = QueryForGraph.findTopDegreeNodeListForTypeWithLink(db, nodeTypes[1], Vars.topDegreeEndNode, linkType);
//			endNodes = QueryForGraph.findTopDegreeNodeListForType(db, nodeTypes[1], Vars.topDegreeEndNode);
		}
		else if(Vars.endNodeSampling == NodeSampling.RandomSampling){
			endNodes = QueryForGraph.findNodeList(db, nodeTypes[1], Vars.topDegreeEndNode);
		}
		else{
			endNodes = QueryForGraph.getNodesWithType(db, nodeTypes[1]);
		}

		//Data Construction
		DataConstructor dataConstructor = new DataConstructor(db ,startNodes, endNodes, Vars.folds);
		ArrayList<HashMap<Node, ArrayList<Node>>> testdata = dataConstructor.getTestData();
		ArrayList<HashMap<Node, ArrayList<Node>>> traindata = new ArrayList<>();
		
		switch(Vars.sampling){
			case UnderSampling:
				traindata = dataConstructor.getTrainUnderSampling();
				featureVectorWriteDefault(testdata, traindata, metaPathList,n);
				break;
	
			case UnderSamplingWithRatio:
				traindata = dataConstructor.getTrainDataUndersamplingWithRatio(Vars.samplingRatio);
				featureVectorWriteDefault(testdata, traindata, metaPathList,n);
				break;
	
			case NoBalance:
				traindata = dataConstructor.getTrainDataNoBalance();
				featureVectorWriteDefault(testdata, traindata, metaPathList,n);
				break;
			}
	}

	
	private static void featureVectorWriteDefault(ArrayList<HashMap<Node, ArrayList<Node>>> testdata, 
			ArrayList<HashMap<Node, ArrayList<Node>>> traindata, 
			ArrayList<ArrayList<RelationshipType>> metaPathList, int n){
		FeatureVectorConstructor fvConstructor = new FeatureVectorConstructor();
		ArrayList<ArrayList<RelationshipType>> symmetricStartMetaPaths = new ArrayList<>();
		ArrayList<ArrayList<RelationshipType>> symmetricEndMetaPaths = new ArrayList<>();
		try {
			symmetricStartMetaPaths = schemaDBManager.getMetaPathFromStartEndNodeType(startNodeType, startNodeType, Vars.cluster_path_len);
			symmetricEndMetaPaths = schemaDBManager.getMetaPathFromStartEndNodeType(endNodeType, endNodeType, Vars.cluster_path_len);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("symmetricStartMetaPaths size: "+symmetricStartMetaPaths.size());
		System.out.println("symmetricEndMetaPaths size: "+symmetricEndMetaPaths.size());
		try {
			for(int i=0; i<traindata.size(); i++){
				System.out.println(i+"folds train start");
				FileBufferWriter fbw = new FileBufferWriter(Vars.fileNameLRMs[n]+"train_"+i+".csv");	
	
				HashMap<Node, ArrayList<Node>> traindataForEachFold = traindata.get(i);
				for(Node startNode:traindataForEachFold.keySet()){
					Map<Long, long[]> featureVector = fvConstructor.getFeatureVectorForLRMWithColleague(db, startNode, 
							traindataForEachFold.get(startNode), metaPathList, symmetricStartMetaPaths, symmetricEndMetaPaths, true,n);
					fbw.writeOnBuf(startNode.getId(),featureVector);
				}	
				fbw.close();
				
//				FileBufferWriter fbwCN = new FileBufferWriter(Vars.fileNameClustering[n]+"train_"+i+".csv");
//				for(Node startNode:traindataForEachFold.keySet()){
//					Set<Node> startCluster = symmetriClustering.getSymmetricCluster(db, startNode, symmetricStartMetaPaths);
//					for(Node endNode:traindataForEachFold.get(startNode)){
//						Set<Node> endCluster = symmetriClustering.getSymmetricCluster(db, endNode, symmetricEndMetaPaths);
//						ArrayList<Long> feature = symmetriClustering.getCommonNeighbor(db, startNode, endNode, startCluster, endCluster);
//						fbwCN.writeOnBuf(feature);
//					}
//				}
//				fbwCN.close();
			}	

			for(int i=0; i<testdata.size(); i++){
				System.out.println(i+"folds test start");
				FileBufferWriter fbw = new FileBufferWriter(Vars.fileNameLRMs[n]+"test_"+i+".csv");
//				FileBufferWriter fbwClustering = new FileBufferWriter(Vars.fileNameClustering[n]+"test_"+i+".csv");	
				HashMap<Node, ArrayList<Node>> testdataForEachFold = testdata.get(i);
				for(Node startNode:testdataForEachFold.keySet()){
					Map<Long, long[]> featureVector = fvConstructor.getFeatureVectorForLRMWithColleague(db, startNode, 
							testdataForEachFold.get(startNode), metaPathList, symmetricStartMetaPaths, symmetricEndMetaPaths, true, n);
					fbw.writeOnBuf(startNode.getId(),featureVector);
					
				}
				fbw.close();

				
//				FileBufferWriter fbwCN = new FileBufferWriter(Vars.fileNameClustering[n]+"test_"+i+".csv");
//				for(Node startNode:testdataForEachFold.keySet()){
//					Set<Node> startCluster = symmetriClustering.getSymmetricCluster(db, startNode, symmetricStartMetaPaths);
//					for(Node endNode:testdataForEachFold.get(startNode)){
//						Set<Node> endCluster = symmetriClustering.getSymmetricCluster(db, endNode, symmetricEndMetaPaths);
//						ArrayList<Long> feature = symmetriClustering.getCommonNeighbor(db, startNode, endNode, startCluster, endCluster);
//						fbwCN.writeOnBuf(feature);
//					}
//				}
//				fbwCN.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
