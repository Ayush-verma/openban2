package controllers;

import play.*;
import play.libs.WS.*;
import play.libs.WS;
import scala.concurrent.Future;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import play.mvc.*;
import views.html.*;
import play.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonMethod;
import org.json.*;

import java.io.*;

import com.csvreader.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Application extends Controller {
	
	/*
	 * Home Page
	 */
    public static Result index() {
        return ok(index.render("OpenBan"));
    }
    
    /*
     * Help Page
     */
    public static Result help() {
       return ok(index.render("OpenBan"));
    }
    
    /*
     * ----To Download MeterReading and Make CSV file ServerSide------
     */
    public static Result download() {
    	Map<String,String[]> parameters = request().body().asFormUrlEncoded();
        String uuid = parameters.get("uuid")[0];
        String FileName = parameters.get("FileName")[0];
        System.out.println("uuid"+uuid+"file"+FileName);
        
        String feedUrl = "http://energy.iiitd.edu.in:9102/backend/api/data/uuid/ff48ff93-a883-5391-9977-60c7c7bca113?starttime=1407425580000&endtime=1407426180000&format=csv&tags=&timefmt=iso8601&" ;
        Promise<Result> resultPromise = WS.url(feedUrl).get().map(
                new Function<WS.Response, Result>() {
                    public Result apply(WS.Response response) {
                 	   JsonNode plz = response.asJson().findValue("Readings");
                 	   try {
                     	  FileWriter file = new FileWriter("data/"+FileName+".csv");
                     	  file.write(plz.toString());
                     	  file.close();
                          } catch (IOException e) {
                            e.printStackTrace();
                 
                          } 
                 	   return ok();
                    }
                }
        );
     	return ok();
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
    public static Map<String, Map<String,String>> maps = new HashMap<String, Map<String,String>>();
    /*--------------
     * To Send the main series data to be rendered in the HighChart 
     ----------------*/
    public static Result post() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
    	
    	Map<String,String[]> parameters1 = request().body().asFormUrlEncoded();
        System.out.print("sd");
        /*
    	int StartDate = Integer.parseInt(parameters.get("Start")[0]);
        int EndDate = Integer.parseInt(parameters.get("End")[0]);
        String DataSeries = parameters.get("dataSeries")[0];
		Map<String,String> ReqMap = new HashMap<>();
        System.out.print("sd");

        Map<String,String> data = maps.get(DataSeries);
        System.out.print("sd");
        int i = StartDate;
        /*while (i<EndDate){
        	
        	if (data.containsKey(i))
        		{System.out.println(data.get(i));}
        	i++;
         
        }
        
        
        /*Boolean Started = false;
        Set<String> set = data.keySet();
        //System.out.print(set);
        Iterator<String> it = set.iterator();

        while(it.hasNext()){
        	String currentValue= it.next(); 
        	if(currentValue.equals(StartDate)){
        		ReqMap.put(currentValue, data.get(currentValue));
                Started = true;
                System.out.print("here");
            } 
            if(currentValue.equals(EndDate) && Started){
            	ReqMap.put(currentValue, data.get(currentValue));
                Started = false;
                System.out.print("here12");
            }
			if(Started){
				  
				ReqMap.put(currentValue, data.get(currentValue));
          	}
        	
        }
        
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
	
		//System.out.print(ReqMap);
		String json ="";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(ReqMap);
		}  catch (IOException e) {
			// TODO Auto-generated catch block
   			return internalServerError("Server Error : IOException"+e.getMessage()); 

		}
			
        return ok(json);*/
    	
    	
    	
    	
    	//==========second Way
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
System.out.print("asa");
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
