package dbl.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.RelationshipType;

import dbl.variable.MetaPathInfo;
import dbl.variable.RelationManager;

public class FileLineReader {
	private BufferedReader br = null;
	
	public FileLineReader(String fileName) throws FileNotFoundException 
	{
		br = new BufferedReader(new FileReader(fileName));
	}
	
	public ArrayList<ArrayList<RelationshipType>> getMetaPathWithOrder() throws IOException
	{
		ArrayList<ArrayList<RelationshipType>> metaPaths = new ArrayList<>();
		
		String line = br.readLine();
		
		while(line != null)
		{
			String[] tokens = line.split(",");
			int i = Integer.parseInt(tokens[0]);
			String[] relTypes = tokens[1].split(">");
			ArrayList<RelationshipType> metaPath = new ArrayList<>();
			
			for(int j = 0; j < relTypes.length; j++)
			{
				metaPath.add(RelationManager.RelTypes.valueOf(relTypes[j]));
			}

			metaPaths.add(i, metaPath);
			line = br.readLine();
		}
		
		br.close();
		return metaPaths;
	}

	public Long[] getPairs() throws IOException
	{
		String line = br.readLine();
		Long[] pair = new Long[2];
		
		if(line != null)
		{
			String[] tokens = line.split(",");
			pair[0] = Long.parseLong(tokens[0]);
			pair[1] = Long.parseLong(tokens[1]);
			return pair;
		}
	
		return null;
	}
	
}
