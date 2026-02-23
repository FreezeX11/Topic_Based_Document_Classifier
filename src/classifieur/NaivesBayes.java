package classifieur;
import traitement.Serialisation;
import traitement.Traitement;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import traitement.Fichier;
import traitement.Bayesian_probability;
public class NaivesBayes implements Classifieur {
	private Map<String,Double> class_probability;
	private Map<String,Map<String,Double>> probability;
	
	public NaivesBayes() {
		this.class_probability=Serialisation.load_prob_class("C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\corpus\\SANGARE_Boubacar_Diam_Proba_class.xml");
		this.probability=Serialisation.load_corpus("C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\corpus\\SANGARE_Boubacar_Diam_Proba.xml");
	}
	
	public NaivesBayes(Map<String,Double> class_probability,Map<String,Map<String,Double>> probability) {
		this.class_probability=class_probability;
		this.probability=probability;
	}
	
	public void Train(List<File>train_data){
		Map<String,List<String>>word_by_fold=Fichier.word_by_class(train_data);
		Map<String,Map<String,Double>>probability=new Bayesian_probability(word_by_fold).probability();
		this.probability=probability;
		this.class_probability=Bayesian_probability.probability_per_class(train_data);
	}
	
	public String compute_probability(List<String>input){
		Double Max_prob=0.0;
		String classe="";
		for(String folder:probability.keySet()) {
			Double proba_class=this.class_probability.get(folder);
			Map<String,Double> word_prob=this.probability.get(folder);
			for(String word:input) {
				if(word_prob.containsKey(word)) {
					proba_class*=word_prob.get(word);
				}	
			}
			if(proba_class>=Max_prob){
				Max_prob=proba_class;
				classe=folder;
			}
		}
		return classe;
	}
	
	public Map<String,Set<File>> predict_class(List<File> files) throws FileNotFoundException{
		Map<String,Set<File>>prediction=new HashMap<>();
		for(File file:files) {
			Traitement process=new Traitement(file);
			List<String> word_list=process.file_processing();
			String class_predict=this.compute_probability(word_list);
			prediction.computeIfAbsent(class_predict,k->new HashSet<>()).add(file);
		}
		return prediction;	
	}
}
