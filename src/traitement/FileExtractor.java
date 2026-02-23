package traitement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileExtractor {
	private String data_path;
	public FileExtractor(String data_path) {
		this.data_path=data_path;
	}
	
	public void Extract(String destination) {
		File[] Folders=new File(this.data_path).listFiles();
		for(File folder:Folders) {
			if(folder.isDirectory()) {
				File[] files=new File(this.data_path+"\\"+folder.getName()).listFiles();
				try {
					for(File file : files) {
					    Files.copy(file.toPath(),
					        (new File(destination + file.getName()+"__"+folder.getName().replace(".", "_"))).toPath(),
					        StandardCopyOption.REPLACE_EXISTING);
					}	
				}catch(IOException e){
					e.printStackTrace();	
				}
				
			}
		}
		System.out.println("Extraction Fisnish...");
	}
	
	public void delele(String path) {
		File[] Files=new File(path).listFiles();
			for(File file:Files) {
				if(file.isFile()) {
					file.delete();
				}
			}
		System.out.println("Finish");
		}
	
	public static void main(String args[]) {
		String path="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\Data";
		String destination="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\crossvalidation\\";
		FileExtractor test=new FileExtractor(path);
		test.delele(destination);
		//test.Extract(destination);
	}
}
