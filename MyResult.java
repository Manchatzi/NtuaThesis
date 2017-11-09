import java.util.List;

final class MyResult {
	private final List<List<Integer>> first;
	private final List<Integer> second;
	private final List<String> third;
	
	public MyResult(List<List<Integer>> first,List<Integer> second, List<String> third){
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public List<List<Integer>> getFirst() {
		return first;
	}
	
	public List<Integer> getSecond() {
		return second;
	}
	
	public List<String> getThird() {
		return third;
	}

}
