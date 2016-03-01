package justhalf.nlp.test;

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
import justhalf.nlp.tokenizer.RegexTokenizer;
import justhalf.nlp.tokenizer.StanfordTokenizer;
import justhalf.nlp.tokenizer.Tokenizer;
import justhalf.nlp.tokenizer.WhitespaceTokenizer;

@RunWith(JUnitParamsRunner.class)
public class TokenizerTest extends TestHelper {
	
	private static final String WHITESPACE = "Whitespace Tokenizer";
	private static final String REGEX = "Regex Tokenizer";
	private static final String STANFORD = "Stanford Tokenizer";

	private static String[][] testCases = new String[][]{
		new String[]{"Basic split", "Basic|split"},
		new String[]{"Handling punctuation.","Handling|punctuation|."},
		new String[]{ // Complex punctuation
				"Apparently \"the thing's\" teeth are similar to (but not the same as) my dogs' teeth.",
				"Apparently|\"|the|thing|'s|\"|teeth|are|similar|to|(|but|not|the|same|as|)|my|dogs|'|teeth|."},
		new String[]{ // Non-ascii
				"Lol i mean wah, 又是我做坏人.",
				"Lol|i|mean|wah|,|又是我做坏人|."},
		new String[]{ // Emoticons
				"I'm so sad le:-(",
				"I|'m|so|sad|le|:-("},
	};

	private static Tokenizer whitespaceTokenizer;
	private static Tokenizer regexTokenizer;
	private static Tokenizer stanfordTokenizer;
	
	private static Map<String, Integer> counter;
	
	@BeforeClass
	public static void setUp(){
		whitespaceTokenizer = new WhitespaceTokenizer();
		regexTokenizer = new RegexTokenizer();
		stanfordTokenizer = new StanfordTokenizer();
		counter = new HashMap<String, Integer>();
	}
	
	@AfterClass
	public static void tearDown(){
		for(String lemmatizer: counter.keySet()){
			System.out.println(String.format("[%s] got %d/%d tests correct!", lemmatizer, counter.get(lemmatizer), testCases.length));
		}
	}

	Object[] paramsForTokenizer(){ return testCases; }

	@Test
	@Parameters(method="paramsForTokenizer")
	public void testWhitespaceTokenizer(String testCase, String expected){
		testOne(whitespaceTokenizer, expected, testCase);
		addCount(counter, WHITESPACE);
	}
	
	@Test
	@Parameters(method="paramsForTokenizer")
	public void testRegexTokenizer(String testCase, String expected){
		testOne(regexTokenizer, expected, testCase);
		addCount(counter, REGEX);
	}
	
	@Test
	@Parameters(method="paramsForTokenizer")
	public void testStanfordTokenizer(String testCase, String expected){
		testOne(stanfordTokenizer, expected, testCase);
		addCount(counter, STANFORD);
	}
	
	private void testOne(Tokenizer tokenizer, String expected, String testCase){
		String actual = StringUtils.join(tokenizer.tokenizeToString(testCase), "|");
	    Assume.assumeTrue(messageOnNotEqual(expected, actual), expected.equals(actual));
	}
	
	private static void runOne(Tokenizer tokenizer, String testCase, String tokenizerName){
		System.out.println(String.format("%s: %s", tokenizerName, StringUtils.join(tokenizer.tokenizeToString(testCase), "|")));
	}
	
	public static void main(String[] args){
		setUp();
		Scanner sc = new Scanner(System.in);

		String line = "Apparently \"the thing's\" teeth are similar to (but not the same as) my dogs' teeth.";
		runOne(whitespaceTokenizer, line, WHITESPACE);
		runOne(regexTokenizer, line, REGEX);
		runOne(stanfordTokenizer, line, STANFORD);
		while((line = getNextLine(sc)) != null){
			runOne(whitespaceTokenizer, line, WHITESPACE);
			runOne(regexTokenizer, line, REGEX);
			runOne(stanfordTokenizer, line, STANFORD);
		}
		sc.close();
	}
}
