package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import play.api.*;

import java.util.ArrayList;
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
    
    
    /*
     * To save a new possible annotation name for given data series
     */
    public static Result saveAnnotations() {
  
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String DataSeries = parameters.get("dataSeries")[0];
        String AnnotationName = parameters.get("annotationName")[0];
    	String[] parts = DataSeries.split("\\.");
        
    	String DirPath = "data/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles(); 
    	Boolean found = false;
    	for (int i = 0; i < listOfFiles.length; i++) 
    	{
    		 if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   if (files.contains("_") ){
		    		    String[] sub = files.split("_");
				    	if (sub[0].equals(parts[0]) &&  sub[1].equals("annotation.txt")){
				    		found = true;
				    		String FileName = "data/"+files;
				    	    try{
				    	    	
					       		FileWriter fileWritter = new FileWriter(FileName,true);
					       	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					       	    bufferWritter.write(AnnotationName);
					       	    bufferWritter.newLine();
					       	    bufferWritter.close();
					    
				    	    }catch(IOException e){
					       		return internalServerError("Server Error : IOException"+e.getMessage());					       	}
				   		}
		   			
    	   		   }
	    	   
	    	 }
       }
       if (!found){
    	   File afile = new File(parts[0]+"_annotation.txt");
		try {
			 	FileWriter fw = new FileWriter(afile);
			 	BufferedWriter bw = new BufferedWriter(fw);
			 	bw.write(AnnotationName);
	       	    bw.newLine();
	       	    bw.close();
		
			} catch (IOException e) {
				return internalServerError("Server Error : IOException"+e.getMessage());
			}
      	  
      	   
       }
      return ok();
    }
    
    /*
     * To delete a given annotation name for given data series
     */
    public static Result deleteAnnotations() {
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String Name = parameters.get("annotationName")[0];
        String DataSeries = parameters.get("dataSeries")[0];
    	String[] parts = DataSeries.split("\\.");
        
        String FileName = parts[0]+"_annotation.txt";
        BufferedReader br = null;
		String line = "";
	    
	    try {
	    	
	    	FileWriter fileWritter = new FileWriter("data/"+"tempNames.txt",false);
       	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
   	  		br = new BufferedReader(new FileReader("data/"+FileName));
   	  		while ((line = br.readLine()) != null) {
	   	 		if (!line.equals(Name)){
		   	 		bufferWritter.write(line);
		       	    bufferWritter.newLine();
	   	 		}
	   	 		
	   	 	}
   	  		bufferWritter.close();
		} catch (FileNotFoundException e) {
   			return internalServerError("Server Error : FileNotFoundException "+e.getMessage()); 
   		} catch (IOException e) {
   			return internalServerError("Server Error : IOException "+e.getMessage()); 
   		} finally {
		   			if (br != null) {
		   				try {
		   					br.close();
		   				} catch (IOException e) {
		   					return internalServerError("Server Error : IOException "+e.getMessage()); 
		   				}
		   			}
		   		}
	    
	    
	    File Filedelete = new File("data/"+FileName);
	    boolean delete = Filedelete.delete();
	    //System.out.println(delete);
	    File oldFileName = new File("data/"+"tempNames.txt"); 
	    File newFileName = new File("data/"+FileName);
	    boolean rename= oldFileName.renameTo(newFileName);
	    //System.out.println(rename);
	    /*File file = new File("data/"+FileName);
			if (file.length() == 0) {
			    file.delete();
			    //System.out.println("here");
			} */
		
	    // Also delete the Annotation.csv file for that annotation
	    String DirPath = "data/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles();
    	for (int i = 0; i < listOfFiles.length; i++) {
    		if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   if (files.equals(parts[0]+"_"+Name+".csv")){
		    		   File afile = new File("data/"+files);
		    		   afile.delete();
		    	   }
    		}
    		
    	}
	    return ok();
    }
    
    
    /*
     * To send names of the available data series
     */
    public static Result data() {
    	
    	String DirPath = "data/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles();
    	JSONObject mainObj = new JSONObject();
    	 JSONArray ja = new JSONArray();
    	
    	for (int i = 0; i < listOfFiles.length; i++) 
    	{
	    	if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   if(!files.contains("_"))
		    	   {
		    		   //FilenameUtils.removeExtension(files)
		    		   ja.put(files);		    	   }
	    	}
	    	
    	}
    	try {
			mainObj.put("names", ja);
		} catch (JSONException e) {
			
			return internalServerError("Server Error : JSONException"+e.getMessage());
		}
    	
    	return ok(mainObj.toString());
    	
    }
    
    
    /*
     * To send the names of possible annotations for the data series
     */
    public static Result annotations() {
    	
    	String DirPath = "data/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles();
    	JSONObject mainObj = new JSONObject();
    	JSONArray ja = new JSONArray();
    	
    	BufferedReader br = null;
		String line = "";
    	
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String DataSeries = parameters.get("dataSeries")[0];
        
    	for (int i = 0; i < listOfFiles.length; i++) 
    	{
	    	if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   if(files.contains("_"))
		    	   {
		    		   String[] parts = files.split("_");
		    		   String part1 = parts[0]+".csv";
		    		   if (parts.length == 2 && part1.equals(DataSeries) && parts[1].equals("annotation.txt")){
		    			   
		    			   try {	
		   		   			
				   		   	  		br = new BufferedReader(new FileReader("data/"+files));
				   		   			while ((line = br.readLine()) != null) {
				   			   	 		ja.put(line);
				   			   	 	}
				   		   			
				   		   			
				   		   		} catch (FileNotFoundException e) {
				   					return internalServerError("Server Error : FileNotFoundException"+e.getMessage());

				   		   		} catch (IOException e) {
				   		   			return internalServerError("Server Error : IOException"+e.getMessage()); 
				   		   		} finally {
				   				   			if (br != null) {
				   				   				try {
				   				   					br.close();
				   				   				} catch (IOException e) {
				   				   				return internalServerError("Server Error : IOException"+e.getMessage()); 
				   				   				}
				   				   			}
				   				   		}
		    			   
		    		   }
		       	   }
	    	}
	    	
    	}
    	try {
    		mainObj.put("annotationNames", ja);
		} catch (JSONException e) {
			return internalServerError("Server Error : JSONException"+e.getMessage()); 
		}
    	
    	return ok(mainObj.toString());
    	
    }
    
    
    /*
     *---Saving Annotation---
     * To save data of the given annotation name for the data series
     */
    public static Result save() {
    	JsonNode annotatedNode = request().body().asJson();
    	String start = annotatedNode.get("start").toString();
    	String end = annotatedNode.get("end").toString();
 
    	String dataSeries = annotatedNode.get("dataSeriesName").toString();
    	String dataSeriesName = dataSeries.substring(1, dataSeries.length()-1);
    	String[] parts = dataSeriesName.split("\\.");
    	
    	JsonNode namesNode=annotatedNode.findValue("names");
        Iterator<String> names= namesNode.fieldNames();
    	
        while(names.hasNext()){
        	String currentName = names.next();
        	JsonNode currentNameNode = annotatedNode.get(currentName);
        	
        	Iterator<String> timeStamp = currentNameNode.fieldNames();
        	Iterator<JsonNode> aa= currentNameNode.elements();
        	
        	if(aa.hasNext()){
        		aa.next();
        		String outputFile = "data/"+parts[0]+"_"+currentName+".csv";
        		
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
        			return internalServerError("Server Error : IOException"+e.getMessage()); 
        		}
        		
        
        	}
        	
    	}
    	
    	return ok("");
    }
    
    /*
     *---Showing Annotation---
     * To show all the saved annotations for the data series
     */
    public static Result show() throws JsonProcessingException, JSONException { 
    	
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String DataSeries = parameters.get("dataSeries")[0];
        
    	String[] parts = DataSeries.split("\\.");
        
    	String DirPath = "data/";
    	String files;
    	File folder = new File(DirPath);
    	File[] listOfFiles = folder.listFiles(); 
    	JSONObject mainObj = new JSONObject();
    	
    	for (int i = 0; i < listOfFiles.length; i++) 
    	{
    	 
    	   if (listOfFiles[i].isFile()) {
		    	   files = listOfFiles[i].getName();
		    	   
		    	   
		    	   if (files.contains("_") ){
		    		   
				    	String[] sub = files.split("_");
				    	
				    	if (sub[0].equals(parts[0]) &&  !(sub[1].equals("annotation.txt"))){
				    	
				    		System.out.println("as;;; "+files);
				    	   JSONObject jo = new JSONObject();
				    	   JSONArray ja = new JSONArray();
				    	   String csvFile = "data/"+files;
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
					   			return internalServerError("Server Error : FileNotFoundException"+e.getMessage()); 
					   		} catch (IOException e) {
					   			return internalServerError("Server Error : IOException"+e.getMessage()); 
					   		} finally {
							   			if (br != null) {
							   				try {
							   					br.close();
							   				} catch (IOException e) {
							   					return internalServerError("Server Error : IOException"+e.getMessage()); 
							    			}
							   		}
							   }
		    	     }
		   			
    	   		}
	    	   
	    	 }
    	  }
    	//System.out.println(mainObj.toString());
    	return ok(mainObj.toString());
    }
    
    
    /*
     *---Deleting Annotation---
     * To delete a particular data range of a given annotation (given data series)
     */
    public static Result delete() {
    	
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String StartDate = parameters.get("Start")[0];
        String StopDate = parameters.get("Stop")[0];
        
        String Name = parameters.get("Name")[0];
        String DataSeries = parameters.get("dataSeries")[0];
    	String[] parts = DataSeries.split("\\.");
        
        String FileName = parts[0]+"_"+Name+".csv";
        
        boolean Started = false;
        BufferedReader br = null;
		String line = "";
	    String cvsSplitBy = ",";
	    
	    try {
	    	
	    	CsvWriter csvOutput = new CsvWriter(new FileWriter("data/"+"temp.csv", false), ',');
   	  		br = new BufferedReader(new FileReader("data/"+FileName));
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
   			return internalServerError("Server Error : FileNotFoundException"+e.getMessage()); 
   		} catch (IOException e) {
   			return internalServerError("Server Error : IOException"+e.getMessage()); 
   		} finally {
		   			if (br != null) {
		   				try {
		   					br.close();
		   				} catch (IOException e) {
		   					return internalServerError("Server Error : IOException"+e.getMessage()); 
		   				}
		   			}
		   		}
	    
	    
	    File Filedelete = new File("data/"+FileName);
	    boolean delete = Filedelete.delete();
	    System.out.println(delete);
	    File oldFileName = new File("data/"+"temp.csv"); 
	    File newFileName = new File("data/"+FileName);
	    boolean rename= oldFileName.renameTo(newFileName);
	    System.out.println(rename);
	    File file = new File("data/"+FileName);
			if (file.length() == 0) {
			    file.delete();
			    //System.out.println("here");
			} 
		
	    return ok();
    }
    public static Map<String, String> maps = new HashMap<String, String>();
    /*--------------
     * To Send the main series data to be rendered in the HighChart 
     ----------------*/
    public static Result post() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String StartDate = parameters.get("Start")[0];
        String EndDate = parameters.get("End")[0];
        String DataSeries = parameters.get("dataSeries")[0];
        
        String csvFile = "data/"+DataSeries;
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
			System.out.println("sdfsdf"+DataSeries);
   			return internalServerError("Server Error : FileNotFoundException"+e.getMessage()); 

		} catch (IOException e) {
   			return internalServerError("Server Error : IOException"+e.getMessage()); 

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
		   			return internalServerError("Server Error : IOException"+e.getMessage()); 

				}
			}
		}
		
		String json ="";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(maps);
		}  catch (IOException e) {
			// TODO Auto-generated catch block
   			return internalServerError("Server Error : IOException"+e.getMessage()); 

		}
     
        return ok(json);
    }
}
