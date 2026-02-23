package distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Distance {

	public static double distance(List<Double>P1,List<Double>P2) {
		int size=P1.size();
		double distance=0.0;
		for(int i=0;i<size;i++) distance+=(P1.get(i)-P2.get(i))*(P1.get(i)-P2.get(i));
		return Math.sqrt(distance);
	}
	
	public static double Single_Linkage(Map<String,List<Double>>c1,Map<String,List<Double>>c2){
		double min_distance=Double.POSITIVE_INFINITY;
		for(String Point1:c1.keySet()) {
			for(String Point2:c2.keySet()){
				double distance=Distance.distance(c1.get(Point1),c2.get(Point2));
				if(distance<min_distance) {
					min_distance=distance;
				}
			}
		}
		return min_distance;
	}
	
	public static double Complete_Linkage(Map<String,List<Double>>c1,Map<String,List<Double>>c2){
		double max_distance=Double.NEGATIVE_INFINITY;
		for(String Point1:c1.keySet()) {
			for(String Point2:c2.keySet()){
				double distance=Distance.distance(c1.get(Point1),c2.get(Point2));
				if(distance>max_distance) {
					max_distance=distance;
				}
			}
		}
		return max_distance;
	}
	
	public static double Centroid_Linkage(Map<String,List<Double>>c1,Map<String,List<Double>>c2) {
		return Distance.distance(Distance.Centre_gravite(c1),Distance.Centre_gravite(c2));	
}
	
	public static List<Double>Centre_gravite(Map<String,List<Double>>cluster) {
		int dim=cluster.entrySet().iterator().next().getValue().size();
		List<Double>centre_gravite=new ArrayList<Double>(Collections.nCopies(dim, 0.0));
		for(String Point:cluster.keySet()) for(int i=0;i<dim;i++) centre_gravite.add(i,centre_gravite.get(i)+cluster.get(Point).get(i));
		List<Double>result=centre_gravite.stream()
										 .map(value->value/cluster.size())
										 .collect(Collectors.toList());
		return result;
	}
}
