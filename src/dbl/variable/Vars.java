package dbl.variable;

import dbl.variable.RelationManager.RelTypes;


public class Vars {

	public static enum Sampling{
		UnderSampling,
		UnderSamplingWithRatio,
		NoBalance,
		OverSampling,
		OverSamplingWithRatio
	}
	
	public static String propertyNodeType = "nodeType";//"slap: nodeType, koreaU: nodetype";
	public static String path = "./"; 

	public static enum NodeSampling{
		None,
		TopDegreeSampling,
		RandomSampling
	}
	
	public static String startClusterFile = path+"new_result/slap_topdb/160316 startNodeCliqueSize3_uniquenessRelationpath.txt";
	public static String endClusterFile = path+"new_result/slap_topdb/160316 endNodeCliqueSize3_uniquenessRelationpath.txt";
	
	public static RelTypes[] linkTypes = {
		RelationManager.RelTypes.hasDisease,
//		RelationManager.RelTypes.causeDisease, 
//		RelationManager.RelTypes.hasSubstructure, 
//		RelationManager.RelTypes.hasGeneFamily, 
//		RelationManager.RelTypes.causeSideEffect, 
//		RelationManager.RelTypes.expressIn, 
//		RelationManager.RelTypes.hasPathway, 
//		RelationManager.RelTypes.hasChemicalOntology,
//		RelationManager.RelTypes.express,
//		RelationManager.RelTypes.proteinProteinInteraction,
//		RelationManager.RelTypes.hasGO, 
//		RelationManager.RelTypes.bind 
		};
	
	public static String[] fileNameLRMs = {
		path+"new_result/slap/170211 hasDisease undersampling10_colleague_",
//		path+"new_result/slap/170111 causeDisease undersampling10_colleague_",
//		path+"new_result/slap/170111 hasSubstructure undersampling10_colleague_",
//		path+"new_result/slap/170111 hasGeneFamily undersampling10_colleague_",
//		path+"new_result/slap/170111 causeSideEffect undersampling10_colleague_",
//		path+"new_result/slap/170111 expressIn undersampling10_colleague_",
//		path+"new_result/slap/170111 hasPathway undersampling10_colleague_",
//		path+"new_result/slap/170111 hasChemicalOntology undersampling10_colleague_",
//		path+"new_result/slap/170111 express undersampling10_colleague_",
//		path+"new_result/slap/170111 proteinProteinInteraction undersampling10_colleague_",
//		path+"new_result/slap/170111 hasGO undersampling10_colleague_",
//		path+"new_result/slap/170111 bind undersampling10_colleague_"
		};
	

	public static String fileMetaPath = "./result/after_adding_gene_family/type_corr/TypeCorVal ";//+linkType.name()+"2.txt" ;
	public static String fileMetaPath2 = "./result/after_adding_gene_family/type_corr_csv/TypeCorVal ";//+linkType.name()+"2.txt" ;
//	file name where order of meta path is written
	public static String fileMetaPathOrder = path+"metapathorder/slap/MetaPathOrder ";//+linkType.name()+"2.txt";
//	database path name 
	public static String dbPath = path+"db\\SLAP.db";
//	schema database path name 
	public static String schemaDBPath = "db\\SlapSchema.db";
	
//	public static RelationManager.RelTypes relType = RelationManager.RelTypes.DI_DI;
//	true if you want to calculate type correaltion value for each meta path
	public static boolean wannaCalTypeCorVal = false;
//	true if you want to write meta path on the file(fileMetaPath)
	public static boolean writeMetaPathOnFile = false;
//	true if you want to write the orders of meta path
//	public static boolean writeMetaPathOrderOnFile = true;
	
	public static NodeSampling startNodeSampling = NodeSampling.TopDegreeSampling;
	public static NodeSampling endNodeSampling = NodeSampling.TopDegreeSampling;
	public static int topDegreeStartNode = 1000;
	public static int topDegreeEndNode = 1000;
	
	public static final int folds = 10;
	public static final Sampling sampling = Sampling.UnderSamplingWithRatio;
	public static final int samplingRatio = 10;

	
	public static String measurement = "jaccard";
	public static String measurement_sorensen = "sorensen";
	public static String measurement_jaccard = "jaccard";
	public static int max_path_length = 3;
	public static int cluster_path_len = 2;
	public static int alpha = 1;
}
