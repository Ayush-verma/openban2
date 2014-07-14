
import java.io.BufferedReader;
import java.io.File;

import play.*;



public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
	  //boolean Started=false;
			String csvFile = "C:\\Users\\hp\\Desktop\\data_meter.csv\\data_meter.csv";
			BufferedReader br = null;
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
			    		   System.out.println(files);
			    		   
			    		   
					    	
			    	   }
	    	   }
	    	}
			
			
		 
			System.out.println("Done");
		  }
		
  }  
  
  
    


