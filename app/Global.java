import play.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import controllers.Application;


public class Global extends GlobalSettings{
	
	
	public void onStart(Application app) {
	   
	    //boolean Started=false;
		String csvFile = "C:\\Users\\hp\\Desktop\\data_meter.csv\\data_meter.csv";
		BufferedReader br = null;
		String line = "";

		String cvsSplitBy = ",";
		Map<String, String> maps = new HashMap<String, String>();
		try {
	 
	 		br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
	 
				// use comma as separator
				String[] value = line.split(cvsSplitBy);
				maps.put(value[0], value[1]);
	                        /* if(value[0].equals(From)){
	                            Started = true;
	                        } 
	                        if(value[0].equals(To) && Started){
	                            maps.put(value[0], value[1]);
	                            Started = false;
	                        }
							if(Started){
	                            maps.put(value[0], value[1]);
	                      	}*/
	 
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
	 
		System.out.println("Done");
	  }
	
		
		
		
	    
}
