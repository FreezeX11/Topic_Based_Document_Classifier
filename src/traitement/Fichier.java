package traitement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Fichier {
	private String chemin;
	
	public Fichier() {
		this.chemin="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\Data";
	}
	
	public Map<String,List<String>> word_by_classe(){
		Stop_Words test=new Stop_Words();
		List<String>stopword=test.Stop_word_list();
		Map<String,List<String>> Folders_word=new HashMap<>();
		File[] Folders=new File(this.chemin).listFiles();
		for(File folder: Folders) {
			if(folder.isDirectory()) {
				File[] files=new File(this.chemin+"\\"+folder.getName()).listFiles();
				List<String> folder_word=new ArrayList<>();
				for(File file: files) {
					try(Scanner scanner = new Scanner(file)){
						while (scanner.hasNextLine()) {
							String line=scanner.nextLine();
							String[] mots=line.trim().split("\\s+"); //[^a-zA-Z]+
							for(String mot:mots) {
								mot = mot.replaceAll("\\b\\S+@\\S+\\.\\S+\\b", "");
					            mot = mot.replaceAll("[^a-zA-Z0-9]", "");
								if(!mot.isEmpty()) {
									if(!stopword.contains(mot.toLowerCase())) {
										Stemming stem=new Stemming(mot);
										String mot_lemm=stem.Text_lemm();
										folder_word.add(mot_lemm);	
						            }			
							}
						}
					}
					}catch(FileNotFoundException e) {
						e.printStackTrace();
					}
							
				}
				Folders_word.put(folder.getName(),folder_word);
			}	
		}
		return Folders_word;
	}
	
	public static Map<String,List<String>> word_by_class(List<File>train_data){
		Stop_Words test=new Stop_Words();
		List<String>stopword=test.Stop_word_list();
		Map<String,List<String>>class_word=new HashMap<>();
		for(File file:train_data) {
			String className=file.getName().split("__")[1].replace("_",".");
			try(Scanner scanner=new Scanner(file)){
				while(scanner.hasNextLine()) {
					String line=scanner.nextLine();
					String[] mots=line.trim().split("\\s+");
					for(String mot:mots) {
						mot = mot.replaceAll("\\b\\S+@\\S+\\.\\S+\\b", "");
			            mot = mot.replaceAll("[^a-zA-Z0-9]", "");
						if(!mot.isEmpty()) {
							if(!stopword.contains(mot.toLowerCase())) {
								Stemming stem=new Stemming(mot);
								String mot_lemm=stem.Text_lemm();
								class_word.computeIfAbsent(className, k->new ArrayList<>()).add(mot_lemm);
				            }			
					}
				}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return class_word;
	}
	
	public static Map<File, List<String>> Map_Creation(List<File>train_data){
		Map<File, List<String>> result_map=new HashMap<>();
		Stop_Words test=new Stop_Words();
		List<String>stopword=test.Stop_word_list();
		for(File file: train_data) {
			try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                	String line=scanner.nextLine();
					String[] mots=line.trim().split("\\s+");
					for(String mot:mots) {
						mot = mot.replaceAll("\\b\\S+@\\S+\\.\\S+\\b", "");
			            mot = mot.replaceAll("[^a-zA-Z0-9]", "");
						if(!mot.isEmpty()) {
							if(!stopword.contains(mot.toLowerCase())) {
								Stemming stem=new Stemming(mot);
								String mot_lemm=stem.Text_lemm();
								result_map.computeIfAbsent(file, k->new ArrayList<>()).add(mot_lemm);
				            }			
					}
				}
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
		}
		return result_map;	
	}
}
