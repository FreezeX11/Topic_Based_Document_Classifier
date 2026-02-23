package traitement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class Traitement {
	private File file;
	public Traitement(File file) {
		this.file=file;
	}
	
	public List<String>file_processing() throws FileNotFoundException {
		Stop_Words test=new Stop_Words();
		List<String>stopword=test.Stop_word_list();
		List<String> word_list = new ArrayList<>();
		try(Scanner scanner = new Scanner(this.file)){
			while (scanner.hasNextLine()) {
				String line=scanner.nextLine();
				String[] mots=line.trim().split("\\s+"); //[^a-zA-Z]+
				for(String mot:mots) {
					mot = mot.replaceAll("\\b\\S+@\\S+\\.\\S+\\b","");
		            mot = mot.replaceAll("[^a-zA-Z0-9]","");
					if(!mot.isEmpty()) {
						if(!stopword.contains(mot.toLowerCase())) {
							Stemming stem=new Stemming(mot);
							String mot_lemm=stem.Text_lemm();
							word_list.add(mot_lemm);	
						}	
				}
			}
		}
	}
		return word_list;
	}
}
