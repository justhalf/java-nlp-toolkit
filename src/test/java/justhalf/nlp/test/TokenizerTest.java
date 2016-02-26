package justhalf.nlp.test;

import justhalf.nlp.tokenizer.StanfordTokenizer;
import justhalf.nlp.tokenizer.Tokenizer;

public class TokenizerTest {
	public static void main(String[] args){
		Tokenizer tokenizer = new StanfordTokenizer();
		String testString = "Apparently \"the thing's\" teeth are similar to (but not the same as) my dogs' teeth.";
		for(String token: tokenizer.tokenizeToString(testString)){
			System.out.print(token+" ");
		}
	}
}
