package justhalf.nlp.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import justhalf.nlp.depparser.DepParser;
import justhalf.nlp.depparser.MedicalDepParser;
import justhalf.nlp.depparser.StanfordDepParser;
import justhalf.nlp.postagger.POSTagger;
import justhalf.nlp.postagger.StanfordPOSTagger;
import justhalf.nlp.tokenizer.StanfordTokenizer;
import justhalf.nlp.tokenizer.Tokenizer;

@RunWith(JUnitParamsRunner.class)
public class DepParserTest extends TestHelper {
	
	private static final String STANFORD =  "Stanford Dependency Parser ";
	private static final String UNIVERSAL = "Universal Dependency Parser";
	private static final String MEDICAL =   "Medical Dependency Parser  ";

	private static String[][] testCases = new String[][]{
		new String[]{"I work in Singapore.", "[nsubj(work-2, I-1), root(ROOT-0, work-2), case(Singapore-4, in-3), nmod:in(work-2, Singapore-4), punct(work-2, .-5)]"},
	};

	private static DepParser universalDepParser;
	private static DepParser stanfordDepParser;
	private static DepParser medicalDepParser;
	private static POSTagger posTagger;
	private static Tokenizer tokenizer;
	
	private static Map<String, Integer> counter;
	
	@BeforeClass
	public static void setUp(){
		tokenizer = new StanfordTokenizer();
		posTagger = new StanfordPOSTagger();
		universalDepParser = new StanfordDepParser(StanfordDepParser.UNIVERSAL_ENGLISH);
		stanfordDepParser = new StanfordDepParser(StanfordDepParser.STANDARD_ENGLISH);
		medicalDepParser = new MedicalDepParser();
		counter = new HashMap<String, Integer>();
	}
	
	@AfterClass
	public static void tearDown(){
		for(String depParser: counter.keySet()){
			System.out.println(String.format("[%s] got %d/%d tests correct!", depParser, counter.get(depParser), testCases.length));
		}
	}

	Object[] paramsForDepParser(){ return testCases; }
	
	@Test
	@Parameters(method="paramsForDepParser")
	public void testUniversalDepParser(String testCase, String expected){
		List<CoreLabel> posTagged = posTagger.tagCoreLabels(tokenizer.tokenize(testCase));
		testOne(universalDepParser, expected, posTagged);
		addCount(counter, UNIVERSAL);
	}

	@Test
	@Parameters(method="paramsForDepParser")
	public void testStanfordDepParser(String testCase, String expected){
		List<CoreLabel> posTagged = posTagger.tagCoreLabels(tokenizer.tokenize(testCase));
		testOne(stanfordDepParser, expected, posTagged);
		addCount(counter, STANFORD);
	}
	
	@Test
	@Parameters(method="paramsForDepParser")
	public void testMedicalDepParser(String testCase, String expected){
		List<CoreLabel> posTagged = posTagger.tagCoreLabels(tokenizer.tokenize(testCase));
		testOne(medicalDepParser, expected, posTagged);
		addCount(counter, MEDICAL);
	}
	
	private void testOne(DepParser posTagger, String expected, List<CoreLabel> testCase){
		List<TypedDependency> result = posTagger.parse(testCase);
		String actual = result.toString();
	    Assume.assumeTrue(messageOnNotEqual(expected, actual), expected.equals(actual));
	}
	
	private static void runOne(DepParser depParser, String testCase, String depParserName){
		List<CoreLabel> posTagged = posTagger.tagCoreLabels(tokenizer.tokenize(testCase));
		List<TypedDependency> dependencies = depParser.parse(posTagged);
		System.out.println(String.format("%s: %s", depParserName, dependencies));
	}
	
	public static void main(String[] args){
		setUp();
		Scanner sc = new Scanner(System.in);

		String line = "The horse raced past the barn fell.";
		runOne(universalDepParser, line, UNIVERSAL);
		runOne(stanfordDepParser, line, STANFORD);
		runOne(medicalDepParser, line, MEDICAL);
		System.out.println("Enter one sentence at a time");
		while((line = getNextLine(sc)) != null){
			runOne(universalDepParser, line, UNIVERSAL);
			runOne(stanfordDepParser, line, STANFORD);
			runOne(medicalDepParser, line, MEDICAL);
		}
		sc.close();
	}
}
