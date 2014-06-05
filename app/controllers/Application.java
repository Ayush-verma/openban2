package controllers;

import play.*;
import play.mvc.*;

import java.util.HashMap;
import java.util.Map;

import play.api.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.lang.reflect.*;

//import play.libs.Json;
import views.html.*;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;


public class Application extends Controller {

    public static Result index() {
        return ok(index.render("OpenBan"));
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
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       // Global a = new Global();
       //Class aClass = Class.forName("global");
       //Field field = aClass.getField("maps");
       
    
        return ok(json);
    }

	//public static void main(String[] args) {
		//import org.codehaus.jackson.JsonNode;
		
	//}
    
    /*public static Result try() {
    	return ok(index.render("openban"));
    }*/
}
