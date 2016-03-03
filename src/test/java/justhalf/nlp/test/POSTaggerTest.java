package justhalf.nlp.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.stanford.nlp.util.StringUtils;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import justhalf.nlp.postagger.NLP4JPOSTagger;
import justhalf.nlp.postagger.POSTagger;
import justhalf.nlp.postagger.StanfordPOSTagger;

@RunWith(JUnitParamsRunner.class)
public class POSTaggerTest extends TestHelper{
	
	private static final String NLP4J = "NLP4J POS Tagger";
	private static final String STANFORD = "Stanford POS Tagger";

	private static String[][] testCases = new String[][]{
		new String[]{"I", "PRP"},
		new String[]{"I am here .","PRP VBP RB ."},
		new String[]{"The horse raced past the barn fell .","DT NN VBN IN DT NN VBD ."},
		new String[]{"We are like sheep .","PRP VBP IN NN ."},
		new String[]{"We are all like sheep .","PRP VBP RB IN NN ."},
		new String[]{"Do you like sheep ?","VBP PRP VB NN ."},
		new String[]{"Time flies like an arrow .","NN VBZ IN DT NN ."},
		new String[]{"Fruit flies like bananas .","NN NNS VB NNS ."},
	};

	private static POSTagger nlp4jPOSTagger;
	private static POSTagger stanfordPOSTagger;
	
	private static Map<String, Integer> counter;
	
	@BeforeClass
	public static void setUp(){
		nlp4jPOSTagger = new NLP4JPOSTagger();
		stanfordPOSTagger = new StanfordPOSTagger();
		counter = new HashMap<String, Integer>();
	}
	
	@AfterClass
	public static void tearDown(){
		for(String posTagger: counter.keySet()){
			System.out.println(String.format("[%s] got %d/%d tests correct!", posTagger, counter.get(posTagger), testCases.length));
		}
	}

	Object[] paramsForPOSTagger(){ return testCases; }

	@Test
	@Parameters(method="paramsForPOSTagger")
	public void testNLP4JPOSTagger(String testCase, String expected){
		testOne(nlp4jPOSTagger, expected, testCase);
		addCount(counter, NLP4J);
	}
	
	@Test
	@Parameters(method="paramsForPOSTagger")
	public void testStanfordPOSTagger(String testCase, String expected){
		testOne(stanfordPOSTagger, expected, testCase);
		addCount(counter, STANFORD);
	}
	
	private void testOne(POSTagger posTagger, String expected, String testCase){
		String actual = StringUtils.join(posTagger.tag(Arrays.asList(testCase.split(" "))), " ");
	    Assume.assumeTrue(messageOnNotEqual(expected, actual), expected.equals(actual));
	}
	
	private static void runOne(POSTagger posTagger, String testCase, String posTaggerName){
		System.out.println(String.format("%20s: %s", posTaggerName, StringUtils.join(posTagger.tag(Arrays.asList(testCase.split(" "))), " ")));
	}
	
	public static void main(String[] args){
		setUp();
		Scanner sc = new Scanner(System.in);

		String line = "The horce raced past the barn fell .";
		runOne(nlp4jPOSTagger, line, NLP4J);
		runOne(stanfordPOSTagger, line, STANFORD);
		System.out.println("Enter words in a space-separated format");
		while((line = getNextLine(sc)) != null){
			runOne(nlp4jPOSTagger, line, NLP4J);
			runOne(stanfordPOSTagger, line, STANFORD);
		}
		sc.close();
	}
}
