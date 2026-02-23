package traitement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Stop_Words {
private String stop_word_file;
	
	public Stop_Words(){
		this.stop_word_file="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\stopwords.txt";
	}
	
	public List<String> Stop_word_list(){
		File fichier=new File(this.stop_word_file);
		//words list
		List<String> mots = new ArrayList<String>();
		
		try (Scanner scanner = new Scanner(fichier)) {
            while (scanner.hasNext()) {
                mots.add(scanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		
		return mots;
	}
}
