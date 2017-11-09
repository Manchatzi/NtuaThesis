import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Ints;


public class Thesis {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
		
		String rdf_uri = "D:/eclipseWorkspace/Thesis/src/bangkok_dangerous_triples.nt" ;
		String ont_uri = "D:/eclipseWorkspace/Thesis/src/scriptontology.owl" ;
		
		CreateVectors cv = new CreateVectors();
		List<List<Integer>> workVectors = new ArrayList<List<Integer>>();
		List<Integer> workIndexes = new ArrayList<Integer>();
		List<String> workLabels = new ArrayList<String>();
		List<Integer> clusters = new ArrayList<Integer>();
		
		try{
			MyResult workResult = cv.vectorize(rdf_uri, ont_uri);
			workVectors = workResult.getFirst();
			workIndexes = workResult.getSecond();
			workLabels = workResult.getThird();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		System.out.println("Printing to text files");
		
		String filename = "D:/eclipseWorkspace/Thesis/src/vectors.txt";
		Writer writer = null;
		try{
			writer = new FileWriter(filename);
			for (List<Integer> l : workVectors){
				for (int i : l){
					writer.write(String.valueOf(i) + " ");
				}
				writer.write(System.getProperty("line.separator"));
			}
		}catch (IOException e){
			e.getMessage();
		}finally{
			try {writer.close();} catch (Exception ex) {}
		}
		
		String filename1 = "D:/eclipseWorkspace/Thesis/src/indexes.txt";
		Writer writer1 = null;
		try{
			writer1 = new FileWriter(filename1);
			for (int i : workIndexes){
					writer1.write(String.valueOf(i));
				writer1.write(System.getProperty("line.separator"));
			}
		}catch (IOException e1){
			e1.getMessage();
		}finally{
			try {writer1.close();} catch (Exception ex1) {}
		}
		
		String filename2 = "D:/eclipseWorkspace/Thesis/src/labels.txt";
		Writer writer2 = null;
		try{
			writer2 = new FileWriter(filename2);
			for (String s : workLabels){
					writer2.write(s);
				writer2.write(System.getProperty("line.separator"));
			}
		}catch (IOException e2){
			e2.getMessage();
		}finally{
			try {writer2.close();} catch (Exception ex2) {}
		}
//----------------------------------------------------------------
		System.out.println("Calculating cosine similarities");
		
		List<double[]> cosSims = new ArrayList<double[]>();		
	//	int input = 1428;
	//	int key = workIndexes.indexOf(input);
		
		for (int i=0; i<workVectors.size(); i++){
			double[] cos = new double[workVectors.size()];
			for (int j=0; j<workVectors.size(); j++){
				cos[j] = new CosineSimilarity().cosineSimilarity(workVectors.get(i),workVectors.get(j));
			}
			cosSims.add(cos);
		}
		
		String filename2a = "D:/eclipseWorkspace/Thesis/src/DBsimilarities.csv";
		try{
			writer2 = new FileWriter(filename2a);
			int i = 0;
			for (double[] d : cosSims){
				int[] minIndex = ArrayStuff.min5(d);
				writer2.write("\"" + workIndexes.get(i) + "\"");
				for (int id : minIndex){
					writer2.write(",\"" + workIndexes.get(id) + "\""); writer2.write(",\"" + d[id] + "\""); 
				}
				writer2.write(System.getProperty("line.separator"));
				i++;
			}
		}catch (IOException e2){
			e2.getMessage();
		}finally{
			try {writer2.close();} catch (Exception ex2) {}
		}
		
	//	System.out.println("The closest to " + input + " using cosine similarity is " + workIndexes.get(cosSims.indexOf(Collections.min(cosSims))));
		
//---------------------------------------------------------
		System.out.println("Initiating clustering");
		System.out.println("Clustering Complete, getting results");
		
		String resultsFile = "D:/eclipseWorkspace/Thesis/src/kmeansClusters.txt";
		try{
			Scanner scanner = new Scanner(new File(resultsFile));
			while(scanner.hasNextInt()){
				clusters.add(scanner.nextInt());
			}
			scanner.close();
		}
		catch(Exception e3){
			e3.getMessage();
		}
		
		//System.out.println(clusters.size());
//----------------------------------------------------------		
		System.out.println("Creating the clusters");
		
		List<Cluster> workClusters = new ArrayList<Cluster>();
		int[] labelSums = new int[workLabels.size()];
		
		for(int i=1; i<=Collections.max(clusters);i++){
			Arrays.fill(labelSums, 0);
			Cluster c = new Cluster(i);		
			
			for(int j=0; j<clusters.size();j++){
				if(clusters.get(j)==i){
					c.inShots.add(workIndexes.get(j));
					c.inVectors.add(workVectors.get(j));
				}
			}
			for (List<Integer> l:c.inVectors){
				for (int k=0;k<l.size();k++){
					labelSums[k] += l.get(k);
				}
			}
		//	if (i==3){
		//		System.out.println(Arrays.toString(labelSums));
		//	}
			int[] maxIndex = ArrayStuff.top10(labelSums);
			for (int ind:maxIndex){
				c.topLabels.add(workLabels.get(ind));
			}
			workClusters.add(c);	
		}
		
//-------------------------------------------------------------------------------------
		
		System.out.println("Creating DB files");
		String filename3 = "D:/eclipseWorkspace/Thesis/src/KMEANSLabels.csv";
		Writer writer3 = null;
		try{
			writer3 = new FileWriter(filename3);
			for (Cluster c : workClusters){
				writer3.write("\"" + c.id + "\"");
				for (String s : c.topLabels){
					writer3.write(",\"" + s + "\"");
				}
				writer3.write(System.getProperty("line.separator"));
			}
		}catch (IOException e3){
			e3.getMessage();
		}finally{
			try {writer3.close();} catch (Exception ex3) {}
		}
		
		String filename4 = "D:/eclipseWorkspace/Thesis/src/KMEANSShots.csv";
		try{
			writer3 = new FileWriter(filename4);
			for (Cluster c : workClusters){
				for (int i : c.inShots){
					writer3.write("\"" + c.id + "\",\"" + i + "\"");
					writer3.write(System.getProperty("line.separator"));
				}
			}
		}catch (IOException e3){
			e3.getMessage();
		}finally{
			try {writer3.close();} catch (Exception ex3) {}
		}
		
		String filename5 = "D:/eclipseWorkspace/Thesis/src/DBPics.csv";
		try{
			writer3 = new FileWriter(filename5);
			for (Cluster c : workClusters){
				for (int i : c.inShots){
					writer3.write("\"" + i + "\",\"" + "picture"+ String.format("%04d",i) +".jpeg" + "\"");
					writer3.write(System.getProperty("line.separator"));
				}
			}
		}catch (IOException e3){
			e3.getMessage();
		}finally{
			try {writer3.close();} catch (Exception ex3) {}
		}
		
		System.out.println("DONE");
	}

}
