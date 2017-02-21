package dbl.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import dbl.variable.MetaPathInfo;
import dbl.variable.MetaPathComparator;


public class FileBufferWriter {

	public BufferedOutputStream bos = null;

	public FileBufferWriter(String fileName) throws FileNotFoundException 
	{
		bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
	}
	
	public FileBufferWriter(String fileName, int bufsize)  throws FileNotFoundException 
	{
		bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)), bufsize);
	}
	
	public void openFile(String fileName) throws FileNotFoundException
	{
		if(bos == null)
		{
			bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
		}
	}

	public void writeOnBuf(String line) throws IOException
	{		
		line = line + "\n";
		bos.write(line.getBytes());
	}
	
	public void writeOnBuf(long startNodeID, Map<Long, long[]> featureVector) throws IOException{
		//startNode id, endNode id, value (metapath count, degree, label)
		
		for(Long endNodeID:featureVector.keySet()){
			String result = Long.toString(startNodeID)+","+Long.toString(endNodeID);
			for(int i=0; i<featureVector.get(endNodeID).length; i++){
				result = result+","+Long.toString(featureVector.get(endNodeID)[i]);
			}
			result = result+"\n";
			bos.write(result.getBytes());
		}
	}
	
	public void writeOnBufDouble(long startNodeID, Map<Long, double[]> featureVector) throws IOException{
		//startNode id, endNode id, value (metapath count, degree, label)
		
		for(Long endNodeID:featureVector.keySet()){
			String result = Long.toString(startNodeID)+","+Long.toString(endNodeID);
			for(int i=0; i<featureVector.get(endNodeID).length; i++){
				result = result+","+Double.toString(featureVector.get(endNodeID)[i]);
			}
			result = result+"\n";
			bos.write(result.getBytes());
		}
	}
	
	public void writeOnBuf(ArrayList<Long> featVector) throws IOException
	{		
		/***
		 * @params ArrayList<Long> featVector has a list of feature values  
		 * @return nothing but write featVector on buffer with csv style(,)
		 */
		String result = "";
		
		Iterator<Long> itorFeat= featVector.iterator();
		
		if(itorFeat.hasNext())
		{
			result = itorFeat.next().toString();
		}
		
		while(itorFeat.hasNext())
		{
			result = result + "," + itorFeat.next().toString();
		}
		result = result + "\n";

		bos.write(result.getBytes());
	}

	public void writeMetaPathWithOrder(ArrayList<ArrayList<RelationshipType>> metaPaths) throws IOException 
	{
		int i = 0;
		
		metaPaths.sort(new MetaPathComparator());
		
		for(i = 0; i < metaPaths.size(); i++)
		{
			ArrayList<RelationshipType> metaPath = metaPaths.get(i);
			bos.write(getFormattedMetaPathWithOrder(i, metaPath).getBytes());
		}
		
		close();
	}

	public void writeMetaPath2(int index, MetaPathInfo metaPath) throws IOException
	{
		bos.write(getFormattedTypeCorrVal(index, metaPath.getTypeCorrValue()).getBytes());
		
	}
	public void writeMetaPath(int index, MetaPathInfo metaPath) throws IOException
	{
		bos.write(getFormattedMetaPath(metaPath, index).getBytes());
		
	}
	private String getFormattedTypeCorrVal(int index, double val)
	{
		String result = null;
		
		if(index == 0)
		{
			result = Double.toString(val); 
		}else
			result = "," + Double.toString(val); 
		
		return result;
	}
	private String getFormattedMetaPath(MetaPathInfo metaPath, int index)
	{
		String result = "";
		/*********************
		 * format is like as below
		 * 
		 * 1
		 * DR_DI>DI_DI>DI_TG>TG_GE
		 * NumOfCycle\t:\t1
		 * NumOfOnlyMetaPath\t:\t5
		 * NumOfMetaPath\t:\t6 
		 * NumOfOnlyLinks\t:\t3
		 * NumOfLinks\t:\t4
		 * type cor val : 0.4
		 */
	
		result = index + "\n" + metaPath.toString() + "\nNumOfCycle\t:\t"+ metaPath.getNumOfCycle() + 
				"\nNumOfMetaPath\t:\t" + metaPath.getNumOfMetaPaths() + "\nNumOfOnlyMetaPath\t:\t" + metaPath.getNumOfOnlyMetaPaths() +
				"\nNumOfLinks\t:\t" + metaPath.getNumOfLinks() + "\nNumOfOnlyLinks\t:\t" + metaPath.getNumOfOnlyLinks() + "\nTypeCorVal\t:\t" + metaPath.getTypeCorrValue() + 
				"\nRatioOfCycleOoLinks\t:\t" + ((double)(metaPath.getNumOfLinks()-metaPath.getNumOfOnlyLinks())/metaPath.getNumOfLinks())+ "\n\n";

		
		return result;
	}

	private String getFormattedMetaPathWithOrder(int index, ArrayList<RelationshipType> metaPath)
	{
		String result = "";
		/*********************
		 * format is like as below
		 * 
		 *  0,DR_DI>DI_DI>DI_TG>TG_GE
		 *  1,DR_DI>DI_DI>DI_TG>TG_SE
		 */
	
		result = index + "," + metaPath.get(0).name();
		for(int i = 1; i < metaPath.size(); i++)
		{
			result = result + ">"+ metaPath.get(i).name();
		}
	
		result = result + "\n";
		
		return result;
	}
	
	private String getFormattedMetaPathWithOrder(int index, MetaPathInfo metaPath)
	{
		String result = "";
		/*********************
		 * format is like as below
		 * 
		 *  0,DR_DI>DI_DI>DI_TG>TG_GE
		 *  1,DR_DI>DI_DI>DI_TG>TG_SE
		 */
	
		result = index + ","+ metaPath.toString() + "\n";
	
		return result;
	}
	
	public void close() throws IOException 
	{
		/***
		 * @params nothing
		 * @return nothing but write all the information on buffer into the file and close the file 
		 */ 
		
		bos.flush();
		bos.close();
	}
}
