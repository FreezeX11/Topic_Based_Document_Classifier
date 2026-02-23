package classifieur;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import distance.Distance;

public class Clustering{
	private Map<Integer,Map<String,List<Double>>>clusters;
	private Map<Integer,Map<Integer,Double>>clusters_distance;
	private Map<Integer,List<Double>>centres_gravite;
	public Clustering() {
		this.clusters=new HashMap<>();
		this.clusters_distance=new HashMap<>();
	}
	
	public void merge(int cluster_1,int cluster_2){
		this.clusters.get(cluster_1).putAll(this.clusters.get(cluster_2));
		this.clusters.remove(cluster_2);
		this.clusters_distance.remove(cluster_2);
		for(int cluster:this.clusters_distance.keySet()){
			if(this.clusters_distance.get(cluster).containsKey(cluster_2)) {
				this.clusters_distance.get(cluster).remove(cluster_2);
				//if(this.clusters_distance.get(cluster).isEmpty()) this.clusters_distance.remove(cluster);
			}
		}
	}
	
	public void update_distance(int cluster_1) {
		for(int cluster_2:clusters_distance.keySet()) {
			if(cluster_1==cluster_2) continue;
			if(clusters_distance.get(cluster_2).containsKey(cluster_1)){
				double distance=Distance.Centroid_Linkage(clusters.get(cluster_1),clusters.get(cluster_2));
				clusters_distance.get(cluster_2).put(cluster_1,distance);
			}else {
				double distance=Distance.Centroid_Linkage(clusters.get(cluster_1),clusters.get(cluster_2));
				clusters_distance.get(cluster_1).put(cluster_2,distance);
			}
		}
	}
	
	public void Ascendant_Clustering(Map<String,List<Double>>documents,int k){
		int nb_cluster=k;
		int i=0;
		for(String document:documents.keySet()) {
			Map<String,List<Double>>doc=new HashMap<>();
			doc.put(document,documents.get(document));
			this.clusters.put(i,doc);
		}
		
		for(int cluster_1:clusters.keySet()) {
			Map<Integer,Double>Point_distance=new HashMap<>();
			for(int cluster_2:clusters.keySet()){
				if(cluster_1==cluster_2) continue;
				if(clusters_distance.get(cluster_2).containsKey(cluster_1)) continue;
				double distance=Distance.Centroid_Linkage(clusters.get(cluster_1),clusters.get(cluster_2));
				Point_distance.put(cluster_2, distance);
				this.clusters_distance.put(cluster_1, Point_distance);
				}
			}
		
		while(clusters.size()>nb_cluster){
			double min_distance=Double.POSITIVE_INFINITY;
			List<Integer>merge_info=new ArrayList<>();
			for(int cluster_1:clusters_distance.keySet()) {
				for(int cluster_2:clusters_distance.get(cluster_1).keySet()) {
					double distance=clusters_distance.get(cluster_1).get(cluster_2);
					if(distance<min_distance) {
						if(merge_info.size()!=0) merge_info.clear();
						min_distance=distance;
						merge_info.add(cluster_1);
						merge_info.add(cluster_2);
					}		
			}
		}
			this.merge(merge_info.get(0),merge_info.get(1));
			this.update_distance(merge_info.get(0));
		}
			
	}
	
	public void initialiser_cluster(Map<String,List<Double>>documents,int k){
		Random rand = new Random();
		for(String document_name:documents.keySet()) {
			int n=rand.nextInt(k)+1;
			Map<String,List<Double>>document=new HashMap<>();
			document.put(document_name, documents.get(document_name));
			//while(this.clusters.get(n).containsKey(document_name)) n=rand.nextInt(20)+1;
			this.clusters.put(n, document);
			}	
	}
	
	public void calculer_centre(int k){
		for(int i=1;i<k+1;i++) {
			this.centres_gravite.put(i,Distance.Centre_gravite(this.clusters.get(i)));
		}	
	}
	
	public Map<Integer,Map<String,List<Double>>> assigner(Map<String,List<Double>>documents,int k) {
		Map<Integer,Map<String,List<Double>>>cluster_assignation=new HashMap<>();
		for(String document_name:documents.keySet()){
			int centre_indice=0;
			double min_distance=Double.POSITIVE_INFINITY;
			for(int i=1;i<k+1;i++) {
				double distance=Distance.distance(documents.get(document_name),this.centres_gravite.get(i));
				if(distance<min_distance) {
					min_distance=distance;
					centre_indice=i;
				}
			}
			Map<String,List<Double>>document=new HashMap<>();
			document.put(document_name,documents.get(document_name));
			cluster_assignation.put(centre_indice,document);
		}
		return cluster_assignation;
	}
	
	public boolean convergence(Map<Integer,Map<String,List<Double>>>cluster_assignation,int k) {
		Map<Integer,List<Double>>centres=new HashMap<>();
		for(int i=1;i<k+1;i++) {
			centres.put(i,Distance.Centre_gravite(cluster_assignation.get(i)));
		}
		if(this.centres_gravite.equals(centres)) {
			return true;
		}
		this.clusters.clear();
	    this.clusters.putAll(cluster_assignation);
		return false;
	}
	
	public void K_Means(Map<String,List<Double>>documents,int k){
		boolean convergence=false;
		this.initialiser_cluster(documents,k);
		while(convergence){
			this.calculer_centre(k);
			Map<Integer,Map<String,List<Double>>>assignation=this.assigner(documents, k);
			convergence=this.convergence(assignation,k);	
		}
	}

}
