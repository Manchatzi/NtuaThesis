import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class CreateVectors {

	public MyResult vectorize(String uri1, String uri2) throws IOException {
		
		FileManager.get().addLocatorClassLoader(CreateVectors.class.getClassLoader());
		Model myScript = FileManager.get().loadModel(uri1);
		
		String filename = "D:/eclipseWorkspace/Thesis/src/output.nt";
		FileWriter out = new FileWriter (filename);
		try{
			myScript.write(out,"N-TRIPLE");
		}
		finally{
			try{
				out.close();
			}
			catch (IOException closeException){
			}
		}
///////////////////////////////////////////////////////////
		OntModel myOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);//OWL_MEM_MINI_RULE_INF
		InputStream in = new FileInputStream(uri2);
		myOntology.read(in,null);
		in.close();
	
		OntClass shotClass = myOntology.getOntClass("http://moviedb.org/scriptontology/Shot");
		List<String> shotClassList = new ArrayList<String>();
		List<String> shotParent = new ArrayList<String>();
		Queue<OntClass> q = new LinkedList<OntClass>();
		q.add(shotClass);
		while (!q.isEmpty()) {			
			OntClass oc = q.remove();
			shotClassList.add(oc.getURI().substring(oc.getURI().lastIndexOf("/")+1));
			
			OntClass p = oc.getSuperClass();
			if (p==null){
				shotParent.add("root");
			}
			else{
				shotParent.add(p.getURI().substring(p.getURI().lastIndexOf("/")+1));
			}
						
			Iterator<OntClass> it = oc.listSubClasses();
			List<OntClass> listit = Lists.newArrayList(it);
			for (OntClass noc : listit){
				q.add(noc);
			}
			//System.out.println(oc.toString() + "--------");			
		}
		
		List<String> sceneClassList = new ArrayList<String>();
		Iterator<OntClass> it = myOntology.getOntClass("http://moviedb.org/scriptontology/Scene").listSubClasses();
		while (it.hasNext()) {
			OntClass oc = it.next();
			sceneClassList.add(oc.getURI().substring(oc.getURI().lastIndexOf("/")+1));
			//System.out.println(oc.toString());
		}
	//	System.out.println(shotClassList.size() + " " + sceneClassList.size());
		
		/*	SHOTS-------
		 * 	1.AmericanShot,MediumLongShot,FullLengthShot,ThreeQuarterShot 2.CloseUpShot 3.CutAwayShot 
		 * 	4.CutInShot 5.DutchAngleShot,ObliqueAngleShot 6.EstablishingShot,MasterShot 7.ExtremeWideShot
		 * 	8.EyeLevelShot 9.FadeInShot 10.FadeOutShot 11.FlashPanShot 12.HighAngleShot 13.LongTakeShot
		 * 	14.LowAngleShot 15.MediumCloseUpShot 16.MediumShot 17.MediumWideShot 18.MovingShot 
		 * 	19.OverTheShoulderShot 20.PeopleShot 21.PointOfViewShot 22.ProfileView,SideView 23.StockShot
		 * 	24.SubjectiveShot 25.VeryWideShot 26.WideLongShot 27.BigCloseUpShot 28.ExtremeCloseUpShot
		 * 	29.VeryCloseUpShot 30.LongShot,FullShot,WideShot 31.ExtremeLongShot 32.BirdsEyeShot,OverheadShot
		 * 	33.ExtremeHighAngleShot 34.ExtremeLowAngleShot 35.ZoomShot 36.ZoomInShot 37.ZoomOutShot
		 * 	38.CraneShot 39.DollyShot 40.DollyInShot 41.DollyOutShot 42.HandHeldShot 43.TiltShot 44.TrackingShot
		 * 	45.PanShot 46.ZipPanShot 47.ThreeShot 48.TwoShot
		 * 
		 *	2 -> 27,28,29	6 -> 30 ->31	12 -> 32,33		14 -> 34	20 -> 47,48
		 *	18 -> 35 -> 36,37
		 *     -> 39 -> 40,41
		 *     -> 45 -> 46
		 *     -> 38,42,43,44
		 *     
		 *  1.CloseUpShot 2.ExtremeCloseUpShot 3.LongShot,FullShot,WideShot 4.HighAngleShot 5.OverheadShot
		 *  6.LowAngleShot 7.MediumCloseUpShot 8.MediumShot 9.MovingShot 10.SideView 11.WideLongShot
		 *  
		 *  ACTS-------
		 *  1.HumanAct 2.BodyAct 3.HumanExpressionAct 4.EatingAct 5.FightingAct 6.RunningAct 7.SittingAct
		 *  8.SleepingAct 9.WalkingAct 10.CryingAct 11.LaughingAct 12.ScreamingAct 13.ShoutingAct
		 *  14.SpeakingAct 15.VoiceOverSpeakingAct
		 *  
		 *  1 -> 2 -> 4,5,6,7,8,9
		 *    -> 3 -> 10,11,12,13,(14 -> 15)
		 *    
		 *  ALL SpeakingAct
		 *    
		 *	SCENES------
		 *	1.DayScene 2.DissolveScene 3.ExternalScene 4.FadeInScene 5.FlashbackScene 6.InternalScene
		 *	7.NightScene |||| 8.FadeOutScene 9.DawnScene 10.InterCutScene 11.DarkScene 12.EveningScene
		 *
		 */
////////////////////////////////////////////////
		
		List<List<Integer>> finalVectors = new ArrayList<List<Integer>>();
		List<Integer> indexes = new ArrayList<Integer>();
		List<String> labels = new ArrayList<String>();
		List<RDFNode> shots = new ArrayList<RDFNode>();
		List<String> roles = new ArrayList<String>(Collections.nCopies(48, ""));
		QuerySolutionMap initialBindings = new QuerySolutionMap();
//----------------------------------------------
		String queryStringDictionary = 
				"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"SELECT ?z " +
				"WHERE {" +
				"{	?x movie:description ?z .}" +
				"UNION" +
				"{	?x movie:text ?z .}" +
				"UNION" +
				"{	?x movie:name ?z .}" +
				"}";
		
		Query queryDictionary = QueryFactory.create(queryStringDictionary);
		QueryExecution qeDictionary = QueryExecutionFactory.create(queryDictionary,myScript);
		ResultSet resultsDictionary = qeDictionary.execSelect();
		//ResultSetFormatter.out(System.out, resultsDictionary, queryDictionary);
		
		StringBuilder allText = new StringBuilder();
		
		for ( ; resultsDictionary.hasNext() ;){
			QuerySolution soln = resultsDictionary.nextSolution();
			RDFNode n = soln.get("z");
			String value = ((Literal)n).toString().replaceAll("@en", "").toLowerCase();
			allText.append(value + " ");
		}
		
		StanfordLemmatizer slemm = new StanfordLemmatizer();
		List<String> allLemmas = slemm.lemmatize(allText.toString());			
		Set<String> distinctLemmas = new TreeSet<String>(allLemmas);
		List<String> dictionary = new ArrayList<String>(distinctLemmas);
		System.out.println(dictionary.size());
		qeDictionary.close();
		//----------
		dictionary.remove("."); dictionary.remove("-"); dictionary.remove("'s"); dictionary.remove("the"); 
		dictionary.remove(","); dictionary.remove("a"); dictionary.remove("of"); dictionary.remove("he");
		dictionary.remove("at");dictionary.remove("in");dictionary.remove("but");dictionary.remove("and");
		dictionary.remove("be");dictionary.remove("on"); dictionary.remove("you"); dictionary.remove("she"); 
//----------------------------------------------
		String queryStringRole = 
				"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"SELECT ?y ?z " +
				"WHERE {" +
				"	?x movie:hasRole ?y ." +
				"	?y movie:name ?z ." +
				"}";
		
		Query queryRole = QueryFactory.create(queryStringRole);
		QueryExecution qeRole = QueryExecutionFactory.create(queryRole,myScript);
		ResultSet resultsRole = qeRole.execSelect();
		//ResultSetFormatter.out(System.out, resultsRole, queryRole);
		
		for ( ;resultsRole.hasNext(); ){
			QuerySolution soln = resultsRole.nextSolution();
			RDFNode n1 = soln.get("y"); RDFNode n2 = soln.get("z");
			Resource r1 = (Resource)n1; Literal r2 = (Literal)n2;
			int index = Integer.parseInt(r1.getURI().substring(r1.getURI().lastIndexOf("L")+1));
			//System.out.println(index + " " + r2.toString().replaceAll("@en",""));
			roles.set(index, r2.toString().replaceAll("@en",""));
		}
		qeRole.close();

//--------------------------------------------		
		String queryStringShot = 
				"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"SELECT *" +
				"WHERE {" +
				"	?x rdf:type ?z ." +
				"}";
		
		Query queryShot = QueryFactory.create(queryStringShot);
		QueryExecution qeShot = QueryExecutionFactory.create(queryShot,myScript);
		ResultSet resultsShot = qeShot.execSelect();
		//ResultSetFormatter.out(System.out, resultsShot, queryShot);

		for ( ;resultsShot.hasNext(); ){
			QuerySolution soln = resultsShot.nextSolution();
			RDFNode n1 = soln.get("x"); RDFNode n2 = soln.get("z");
			Resource r2 = (Resource)n2;
			String attr = r2.getURI().substring(r2.getURI().lastIndexOf("/")+1);
			if (shotClassList.contains(attr)){
				if (!shots.contains(n1)){
					shots.add(n1);
				//	System.out.println(n1.toString());
				}
			}
		}
		qeShot.close();
		//System.out.println(shots.size());
		List<String> startTimes = new ArrayList<String>(Collections.nCopies((shots.size()+5), " "));

///////////////////////////////////////////////////////////////////////////////////
		
		for (RDFNode shot : shots){
			
			//@@@@override
			//if (shot.toString().equals("http://image.ntua.gr/scriptontology/MOV_12794_SH1663")){
			
			List<Integer> shotVector = new ArrayList<Integer>();
			List<Integer> temp1 = new ArrayList<Integer>(Collections.nCopies((shotClassList.size()), 0)); 
			temp1.set(0, 1); //always subclass of shot
			List<Integer> temp2 = new ArrayList<Integer>(Collections.nCopies(sceneClassList.size(), 0));
			List<Integer> temp3 = new ArrayList<Integer>(Collections.nCopies(roles.size(), 0));
			List<Integer> temp4 = new ArrayList<Integer>(Collections.nCopies(dictionary.size(), 0));
			List<String> texts = new ArrayList<String>();
			
			//System.out.println("Creating Vector for shot " + shot.toString());
			int ind = Integer.parseInt(shot.toString().substring(shot.toString().lastIndexOf("H")+1));

			initialBindings.add("shot", shot);	

//***1st Query : THE SHOT itself
//------------------------------------------------
			String queryStringA = 
					"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"SELECT * " +
					"WHERE {" +
					"	?shot ?y ?z ." +
					" }";
			Query queryA = QueryFactory.create(queryStringA);
			QueryExecution qeA = QueryExecutionFactory.create(queryA,myScript,initialBindings);
			ResultSet resultsA = qeA.execSelect();
			//ResultSetFormatter.out(System.out, resultsA, queryA);
			
			for ( ;resultsA.hasNext(); ){
				QuerySolution soln = resultsA.nextSolution();
				RDFNode n = soln.get("y");
				if ( n.isLiteral() )
					System.out.println("error1");
				if ( n.isResource() ){
					Resource r = (Resource)n;
					if ( !r.isAnon() )
					{
						String attr = r.getURI();
						//System.out.println(attr);
						if (attr.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
						{
							Resource m = soln.getResource("z");
							String value = m.getURI().substring(m.getURI().lastIndexOf("/")+1).replaceAll("SideViewShot", "SideView"); //The replace is because the triples has error
							//System.out.println(value);
							if (value.equals("FlashbackScene")||value.equals("FadeInScene")||value.equals("DissolveScene")){
								int index = sceneClassList.indexOf(value);
								temp2.set(index, 1);
							}
						/*	if (value.equals("FlashbackScene")){
								temp1.set((temp1.size()-1),1);
							}
							else if (value.equals("FadeInScene")){
								temp1.set((temp1.size()-2),1);
							}
							else if (value.equals("DissolveScene")){
								temp1.set((temp1.size()-3),1);
							}*/
							else {
								int index = shotClassList.indexOf(value);
								temp1.set(index, 1);
								while (!(shotParent.get(index).equals("root"))){
									index = shotClassList.indexOf(shotParent.get(index));
									temp1.set(index, 1);
								}
							}
						}	
						else if (attr.equals("http://image.ntua.gr/scriptontology/startTime"))
						{
							Literal l = soln.getLiteral("z");
							String value = l.getValue().toString().replace("-T", "");
							String fvalue = value.substring(0,8);
							startTimes.set(ind, fvalue);
						}
						else if (attr.equals("http://image.ntua.gr/scriptontology/description"))
						{
							Literal l = soln.getLiteral("z");
							String value = l.getValue().toString();
							texts.add(value);
							//System.out.println(value);													
						}
						else if (attr.equals("http://image.ntua.gr/scriptontology/isNextOf"))
						{
						}
					}
				}
			}
			
//***2nd Query : THE SCENE of the shot
//-------------------------------------------
			String queryStringB = 
					"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"SELECT ?x ?y ?z " +
					"WHERE {" +
					"{	?x movie:hasPart ?shot ." +
					"	?x rdf:type ?z ." +
					"}" +
					"UNION {" +
					"	?x movie:hasPart ?shot ." +
					"	?x movie:description ?z .}" +
					" }";
			Query queryB = QueryFactory.create(queryStringB);
			QueryExecution qeB = QueryExecutionFactory.create(queryB,myScript,initialBindings);
			ResultSet resultsB = qeB.execSelect();
			//ResultSetFormatter.out(System.out, resultsB, queryB);
			
			for ( ;resultsB.hasNext(); )
			{
				QuerySolution soln = resultsB.nextSolution();
				RDFNode n = soln.get("z");
				if ( n.isLiteral() ){
				//its the description
					Literal l = (Literal)n;
					String value = l.getValue().toString();
					texts.add(value);
					//System.out.println(value);
				}
				else if ( n.isResource() )
				//its the scene types
				{
					Resource r = (Resource)n;
					if ( !r.isAnon() )
					{
						String attr = r.getURI();
						//System.out.println(attr);
						String value = attr.substring(attr.lastIndexOf("/")+1);
						//System.out.println(value);
						int index = sceneClassList.indexOf(value);
						temp2.set(index, 1);
					}
				}	
			}
			
//***3rd Query : THE ACTS of the shot	
//--------------------------------------------------------------
			String queryStringC = 
					"PREFIX movie: <http://image.ntua.gr/scriptontology/> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"SELECT ?x ?y ?z " +
					"WHERE {" +
					"	?x movie:happensIn ?shot ." +
					"	?x ?y ?z ." +
					" }";
			Query queryC = QueryFactory.create(queryStringC);
			QueryExecution qeC = QueryExecutionFactory.create(queryC,myScript,initialBindings);
			ResultSet resultsC = qeC.execSelect();
			//ResultSetFormatter.out(System.out, resultsC, queryC);
			
			for ( ;resultsC.hasNext(); )
			{
				QuerySolution soln = resultsC.nextSolution();
				RDFNode n = soln.get("y");
				if ( n.isLiteral() )
					System.out.println("error1");
				if ( n.isResource() ){
					Resource r = (Resource)n;
					if ( !r.isAnon() )
					{
						String attr = r.getURI();
						//System.out.println(attr);
						if (attr.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
						{
							//MOOT, All are Speaking Acts
						}	
						else if ((attr.equals("http://image.ntua.gr/scriptontology/performedBy"))||(attr.equals("http://image.ntua.gr/scriptontology/addressedTo")))
						{
							Resource m = soln.getResource("z");
							int index = Integer.parseInt(m.getURI().substring(m.getURI().lastIndexOf("L")+1));
							temp3.set(index, 1);
							String value = roles.get(index);
							texts.add(value);
							//System.out.println(value);
						}
						else if (attr.equals("http://image.ntua.gr/scriptontology/text"))
						{
							Literal l = soln.getLiteral("z");
							String value = l.getValue().toString();
							texts.add(value);
							//System.out.println(value);													
						}
						else if (attr.equals("http://image.ntua.gr/scriptontology/happensIn"))
						{
						}
					}
				}
			}
			
			StringBuilder shotText = new StringBuilder();
			for (String s : texts){
				shotText.append(s.toLowerCase() + " ");
			}
			//System.out.println(shotText);
			
			StanfordLemmatizer slem = new StanfordLemmatizer();
			List<String> shotLemmas = slem.lemmatize(shotText.toString());
			for (String s : shotLemmas){
			//	System.out.println(s);
				int index = Collections.binarySearch(dictionary, s);
			//	System.out.println(index);
				if ((index>=0) && (index<=dictionary.size())){
					temp4.set(index, 1);
				}
				else {
					//System.out.println("ERROR_1337 word " + s + " with index " + index + " not found");
				}
			}
			
			qeA.close();
			qeB.close();
			qeC.close();
			
			shotVector.addAll(temp4);shotVector.addAll(temp3);shotVector.addAll(temp2);shotVector.addAll(temp1);
//			for (int i =0 ; i<shotVector.size();i++){
//				if (shotVector.get(i) == 1)
//					System.out.println(i);
//			}
			finalVectors.add(shotVector);
			indexes.add(ind);
		//	System.out.println("Vector for shot " + shot.toString() + " added");

		//	break;}
		}
		
		labels.addAll(dictionary); labels.addAll(roles); labels.addAll(sceneClassList); labels.addAll(shotClassList);
		
//------------------------------------------------------------

		startTimes.set(1662, "01:11:26"); startTimes.set(1692, "01:12:39"); startTimes.set(749, "00:30:55");
		startTimes.set(1735, "01:14:07"); startTimes.set(1584, "01:08:14");
		String filename1 = "D:/eclipseWorkspace/Thesis/src/DBtimes.csv";
		Writer writer1 = null;
		try{
			writer1 = new FileWriter(filename1);
			int i = 0;
			for (String s : startTimes){
				int tmp = ArrayStuff.toSeconds(s);
				writer1.write(",\"" + tmp + "\"");
				writer1.write(System.getProperty("line.separator"));
				writer1.write("\"" + i + "\""); writer1.write(",\"" + tmp + "\"");				
				i++;
			}
			writer1.write("\"" + "5435" + "\"");
			
		}catch (IOException e1){
			e1.getMessage();
		}finally{
			try {writer1.close();} catch (Exception ex1) {}
		}
//------------------------------------------------------------
		return new MyResult(finalVectors, indexes,labels);
}
}