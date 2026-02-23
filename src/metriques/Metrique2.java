package metriques;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
public class Metrique2 {
	
	public static Map<String,Double>recall(Map<String,Set<File>>R,Map<String,Set<File>>S){
		Map<String,Double>class_recall=new HashMap<>();
		for(String class_name:R.keySet()) {
			Set<File> real_class_file=R.get(class_name);
			Set<File>result=new HashSet<File>(real_class_file);
			Set<File> predict_class_file=S.getOrDefault(class_name, new HashSet<>());
			result.retainAll(predict_class_file);
			class_recall.put(class_name, (double)result.size()/real_class_file.size());
		}
		return class_recall;
	}
	
	public static Map<String,Double>precision(Map<String,Set<File>>R,Map<String,Set<File>>S){
		Map<String,Double>class_recall=new HashMap<>();
		for(String class_name:R.keySet()) {
			Set<File> real_class=R.get(class_name);
			Set<File>result=new HashSet<File>(real_class);
			Set<File> predict_class=S.getOrDefault(class_name, new HashSet<>());
			result.retainAll(predict_class);
			if(!predict_class.isEmpty()) {
				class_recall.put(class_name, (double)result.size()/predict_class.size());
			}else {
				class_recall.put(class_name, 0.0);
			}
		}
		return class_recall;
	}
	
	public static Double f1_score(Map<String,Double>recall,Map<String,Double>precision){
		Map<String,Double>class_f1_score=new HashMap<>();
		for(String class_name:recall.keySet()) {
			double precision_value=precision.get(class_name);
			double recall_value=recall.get(class_name);
			if(precision_value!=0 || recall_value!=0) {
				double f1_score_value=2*precision_value*recall_value/(precision_value+recall_value);
				class_f1_score.put(class_name, f1_score_value);
			}else {
				class_f1_score.put(class_name, 0.0);
			}
			
		}
		return class_f1_score.values().stream().mapToDouble(Double::doubleValue).sum()/class_f1_score.size();
	}
}
