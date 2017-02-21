package dbl.data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import dbl.operation.QueryForGraph;
import dbl.variable.Link;
import dbl.variable.RelationManager.RelTypes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class DataConstructor {
	
	private ArrayList<Node> sTypeNodeList;
	private ArrayList<Node> eTypeNodeList;
	
	private ArrayList<Instances> trainInstanceList;
	private ArrayList<Instances> testInstanceList;
	private Map<Double, Link> dataset;
	
	private static GraphDatabaseService graphdb;
	
	public DataConstructor(GraphDatabaseService graphdb, ArrayList<Node> sTypeNodeList, ArrayList<Node> eTypeNodeList, int folds){
		this.sTypeNodeList = sTypeNodeList;
		this.eTypeNodeList = eTypeNodeList;
		this.graphdb = graphdb;
		
		makekFoldData(folds);
	}
	
	private void makekFoldData(int folds){
		System.out.println("makekFold");
		trainInstanceList = new ArrayList<>();
		testInstanceList = new ArrayList<>();
		dataset = new HashMap<>();
		
		FastVector atts = new FastVector();
		atts.addElement(new Attribute("id"));
		Instances dataSetInstance = new Instances("LinkSet", atts, 0);
		
		double id = 0;
		int linkCnt = 0;
		
		for(Node sNode:sTypeNodeList){
			for(Node eNode:eTypeNodeList){
				if(QueryForGraph.hasRelationWith(graphdb, sNode,eNode)){
					linkCnt++;
				}
				dataset.put(id, new Link(sNode, eNode));
				double[] values = new double[dataSetInstance.numAttributes()];
				values[0] = id;
				dataSetInstance.add(new Instance(1.0,values));
				id++;
			}
		}
		
		Random rand = new Random();
		Instances data = new Instances(dataSetInstance);
		data.randomize(rand);
		
		for(int n=0; n<folds; n++){
			System.out.println(n+" folds");
			Instances train = data.trainCV(folds, n);
			Instances test = data.testCV(folds, n);
			trainInstanceList.add(train);
			testInstanceList.add(test);
			System.out.println("trainInstance size: "+train.numInstances());
			System.out.println("testInstance size: "+test.numInstances());
		}
	}
	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTestData(){
		ArrayList<HashMap<Node, ArrayList<Node>>> testDataList = new ArrayList<>();
		
		for(int i=0; i<testInstanceList.size(); i++){
			HashMap<Node, ArrayList<Node>> testdata = new HashMap<>();
			Instances testInst = testInstanceList.get(i);
			System.out.println("testInst size: "+testInst.numInstances());
			for(int j=0; j<testInst.numInstances(); j++){
				Link test = dataset.get(testInst.instance(j).value(0));
				if(testdata.containsKey(test.getStartNode())){
					ArrayList<Node> nodeList = testdata.get(test.getStartNode());
					nodeList.add(test.getEndNode());
					testdata.put(test.getStartNode(), nodeList);
				}
				else{
					ArrayList<Node> nodeList = new ArrayList<Node>();
					nodeList.add(test.getEndNode());
					testdata.put(test.getStartNode(), nodeList);
				}
			}
			System.out.println("testdata startNode size: "+testdata.size());
			testDataList.add(testdata);
		}
		
		return testDataList;
	}

	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTrainDataNoBalance(){
		ArrayList<HashMap<Node, ArrayList<Node>>> trainDataList = new ArrayList<>();
		
		for(int i=0; i<trainInstanceList.size(); i++){
			HashMap<Node, ArrayList<Node>> traindata = new HashMap<>();
			Instances trainInst = trainInstanceList.get(i);
			System.out.println("trainInst size: "+trainInst.numInstances());
			for(int j=0; j<trainInst.numInstances(); j++){
				Link trainLink = dataset.get(trainInst.instance(j).value(0));
				if(traindata.containsKey(trainLink.getStartNode())){
					ArrayList<Node> nodeList = traindata.get(trainLink.getStartNode());
					nodeList.add(trainLink.getEndNode());
					traindata.put(trainLink.getStartNode(), nodeList);
				}
				else{
					ArrayList<Node> nodeList = new ArrayList<Node>();
					nodeList.add(trainLink.getEndNode());
					traindata.put(trainLink.getStartNode(), nodeList);
				}
			}
			trainDataList.add(traindata);
			System.out.println("traindata startNode size: "+traindata.size());
		}
		
		return trainDataList;
	}
	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTrainLinkData(){
		ArrayList<HashMap<Node, ArrayList<Node>>> trainLinkDataList = new ArrayList<>();
		
		for(int i=0; i<trainInstanceList.size(); i++){
			HashMap<Node, ArrayList<Node>> traindata = new HashMap<>();
			Instances trainInst = trainInstanceList.get(i);
			int linkCnt = 0;
			
			for(int j=0; j<trainInst.numInstances(); j++){
				Link trainLink = dataset.get(trainInst.instance(j).value(0));
				if(QueryForGraph.hasRelationWith(graphdb, trainLink.getStartNode(), trainLink.getEndNode())){
					if(traindata.containsKey(trainLink.getStartNode())){
						ArrayList<Node> nodeList = traindata.get(trainLink.getStartNode());
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					else{
						ArrayList<Node> nodeList = new ArrayList<Node>();
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					linkCnt++;
				}
			}
			System.out.println("trainLinkCnt: "+linkCnt);
			trainLinkDataList.add(traindata);
		}
		
		return trainLinkDataList;
	}
	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTrainNonLinkData(){
		ArrayList<HashMap<Node, ArrayList<Node>>> trainNonLinkDataList = new ArrayList<>();
		
		for(int i=0; i<trainInstanceList.size(); i++){
			HashMap<Node, ArrayList<Node>> traindata = new HashMap<>();
			Instances trainInst = trainInstanceList.get(i);
			int nonlinkCnt = 0;
			
			for(int j=0; j<trainInst.numInstances(); j++){
				Link trainNonLink = dataset.get(trainInst.instance(j).value(0));
				if(!QueryForGraph.hasRelationWith(graphdb, trainNonLink.getStartNode(), trainNonLink.getEndNode())){
					if(traindata.containsKey(trainNonLink.getStartNode())){
						ArrayList<Node> nodeList = traindata.get(trainNonLink.getStartNode());
						nodeList.add(trainNonLink.getEndNode());
						traindata.put(trainNonLink.getStartNode(), nodeList);
					}
					else{
						ArrayList<Node> nodeList = new ArrayList<Node>();
						nodeList.add(trainNonLink.getEndNode());
						traindata.put(trainNonLink.getStartNode(), nodeList);
					}
					nonlinkCnt++;
				}
			}
			System.out.println("trainNonLinkCnt: "+nonlinkCnt);
			trainNonLinkDataList.add(traindata);
		}
		
		return trainNonLinkDataList;
	}
	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTrainUnderSampling(){
		ArrayList<HashMap<Node, ArrayList<Node>>> trainDataList = new ArrayList<>();
		for(int i=0; i<trainInstanceList.size(); i++){
			HashMap<Node,ArrayList<Node>> traindata = new HashMap<>();
			ArrayList<Link> nonLinkData = new ArrayList<>();
			int numOfLink = 0, numOfNonLink = 0;
			Instances trainInst = trainInstanceList.get(i);
			
			for(int j=0; j<trainInstanceList.get(i).numInstances(); j++){
				Link trainLink = dataset.get(trainInstanceList.get(i).instance(j).value(0));
				if(QueryForGraph.hasRelationWith(graphdb, trainLink.getStartNode(), trainLink.getEndNode())){
					if(traindata.containsKey(trainLink.getStartNode())){
						ArrayList<Node> nodeList = traindata.get(trainLink.getStartNode());
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					else{
						ArrayList<Node> nodeList = new ArrayList<Node>();
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					numOfLink++;
					
				}
				else{
					nonLinkData.add(trainLink);
					numOfNonLink++;
				}
			}
			System.out.println("Undersampling link: "+numOfLink);
			Random random = new Random();
			ArrayList<Integer> idx = new ArrayList<>();
			int nonLinkCnt = numOfLink;
			while(idx.size()<nonLinkCnt){
				int n = random.nextInt(numOfNonLink);
				if(idx.size()!=0&&idx.contains(n))
					continue;
				else
					idx.add(n);
			}
			int cnt = 0;
			for(int j=0; j<idx.size(); j++){
				Link nonlink = nonLinkData.get(idx.get(j));
				if(traindata.containsKey(nonlink.getStartNode())){
					ArrayList<Node> nodeList = traindata.get(nonlink.getStartNode());
					nodeList.add(nonlink.getEndNode());
					traindata.put(nonlink.getStartNode(), nodeList);
				}
				else{
					ArrayList<Node> nodeList = new ArrayList<>();
					nodeList.add(nonlink.getEndNode());
					traindata.put(nonlink.getStartNode(), nodeList);
				}
				cnt++;
			}
			System.out.println("UnderSampling nonlink: "+cnt);
			trainDataList.add(traindata);
		}
		System.out.println("all fold train data size: "+trainDataList.size());
		return trainDataList;
	}
	
	public ArrayList<HashMap<Node, ArrayList<Node>>> getTrainDataUndersamplingWithRatio(int ratio){
		ArrayList<HashMap<Node, ArrayList<Node>>> trainDataList = new ArrayList<>();
		for(int i=0; i<trainInstanceList.size(); i++){
			HashMap<Node,ArrayList<Node>> traindata = new HashMap<>();
			ArrayList<Link> nonLinkData = new ArrayList<>();
			int numOfLink = 0, numOfNonLink = 0;
			Instances trainInst = trainInstanceList.get(i);
			
			for(int j=0; j<trainInstanceList.get(i).numInstances(); j++){
				Link trainLink = dataset.get(trainInstanceList.get(i).instance(j).value(0));
				if(QueryForGraph.hasRelationWith(graphdb, trainLink.getStartNode(), trainLink.getEndNode())){
					if(traindata.containsKey(trainLink.getStartNode())){
						ArrayList<Node> nodeList = traindata.get(trainLink.getStartNode());
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					else{
						ArrayList<Node> nodeList = new ArrayList<Node>();
						nodeList.add(trainLink.getEndNode());
						traindata.put(trainLink.getStartNode(), nodeList);
					}
					numOfLink++;
				}
				else{
					nonLinkData.add(trainLink);
					numOfNonLink++;
				}
			}
			System.out.println("UnderSamplingWithRatio numOfLink: "+numOfLink);
			Random random = new Random();
			ArrayList<Integer> idx = new ArrayList<>();
			int nonLinkCnt = (int) ((double)(dataset.size()*ratio)/100)-numOfLink;
			while(idx.size()<nonLinkCnt){
				int n = random.nextInt(numOfNonLink);
				if(idx.size()!=0&&idx.contains(n))
					continue;
				else
					idx.add(n);
			}
			int cnt = 0;
			for(int j=0; j<idx.size(); j++){
				Link nonlink = nonLinkData.get(idx.get(j));
				if(traindata.containsKey(nonlink.getStartNode())){
					ArrayList<Node> nodeList = traindata.get(nonlink.getStartNode());
					nodeList.add(nonlink.getEndNode());
					traindata.put(nonlink.getStartNode(), nodeList);
				}
				else{
					ArrayList<Node> nodeList = new ArrayList<>();
					nodeList.add(nonlink.getEndNode());
					traindata.put(nonlink.getStartNode(), nodeList);
				}
				cnt++;
			}
			System.out.println("UnderSamplingWithRatio nonlink cnt: "+cnt);
			trainDataList.add(traindata);
		}
		return trainDataList;
	}
}
 