package justhalf.nlp.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import justhalf.nlp.lemmatizer.Lemmatizer;
import justhalf.nlp.lemmatizer.NLP4JLemmatizer;

@RunWith(JUnitParamsRunner.class)
public class LemmatizerTest extends TestHelper {
	
	private static String[][] testCases = new String[][]{
		new String[]{"are", "be"},
		new String[]{"lay", "lie"},
		new String[]{"lay", "lay"},
		new String[]{"lay VB", "lay"},
		new String[]{"lay VBD", "lie"},
		new String[]{"laid", "lay"},
		new String[]{"staring", "stare"},
		new String[]{"starring", "star"},
		new String[]{"passers-by", "passer-by"},
		new String[]{"passersby", "passerby"},
	};
	
	private static Lemmatizer nlp4jLemmatizer;
	
	private static Map<String, Integer> counter;
	
	@BeforeClass
	public static void setUp(){
		nlp4jLemmatizer = new NLP4JLemmatizer();
		counter = new HashMap<String, Integer>();
	}
	
	@AfterClass
	public static void tearDown(){
		for(String lemmatizer: counter.keySet()){
			System.out.println(String.format("[%s] got %d/%d tests correct!", lemmatizer, counter.get(lemmatizer), testCases.length));
		}
	}

	Object[] paramsForLemmatizer(){ return testCases; }
	
	@Test
	@Parameters(method="paramsForLemmatizer")
	public void testLemmatizer(String testCase, String expected){
		testOne(nlp4jLemmatizer, expected, testCase);
		addCount(counter, "NLP4J Lemmatizer");
	}
	
	private void testOne(Lemmatizer lemmatizer, String expected, String testCase){
		String[] tokens = testCase.split(" ");
		String word = tokens[0];
		String pos = null;
		if(tokens.length > 1){
			pos = tokens[1];
		}
		String lemma = null;
		if(pos == null){
			lemma = lemmatizer.lemmatize(word);
		} else {
			lemma = lemmatizer.lemmatize(word, pos);
		}
		String actual = lemma;
	    Assume.assumeTrue(messageOnNotEqual(expected, actual), expected.equals(actual));
	}

	public static void main(String[] args){
		setUp();
		Scanner sc = new Scanner(System.in);
		String line;
		while((line = getNextLine(sc)) != null){
			String[] tokens = line.split(" ");
			String word = tokens[0];
			if(tokens.length > 1){
				String pos = tokens[1];
				System.out.println(nlp4jLemmatizer.lemmatize(word, pos));
			} else {
				System.out.println(nlp4jLemmatizer.lemmatize(word));
			}
		}
		sc.close();
	}
	
}
