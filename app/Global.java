
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import play.*;



public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
	  //boolean Started=false;
			/*BufferedReader br = null;
			String line = "";
			
			String DirPath = "data/";
	    	String files;
	    	File folder = new File(DirPath);
	    	File[] listOfFiles = folder.listFiles(); 
			String cvsSplitBy = ",";

	    	for (int i = 0; i < listOfFiles.length; i++) 
	    	{
	    	 
	    	   if (listOfFiles[i].isFile()) {
			    	   files = listOfFiles[i].getName();
			    	   
			    	   
			    	   if (!files.contains("_") ){
			    		   try {	
			    				Map<String,String> innerMaps = new HashMap<>();
			    		  		br = new BufferedReader(new FileReader("data/"+files));
			    				while ((line = br.readLine()) != null) {
				    		 		String[] value = line.split(cvsSplitBy);
				    		 		innerMaps.put(value[0], value[1]);
			    		        }
			    				controllers.Application.maps.put(files, innerMaps);
			    			} catch (FileNotFoundException e) {
			    				System.out.print("pppppppp");

			    			} catch (IOException e) {
			    				System.out.print("ppp12");
			    			} finally {
			    				if (br != null) {
			    					try {
			    						br.close();
			    					} catch (IOException e) {
			    						System.out.print("ppmmpppp1221pp");
			    					}
			    				}
			    			}
			    		   
			    		   
					    	
			    	   }
	    	   }
	    	}
			
			*/
		 
			System.out.println("Done");
		  }
  @Override
  public void onStop(Application app) {
    Logger.info("Application shutdown...");
  }
		
  }  
  
  
    


