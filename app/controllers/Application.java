package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import play.api.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonMethod;
import org.json.*;

import java.io.*;

import com.csvreader.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Application extends Controller {
	
    public static Result index() {
        return ok(index.render("OpenBan"));
    }
    
    public static Result save() {
    	JsonNode annotatedNode = request().body().asJson();
    	String start = annotatedNode.get("start").toString();
    	String end = annotatedNode.get("end").toString();
    	
    	JsonNode namesNode=annotatedNode.findValue("names");
        Iterator<String> names= namesNode.fieldNames();
    	while(names.hasNext()){
        	String currentName = names.next();
        	JsonNode currentNameNode = annotatedNode.get(currentName);
        	Iterator<String> timeStamp = currentNameNode.fieldNames();
        	Iterator<JsonNode> aa= currentNameNode.elements();
        	if(aa.hasNext()){
        		aa.next();
        		String outputFile = "annotations/"+currentName+".csv";
        		
        		try {
        			// use FileWriter constructor that specifies open for appending
        			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
        			while (timeStamp.hasNext()){
        				csvOutput.write(timeStamp.next());
        				csvOutput.write("1");
        				//csvOutput.write(currentNameNode.get(timeStamp.next()).toString());
            			csvOutput.endRecord();
        			}
        			csvOutput.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		
        
        	}
        	
    	}
    	
    	return ok("aa");
    }
    
    public static Result show() throws JsonProcessingException, JSONException { 
    	String DirPath = "annotations/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles(); 
    	JSONObject mainObj = new JSONObject();
    	
    	for (int i = 0; i < listOfFiles.length; i++) 
    	{
    	 
    	   if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   System.out.println(files);
		    	   JSONObject jo = new JSONObject();
		    	   JSONArray ja = new JSONArray();
		    	   String csvFile = "C:\\Users\\hp\\Desktop\\play-2.2.3\\openban2\\annotations\\"+files;
		    	   //System.out.println(csvFile);
		   		   BufferedReader br = null;
		   		   String line = "";
		   		   String cvsSplitBy = ",";
		   			try {	
		   			
		   	  		br = new BufferedReader(new FileReader(csvFile));
		   			while ((line = br.readLine()) != null) {
			   	 		String[] value = line.split(cvsSplitBy);
			   	 		//System.out.println(value[0]);
			   	 		//int foo = Integer.parseInt(value[0]);
			   	 		jo.put(value[0], value[1]);
			   	 	}
		   			
		   			ja.put(jo);
		   			mainObj.put(files, jo);
		   		} catch (FileNotFoundException e) {
		   			e.printStackTrace();
		   		} catch (IOException e) {
		   			e.printStackTrace();
		   		} finally {
				   			if (br != null) {
				   				try {
				   					br.close();
				   				} catch (IOException e) {
				   					e.printStackTrace();
				   				}
				   			}
				   		}
	    	   
	    	 }
    	  }
    	//System.out.println(mainObj.toString());
    	return ok(mainObj.toString());
    }
    
    
    
    public static Result delete() {
    	
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String StartDate = parameters.get("Start")[0];
        String StopDate = parameters.get("Stop")[0];
        String FileName = parameters.get("Name")[0];
	    
        boolean Started = false;
        BufferedReader br = null;
		String line = "";
	    String cvsSplitBy = ",";
	    
	    try {
	    	
	    	CsvWriter csvOutput = new CsvWriter(new FileWriter("annotations/"+"temp.csv", false), ',');
   	  		br = new BufferedReader(new FileReader("annotations/"+FileName));
   	  		while ((line = br.readLine()) != null) {
	   	 		String[] value = line.split(cvsSplitBy);
	   	 		if (value[0].equals(StartDate)){
	   	 			System.out.println("start");
	   	 			Started = true;
	   	 		}
	   	 		if (!Started){
		   	 		csvOutput.write(value[0]);
					csvOutput.write("1");
					csvOutput.endRecord();
	   	 			//csvOutput.write(value[1]);
	   	 		}
	   	 		if (Started && value[0].equals(StopDate)){
	   	 			Started = false;
	   	 			System.out.println("stop");

	   	 		}
	   	 	}
				csvOutput.close();
				
   			
   		} catch (FileNotFoundException e) {
   			e.printStackTrace();
   		} catch (IOException e) {
   			e.printStackTrace();
   		} finally {
		   			if (br != null) {
		   				try {
		   					br.close();
		   				} catch (IOException e) {
		   					e.printStackTrace();
		   				}
		   			}
		   		}
	    
	    
	    File Filedelete = new File("annotations/"+FileName);
	    boolean delete = Filedelete.delete();
	    System.out.println(delete);
	    File oldFileName = new File("annotations/"+"temp.csv"); 
	    File newFileName = new File("annotations/"+FileName);
	    boolean rename= oldFileName.renameTo(newFileName);
	    System.out.println(rename);
	    File file = new File("annotations/"+FileName);
			if (file.length() == 0) {
			    file.delete();
			    System.out.println("here");
			} 
		
	    return ok("yo");
    }
    
    

    public static Result post() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String StartDate = parameters.get("Start")[0];
        String EndDate = parameters.get("End")[0];
        String csvFile = "C:\\Users\\hp\\Desktop\\data_meter.csv\\data_meter.csv";
		BufferedReader br = null;
		String line = "";
		boolean Started=false;
		String cvsSplitBy = ",";
		Map<String, String> maps = new HashMap<String, String>();
		
		try {	
			
	  		br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
	 		String[] value = line.split(cvsSplitBy);
	 		
	 		 if(value[0].equals(StartDate)){
	                            Started = true;
	                            
	                        } 
	                        if(value[0].equals(EndDate) && Started){
	                            maps.put(value[0], value[1]);
	                            Started = false;
	                        }
							if(Started){
								  
	                            maps.put(value[0], value[1]);
	                      	}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String json ="";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(maps);
		} /*catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */ catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
        return ok(json);
    }
}
