package traitement;
import java.io.File;
import java.util.*;

public class Bayesian_probability {
	private Map<String,List<String>> data;
	public Bayesian_probability(Map<String,List<String>> data) {
		this.data=data;
	}
	
	public List<String>unique_word(){
		Set<String> motsUniques = new HashSet<>();
		for (List<String> listeDeMots : this.data.values()) {
		    motsUniques.addAll(listeDeMots);
		}
		List<String> motsuniques=new ArrayList<>(motsUniques);
		return motsuniques;
	}
	
	public static Map<String,Double> probability_per_class(List<File>train_data){
		Map<String,List<File>> class_file=new HashMap<>();
		Map<String,Double>class_probability=new HashMap<>();
		for(File file:train_data) {
			String class_name=file.getName().split("__")[1].replace("_",".");
			class_file.computeIfAbsent(class_name,k->new ArrayList<>()).add(file);
		}
		for(String className:class_file.keySet()) {
			class_probability.put(className, (double)class_file.get(className).size()/train_data.size());
		}
		return class_probability;
	}
	
	public static Map<String,Double> Probabily_Per_class(){
		String chemin="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\Data";
		Map<String,Double> class_probability=new HashMap<>();
		Map<String,Integer> file_number=new HashMap<>();
		int file_numbers=0;
		File[] folders=new File(chemin).listFiles();
		for(File folder:folders) {
			File[] files=new File(chemin+"\\"+folder.getName()).listFiles();
			file_number.put(folder.getName(),files.length);
		}
		
		for(Integer value:file_number.values()) {
			file_numbers+=value;
		}
		
		for(String folder_name:file_number.keySet()){
			Double class_proba=(double)file_number.get(folder_name)/file_numbers;
			class_probability.put(folder_name,class_proba);
		}
		//Serialisation.save_class_probability(class_probability);
		return class_probability;
	}
	
	public Map<String,Map<String,Integer>> unique_word_occurence(List<String> motsUniques){
		Map<String,Map<String,Integer>> occurence_per_class=new HashMap<>();
		for(String folder:this.data.keySet()) {
			Map<String,Integer> occurence=new HashMap<>();
			for(String mot:motsUniques){
				int frequence=Collections.frequency(this.data.get(folder), mot);
				occurence.put(mot,frequence);
			}
			occurence_per_class.put(folder, occurence);	
		}
		return occurence_per_class;	
	}
	
	public Map<String,Map<String,Double>> probability(){
		Map<String,Map<String,Double>> probability=new HashMap<>();
		List<String> motsUniques=this.unique_word();
		int m=motsUniques.size();
		Map<String,Map<String,Integer>>unique_words_occurence=this.unique_word_occurence(motsUniques);
		for(String folder:this.data.keySet()){
			Map<String,Double> word_proba=new HashMap<>();
			Map<String,Integer>words_occurence=unique_words_occurence.get(folder);
			for(String mot:motsUniques) {
				Double proba=(double)(words_occurence.get(mot)+1)/(this.data.get(folder).size()+m);
				word_proba.put(mot, proba);
			}
			probability.put(folder,word_proba);
		}
		return probability;
	}
}
