package cross_validation;
import java.util.List;
import classifieur.Classifieur;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import metriques.Metrique2;

public class Crossvalidation {
	private Map<String,List<File>>class_file;
	private String destination;
	public Crossvalidation() {
		this.class_file=new HashMap<>();
		this.destination="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\crossvalidation\\";
	}
	
	public void file_per_class(String path){
		File[] files=new File(path).listFiles();
		for(File file:files) {
			String class_name=file.getName().split("__")[1].replace("_",".");
			this.class_file.computeIfAbsent(class_name,k->new ArrayList<>()).add(file);
		}
	}
	public Map<String,Set<File>>file_per_class(List<File>files) {
		Map<String,Set<File>>file_per_class=new HashMap<>();
		for(File file:files) {
			String class_name=file.getName().split("__")[1].replace("_",".");
			file_per_class.computeIfAbsent(class_name,k->new HashSet<>()).add(file);
		}
		return file_per_class;
	}
	
	public Map<String,Double> shuffle(){
		Map<String,Double> class_distribution=new HashMap<>();
		for(String class_name:this.class_file.keySet()) {
			List<File> files=this.class_file.get(class_name);
			Collections.shuffle(files);
			this.class_file.put(class_name, files);
			class_distribution.put(class_name, (double)files.size());
		}
		return class_distribution;
	}
	
	public void folder_split(String path,int n_fold) throws IOException {
		this.file_per_class(path);
		Map<String,Double> class_distribution=this.shuffle();
		Map<String,List<File>> class_file_copy=new HashMap<String,List<File>>(this.class_file);
		for(String class_name:class_file_copy.keySet()) {
			int total_distribution=(int) (class_distribution.get(class_name)/n_fold);
			for(int it=1;it<n_fold+1;it++) {
				List<File> filesToRemove = new ArrayList<>();
				int a1=0;
				for(File file:class_file_copy.get(class_name)){
					if(a1<total_distribution) {
						Files.copy(file.toPath(),
						        (new File(this.destination + file.getName()+"__"+it)).toPath(),
						        	StandardCopyOption.REPLACE_EXISTING);
						filesToRemove.add(file);
					}else {
						break;
					}
					a1++;
				}
				class_file_copy.get(class_name).removeAll(filesToRemove);
			}
			if(class_file_copy.get(class_name).size()>0){
				List<File> filesToRemove = new ArrayList<>();
				for(File file:class_file_copy.get(class_name)){
					Files.copy(file.toPath(),
					        (new File(this.destination + file.getName()+"__"+n_fold)).toPath(),
					        	StandardCopyOption.REPLACE_EXISTING);
					filesToRemove.add(file);
				}
				class_file_copy.get(class_name).removeAll(filesToRemove);
			}	
		}
		
	}
	
	public List<Double> cross_validation(Classifieur model,String path,int cv) throws IOException{
		this.folder_split(path, cv);
		List<File>files=Arrays.asList(new File(this.destination).listFiles());
		List<Double>F1_score=new ArrayList<>();
		for(int i=1;i<cv+1;i++) {
			System.out.println("process===========>"+i+"/"+cv);
			final int fold_number=i;
			List<File>test_data=files.stream()
					.filter(file->Integer.parseInt(file.getName().split("__")[2])==fold_number)
					.collect(Collectors.toList());
			
			List<File>train_data=files.stream()
					.filter(file->Integer.parseInt(file.getName().split("__")[2])!=fold_number)
					.collect(Collectors.toList());
			model.Train(train_data);
			System.out.println("prediction.....");
			Map<String,Set<File>>prediction=model.predict_class(test_data);
			Map<String,Set<File>>R=this.file_per_class(test_data);
			System.out.println("recall....");
			Map<String,Double> recall=Metrique2.recall(R,prediction);
			System.out.println("precision....");
			Map<String,Double> precision=Metrique2.precision(R,prediction);
			System.out.println(Metrique2.f1_score(recall, precision));
			F1_score.add(Metrique2.f1_score(recall, precision));
		}
		System.out.println("F1-Score of crossvalidation:");
		return F1_score; 
	}
}
