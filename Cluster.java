import java.util.ArrayList;
import java.util.List;


final class Cluster {
	public int id;
	public List<Integer> inShots;
	public List<List<Integer>> inVectors;
	public List<String> topLabels;
	
	public Cluster(int id){
		this.id = id;
		this.inShots = new ArrayList<Integer>();
		this.inVectors = new ArrayList<List<Integer>>();
		this.topLabels = new ArrayList<String>();
	}
	
	public Cluster(int id, List<Integer> inShots, List<List<Integer>> inVectors, List<String> topLabels){
		this.id = id;
		this.inShots = inShots;
		this.inVectors = inVectors;
		this.topLabels = topLabels;
	}
	
	public List<List<Integer>> getVectors() {
		return inVectors;
	}
	
	public List<Integer> getShots() {
		return inShots;
	}
	
	public List<String> getTopLabels() {
		return topLabels;
	}
	
	public void printCluster(){
		System.out.println("Cluster " + id + " has shots: " + inShots.size() + " and vectors: " + inVectors.size());
		System.out.println("The most common features in the cluster are:");
		for (String s:topLabels){
			System.out.println(s);
		}
	}

}
