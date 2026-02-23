package classifieur;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import traitement.Fichier;
import traitement.Traitement;

public class KNeighbours implements Classifieur{
	private Map<File,Map<String,Integer>>Occurence_map;
	private Map<File,Map<String,Double>>Tf_idf;
	private int K;
	public KNeighbours(Map<File,Map<String,Integer>>Occurence_map,int K) {
		this.Occurence_map=Occurence_map;
		this.K=K;
	}
	
	public KNeighbours(int K) {
		this.Tf_idf=new HashMap<>();
		this.K=K;
	}
	
	public Map<File, Map<String, Integer>> Occurence_Map(Map<File,List<String>>data) {
		Map<File, Map<String, Integer>> Final=new HashMap<>();
		for (Map.Entry<File, List<String>> entry : data.entrySet()) {
			Map<String, Integer> Occurence_words=new HashMap<>();
	        File file = entry.getKey();
	        List<String> mots = entry.getValue();
	        Set<String> motsUniques = new HashSet<>(mots);
	        for(String mot: motsUniques) {
	        	int word_occ=Collections.frequency(mots, mot);
	        	Occurence_words.put(mot,word_occ);	
	        } 
	        Final.put(file, Occurence_words);
	    }
		return Final;	
	}
	
	public Map<String,Integer> File_Word_Occurence(List<String>listeMots){
		Map<String,Integer> Occurence=new HashMap<>();
		Set<String> motsUniques = new HashSet<>(listeMots);
		for(String mot: motsUniques) {
			int word_occ=Collections.frequency(listeMots, mot);
			Occurence.put(mot,word_occ);
		}
		return Occurence;
	}
	
	public Map<String,Double> File_tf_idf(List<String>listeMots){
		Map<String,Double> Occurence=new HashMap<>();
		Set<String> motsUniques = new HashSet<>(listeMots);
		for(String mot: motsUniques) {
			int word_occ=Collections.frequency(listeMots, mot);
			Occurence.put(mot,(double)word_occ/listeMots.size());
		}
		return Occurence;
	}
	
	public double euclidien_distance(Map<String,Integer>file,Map<String,Integer>Corpus_file) {
		double distance=0.0;
		for(String mot:file.keySet()) {
			if(Corpus_file.containsKey(mot)) {
				distance+=Math.pow((file.get(mot)-Corpus_file.get(mot)),2);
			}
		}
		return Math.sqrt(distance);
	}
	
	public Map<File,Map<String, Double>> TF(Map<File,List<String>>data) {
		Map<File, Map<String, Double>> Final=new HashMap<>();
		for (Map.Entry<File, List<String>> entry :data.entrySet()) {
			Map<String, Double> TF_words=new HashMap<>();
	        File nomFichier = entry.getKey();
	        List<String> mots = entry.getValue();
	        Set<String> motsUniques = new HashSet<>(mots);  
	        for(String mot: motsUniques) {
	        	double tf=Collections.frequency(mots, mot);
	        	double ratio=tf/mots.size();
	        	TF_words.put(mot,ratio);	
	        } 
	        Final.put(nomFichier, TF_words);
	    }
		return Final;	
	}
	
	public Map<File,Map<String, Double>>  IDF(Map<File,List<String>>data){
		Map<File, Map<String, Double>> TF_w=this.TF(data);
		Map<File, Map<String, Double>> IDF_w=new HashMap<>();
		int total_document=TF_w.size();
		for (File nomFichier : TF_w.keySet()) {
			Map<String, Double> tfMap = TF_w.get(nomFichier);
			Map<String, Double> idf_values= new HashMap<>();
			for (String terme : tfMap.keySet()) {
	            double documentFrequency = 0;
	            for (Map<String, Double> map : TF_w.values()) {
	                if (map.containsKey(terme)) {
	                    documentFrequency++;
	                }
	            }
	            double idf = Math.log(total_document / (documentFrequency + 1));
	            idf_values.put(terme, idf);  
		}
			IDF_w.put(nomFichier, idf_values);	
	}
	
	return IDF_w;
}
	
	public Map<File,Map<String, Double>> TF_IDF(Map<File,List<String>>data){
		Map<File, Map<String, Double>> TF_w=this.TF(data);
		Map<File, Map<String, Double>> IDF_w=this.IDF(data);
		Map<File, Map<String, Double>> TF_IDF=new HashMap<>();
		double epsilon=0;
		for(File file_name: TF_w.keySet()) {
			Map<String, Double> tfMap = TF_w.get(file_name);
			Map<String, Double> idfMap = IDF_w.get(file_name);
			Map<String, Double> tF_IDF= new HashMap<>();
			for(String terme: tfMap.keySet()) {
				double tfidf = tfMap.get(terme) * idfMap.getOrDefault(terme, 0.0);
				if (tfidf >= epsilon) {
					tF_IDF.put(terme, tfidf);
	            }
			}
			if (!tF_IDF.isEmpty()) {
				TF_IDF.put(file_name, tF_IDF);
	        }
		}
		return TF_IDF;		
}
	
	public double cosinus(Map<String,Double> file,Map<String,Double> Corpus_file) {
		Double norme_querry=0.0;
		Double norme_Corpus_file=0.0;
		Double scalaire=0.0;
		for(String mot: file.keySet()){
			if(Corpus_file.containsKey(mot)) {
				scalaire+=file.get(mot)*Corpus_file.getOrDefault(mot,0.0);		
			}
			norme_querry+=Math.pow(file.get(mot), 2);	
		}
		for(String mot: Corpus_file.keySet()) {
			norme_Corpus_file+=Math.pow(Corpus_file.get(mot), 2);
		}
		if(norme_querry==0.0 || norme_Corpus_file==0.0) {
			return 0.0;
		}
		return scalaire/(Math.sqrt(norme_querry)+Math.sqrt(norme_Corpus_file));
	}
	
	public String check_distance(Map<String,Integer>file_word_occurence) {
		HashMap<File,Double> result=new HashMap<>();
		for(File file_name: this.Occurence_map.keySet()){
			Map<String,Integer> map=this.Occurence_map.get(file_name);
			Double distance_value=this.euclidien_distance(file_word_occurence, map);
			result.put(file_name, distance_value);
		}
		Map<File, Double> newMap = result.entrySet().stream()
		        .sorted(Map.Entry.<File, Double>comparingByValue().reversed())
		        .limit(this.K)
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		Map<String,Double>result_Map=new HashMap<>();
		for(File file:newMap.keySet()) {
			String class_name=file.getName().split("__")[1].replace("_",".");
			if(result_Map.containsKey(class_name)) {
				result_Map.put(class_name, result_Map.get(class_name)+newMap.get(file));
			}else {
				result_Map.put(class_name,newMap.get(file));
			}
		}
		String className=Collections.max(result_Map.entrySet(), Map.Entry.comparingByValue()).getKey();
		return className;
	}
	
	public String check_distance2(Map<String,Double>file_tfid) {
		HashMap<File,Double> result=new HashMap<>();
		for(File file_name: this.Tf_idf.keySet()){
			Map<String,Double> map=this.Tf_idf.get(file_name);
			Double distance_value=this.cosinus(file_tfid, map);
			result.put(file_name, distance_value);
		}
		Map<File, Double> newMap = result.entrySet().stream()
		        .sorted(Map.Entry.<File, Double>comparingByValue().reversed())
		        .limit(this.K)
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		Map<String,Double>result_Map=new HashMap<>();
		for(File file:newMap.keySet()) {
			String class_name=file.getName().split("__")[1].replace("_",".");
			if(result_Map.containsKey(class_name)) {
				result_Map.put(class_name, result_Map.get(class_name)+newMap.get(file));
			}else {
				result_Map.put(class_name,newMap.get(file));
			}
		}
		String className=Collections.max(result_Map.entrySet(), Map.Entry.comparingByValue()).getKey();
		return className;
	}
	
//	public void Train(List<File>train_data) {
//		Map<File, List<String>>File_Word=Fichier.Map_Creation(train_data);
//		this.Occurence_map=this.Occurence_Map(File_Word);	
//	}
	
	public void Train(List<File>train_data) {
		Map<File, List<String>>File_Word=Fichier.Map_Creation(train_data);
		this.Tf_idf=this.TF_IDF(File_Word);	
	}
	
	public Map<String,Set<File>>predict_class(List<File>files) throws FileNotFoundException{
		Map<String,Set<File>>prediction=new HashMap<>();
		for(File file:files){
			Traitement process=new Traitement(file);
			List<String> word_list=process.file_processing();
			Map<String,Double>file_word_tfidf=this.File_tf_idf(word_list);
			String class_predict=this.check_distance2(file_word_tfidf);
			prediction.computeIfAbsent(class_predict,k->new HashSet<>()).add(file);
		}
		return prediction;	
	}
	
//	public Map<String,Set<File>>predict_class(List<File>files) throws FileNotFoundException{
//		Map<String,Set<File>>prediction=new HashMap<>();
//		for(File file:files){
//			Traitement process=new Traitement(file);
//			List<String> word_list=process.file_processing();
//			Map<String,Integer>file_word_occurence=this.File_Word_Occurence(word_list);
//			String class_predict=this.check_distance(file_word_occurence);
//			prediction.computeIfAbsent(class_predict,k->new HashSet<>()).add(file);
//		}
//		return prediction;	
//	}
	
	public static void main(String[]args) throws FileNotFoundException {
		String path="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\test";
		String path2="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\test2";
		List<File>files=Arrays.asList(new File(path).listFiles());
		List<File>test=Arrays.asList(new File(path2).listFiles());
		KNeighbours model=new KNeighbours(new HashMap<>(),3);
		model.Train(files);
		Map<String,Set<File>>prediction=model.predict_class(test);
		for(String classname:prediction.keySet()) {
			System.out.println(classname+":"+prediction.get(classname));
			break;
		}
//		File test=files.get(25);
//		System.out.println(test.getName());
//		Traitement process=new Traitement(test);
//		List<String> word_list=process.file_processing();
//		Map<String,Integer>file_word_occurence=model.File_Word_Occurence(word_list);
//		String class_predict=model.check_distance(file_word_occurence);
//		System.out.println(class_predict);
//		for(String mot:file_word_occurence.keySet()) {
//			System.out.println(mot+":"+file_word_occurence.get(mot));
//		}
//		for(File file:model.Occurence_map.keySet()) {
//			System.out.println("fichier:"+file.getName());
//			Map<String,Integer>mots=model.Occurence_map.get(file);
//			for(String mot:mots.keySet()) {
//				System.out.println(mot+":"+mots.get(mot));
//			}
//			break;
//		}
	}
}
