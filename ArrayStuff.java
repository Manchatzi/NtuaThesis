import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayStuff {
	
	public static int[] top10(int[] array){
		int[] temp = new int[10];
		int[] maxIndex = new int[10];
		List<Integer> clone = new ArrayList<Integer>();
		
		for(int j:array){
			clone.add(j);
		}
		
		if (array.length<=10){
			return maxIndex;
		}
		else{
			Arrays.sort(array);
			for (int i=0;i<temp.length;i++){
				temp[i] = array[array.length-i-1];
			}
			int k=0;
			for (int i:temp){
				int key = clone.indexOf(i);
				maxIndex[k] = key;
				k++; clone.set(key,5000); //since max can be 2061, # of shots
			}
		}
		return maxIndex;
	}
	
	public static int[] min5(double[] array){
		double[] temp = new double[5];
		int[] minIndex = new int[5];
		List<Double> clone = new ArrayList<Double>();
		
		for(double j:array){
			clone.add(j);
		}
		
		if (array.length<=5){
			return minIndex;
		}
		else{
			Arrays.sort(array);
			for (int i=0;i<temp.length;i++){
				temp[i] = array[i];
			}
			int k=0;
			for (double i:temp){
				int key = clone.indexOf(i);
				minIndex[k] = key;
				k++; clone.set(key,2.0); //since max can be 2061, # of shots
			}
		}
		return minIndex;
	}
	
	public static int toSeconds(String time){
		
		int sec = Integer.parseInt(time.substring(6,8));
		int min = Integer.parseInt(time.substring(3,5));
		int hour = Integer.parseInt(time.substring(0,2));
		
		int seconds = sec + 60*min + 3600*hour;

		return seconds;
	}

}
