package metriques;
import java.io.File;
import java.util.List;
import java.io.FileNotFoundException;
import classifieur.NaivesBayes;
public class Metriques {
	public static double accuracy(String Test_folder) throws FileNotFoundException {
		File[]files=new File(Test_folder).listFiles();
		int n=files.length;
		int correctPrediction=0;
		NaivesBayes classifieur=new NaivesBayes();
		for(File file:files) {
			String className=file.getName().split("__")[1].replace("_",".");
			//System.out.println(className);
			String class_predict=classifieur.predict_class(file);
			//System.out.println(class_predict);
			if(class_predict.equals(className)) {
				correctPrediction++;
			}
		}
		return (double)correctPrediction/n;
	}
	
	public static double compute_f1_score() {
		
	}
	
	public static double recall(int TP,int FN) {
		return TP/(TP+FN);	
	}
	
	public static double precision(int TP,int FP) {
		return TP/(TP+FP);
	}
	
	public static double f1_score(double recall,double precision) {
		return 2*((recall*precision)/(recall+precision));
	}
	
	public static void main(String[] args) {
		String path="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\DataProcessing";
		try {
			System.out.println("Begin...");
			System.out.println("accuracy is:"+accuracy(path));
			System.out.println("end");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
public class EvaluationMetrics {

    public static void main(String[] args) throws IOException {
        // Chemin vers le fichier de probabilités des classes
    	 String wordProbabilitiesFilePath = "Data\\word_probabilities.xml";

         // Créer le classificateur Naive Bayes en utilisant les probabilités du fichier XML
         String classProbabilitiesFilePath = "Data\\class_probabilities.xml";
        // Créer le classificateur Naive Bayes en utilisant les probabilités des classes et des mots
        NaiveBayesClassifier classifier = new NaiveBayesClassifier(wordProbabilitiesFilePath,classProbabilitiesFilePath);

        // Chemin vers le dossier de test contenant les fichiers nomFichier_classe
        String testFolderPath = "Data\\test";
        Map<String, Map<String, Integer>> evaluationMetrics = evaluateClassifier(classifier, testFolderPath);

        // Afficher les métriques d'évaluation
        for (Map.Entry<String, Map<String, Integer>> entry : evaluationMetrics.entrySet()) {
            String className = entry.getKey();
            Map<String, Integer> metrics = entry.getValue();

            System.out.println("Classe : " + className);
            System.out.println("Vrais Positifs (TP) : " + metrics.get("TP"));
            System.out.println("Vrais Négatifs (TN) : " + metrics.get("TN"));
            System.out.println("Faux Positifs (FP) : " + metrics.get("FP"));
            System.out.println("Faux Négatifs (FN) : " + metrics.get("FN"));
            System.out.println("---------------------------");
        }
    }

    public static Map<String, Map<String, Integer>> evaluateClassifier(NaiveBayesClassifier classifier, String dataFolderPath) throws IOException {
        // Charger les fichiers de test du dossier
        File testFolder = new File(dataFolderPath);
        File[] testFiles = testFolder.listFiles();
        Map<String, Map<String, Integer>> evaluationMetrics = new HashMap<>();

        if (testFiles != null) {
            for (File testFile : testFiles) {
                try {
                    // Lire le contenu du fichier de test et obtenir la prédiction du classifieur
                    String text = FileUtils.readFileToString(testFile, "UTF-8");
                    String predictedClass = classifier.classify(text);

                    // Extraire la vraie classe du nom du fichier
                    String trueClass = extractTrueClass(testFile.getName());

                    // Mettre à jour les métriques d'évaluation en fonction des prédictions
                    updateMetrics(evaluationMetrics, trueClass, predictedClass);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Gérer l'exception selon les besoins de votre application
                }
            }
        } else {
            System.out.println("Aucun fichier trouvé dans le dossier de test.");
        }

        return evaluationMetrics;
    }

    private static String extractTrueClass(String fileName) {
        // Supposons que le nom du fichier soit dans le format "nomFichier_classe.extension"
        // Vous pouvez extraire la classe en séparant le nom du fichier à partir du caractère "_"
        String[] parts = fileName.split("_");
        // La classe est le deuxième élément du tableau après le caractère "_"
        return parts[1];
    }

    private static void updateMetrics(Map<String, Map<String, Integer>> metrics, String trueClass, String predictedClass) {
        // Mettre à jour les métriques en fonction des prédictions
        Map<String, Integer> classMetrics = metrics.computeIfAbsent(trueClass, k -> new HashMap<>());

        if (predictedClass.equals(trueClass)) {
            // Vrai positif (TP)
            classMetrics.put("TP", classMetrics.getOrDefault("TP", 0) + 1);
        } else {
            // Faux négatif (FN) pour la vraie classe
            classMetrics.put("FN", classMetrics.getOrDefault("FN", 0) + 1);
            // Faux positif (FP) pour la classe prédite
            classMetrics = metrics.computeIfAbsent(predictedClass, k -> new HashMap<>());
            classMetrics.put("FP", classMetrics.getOrDefault("FP", 0) + 1);
        }

        // Vrai négatif (TN) pour toutes les autres classes que la vraie classe et la classe prédite
        for (String className : metrics.keySet()) {
            if (!className.equals(trueClass) && !className.equals(predictedClass)) {
                classMetrics = metrics.computeIfAbsent(className, k -> new HashMap<>());
                classMetrics.put("TN", classMetrics.getOrDefault("TN", 0) + 1);
            }
        }
    }
}
