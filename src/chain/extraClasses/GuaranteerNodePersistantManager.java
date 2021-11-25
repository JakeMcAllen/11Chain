package chain.extraClasses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import chain.component.Block;
import chain.component.info.NodeInfo;
import chain.component.info.UsersInfo;
import chain.component.info.infoClasses;
import chain.main.Guaranteer;

public class GuaranteerNodePersistantManager {

	private String pathFiles = "";
	private final String DELIMITER = ";";
	private org.json.simple.JSONObject jsonObject; 



	public GuaranteerNodePersistantManager(String path) {
		this.pathFiles = path;
	}



	public void checkFile(String pth) {
		File f = new File( this.pathFiles + pth );
		if (!f.exists()) {
			try {
				Path path = Paths.get( this.pathFiles + pth );
				Files.createDirectories( path.getParent());
				Files.createFile( path );
			} catch (IOException e) { e.printStackTrace(); }
		}
	}


	public void saveTxTFile( JSONObject jo, String fileName ) 
			throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("OBJ", jo );

		FileWriter file = new FileWriter(this.pathFiles + fileName);
		file.write(jsonObject.toString());
		file.close();
	}

	public <T extends infoClasses> void saveCSVFile ( List<T> list, String header, String fileName ) throws IOException {

		BufferedWriter writer = Files.newBufferedWriter(Paths.get( this.pathFiles + fileName ));

		writer.write(header);
		writer.newLine();		
		
		for (T record : list) {
			writer.write( record.toCSV( this.DELIMITER ) );
			writer.newLine();
		}

		writer.close();

	}


	public void saveCSVFile ( HashMap<String, Block> list, String header, String fileName ) throws IOException {

		BufferedWriter writer = Files.newBufferedWriter(Paths.get( this.pathFiles + fileName ));

		writer.write( "key,".concat(header) );
		writer.newLine();

		// write
		for (Entry<String, Block> record : list.entrySet() ) {
			writer.write( record.getKey() + this.DELIMITER + record.getValue().toCSV(this.DELIMITER) );
			writer.newLine();
		}

		writer.close();

	}


	public void saveChain(Block block, String fileName) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get( this.pathFiles + fileName ));

		writer.write( Block.listGlobalVariablesForPersistance );
		writer.newLine();
		Block pBlock = null;
		
		int count = 0;
		
		// write
		do {
			writer.write( block.toCSV(this.DELIMITER) );
			writer.newLine();
			
			pBlock = block;
			block = block.getNextBlock();
		
			count++;
		} while ( pBlock.hasNextBlock() );

		System.out.println("cooutn: " + count);
		
//		writer.write( block.toCSV(this.DELIMITER) );
//		writer.newLine();
//		block = block.getNextBlock();
		
		writer.close();

	}

	
	
	
	
	
	
	
	
	/*
	 * 
	 * 	READ
	 * 
	 */

	public HashMap<String, Block> CSVReaderHashMap ( String fileName ) throws IOException {

		HashMap<String, Block> lb = new HashMap<String, Block> ();
	    BufferedReader br = Files.newBufferedReader(Paths.get( this.pathFiles + fileName ));

	    String line = br.readLine();
	    while ((line = br.readLine()) != null) 
	    {
	    	String[] ln = line.split(this.DELIMITER);
	    	lb.put(ln[0], Block.fromCSV( Arrays.copyOfRange(ln, 1, ln.length) ) );
	    }

	    br.close();
		return lb;
	}

	
	public List<NodeInfo> CSVReaderListNodeInfo ( String fileName ) throws IOException {

		List<NodeInfo> lb = new ArrayList<NodeInfo> ();
	    BufferedReader br = Files.newBufferedReader(Paths.get( this.pathFiles + fileName ));

	    String line = br.readLine();
	    while ((line = br.readLine()) != null) 
	    {
	    	lb.add( NodeInfo.fromCSV( line.split(this.DELIMITER) ) );
	    }

	    br.close();
		return lb;
	}

	public List<UsersInfo> CSVReaderListUsersInfo ( String fileName ) throws IOException {

		List<UsersInfo> lb = new ArrayList<UsersInfo> ();
	    BufferedReader br = Files.newBufferedReader(Paths.get( this.pathFiles + fileName ));

	    String line = br.readLine();
	    while ((line = br.readLine()) != null) 
	    {
	    	lb.add( UsersInfo.fromCSV( line.split(this.DELIMITER) ) );
	    }

	    br.close();
		return lb;
	}



	

	public org.json.simple.JSONObject JSONReadStringFromFile(String GEXT_F) throws FileNotFoundException, IOException, ParseException {
		if (this.jsonObject == null) {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader( this.pathFiles + GEXT_F ));
			this.jsonObject = (org.json.simple.JSONObject) obj;
		}
		System.out.println("jsonObject " + jsonObject.get("OBJ"));
		
		return (org.json.simple.JSONObject) jsonObject.get("OBJ");
	}
	public String JSONReadFromFile(String key, String GEXT_F) throws FileNotFoundException, IOException, ParseException {
		return JSONReadStringFromFile(GEXT_F).get(key).toString();
	}
	
	public JSONArray JSONReadStringFromFileJA(String key, String GEXT_F) throws JSONException, FileNotFoundException, IOException, ParseException {
		return new JSONArray( JSONReadStringFromFile(GEXT_F).get(key).toString() );
	}

	
	
	
	public void loadChain(Guaranteer guaranteer, String fileName, Block head, Block tail) throws IOException {
		Block b = null;
		
	    BufferedReader br = Files.newBufferedReader(Paths.get( this.pathFiles + fileName ));

	    String line = br.readLine();
	    while ((line = br.readLine()) != null) 
	    {
	    	String[] ln = line.split(this.DELIMITER);
	    	Block currentBlock = Block.fromCSV( ln );
	    	
	    	guaranteer.updateTailOnPersistance( currentBlock );
	    }
	    
	    
	    
	    
	    br.close();
	}






}
