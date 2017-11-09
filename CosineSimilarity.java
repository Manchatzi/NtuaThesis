import java.util.List;


public class CosineSimilarity {
	
	public double cosineSimilarity(List<Integer> vec1, List<Integer> vec2 ){
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;
		
		for (int i=0; i<vec1.size(); i++){
			dotProduct += vec1.get(i) * vec2.get(i);
			magnitude1 += Math.pow(vec1.get(i), 2);
			magnitude2 += Math.pow(vec2.get(i), 2);
		}
		
		magnitude1 = Math.sqrt(magnitude1);
		magnitude2 = Math.sqrt(magnitude2);
		
		if ((magnitude1 != 0.0) && (magnitude2 != 0.0)){
			cosineSimilarity = dotProduct/(magnitude1*magnitude2);
		}
		
		return cosineSimilarity;
	}

}
