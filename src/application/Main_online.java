package application;
import cross_validation.Crossvalidation;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import classifieur.KNeighbours;
import classifieur.NaivesBayes;
public class Main_online {
	public static void main(String[]args) throws IOException{
		String path="C:\\Users\\Y.STORE\\Documents\\MasterS3\\TextMining\\DataProcessing";
		Crossvalidation cross_val=new Crossvalidation();
		System.out.println("1-KNN");
		System.out.println("2-NaivesBayes");
		boolean letgo=true;
		while(letgo) {
			@SuppressWarnings("resource")
			int choices= new Scanner(System.in).nextInt();
			if(choices==1) {
				System.out.println("choisissez le nombre de voisin a prendre en compte");
				@SuppressWarnings("resource")
				int k=new Scanner(System.in).nextInt();
				KNeighbours model=new KNeighbours(k);
				List<Double>f1_score=cross_val.cross_validation(model, path,4);
				System.out.println(f1_score);
			}else if(choices==2) {
				NaivesBayes model=new NaivesBayes(new HashMap<>(),new HashMap<>());
				List<Double>f1_score=cross_val.cross_validation(model, path,4);
				System.out.println(f1_score);
			}else {
				letgo=false;
			}
		}
	}

}
