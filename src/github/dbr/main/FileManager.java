package github.dbr.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

	public static LogData loadCSVFile(File file) {
		LogData data = null;
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			boolean firstLine = true;
			
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                
                if(firstLine) {
                	data = new LogData(values);
                } else {
                	data.addData(values);
                }
                
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return data;
	}
	
}
