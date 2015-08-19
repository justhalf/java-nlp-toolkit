package justhalf.nlp.tokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;

public class StanfordTokenizer implements Tokenizer {
	
	/**
	 * Factory for the tokenizer
	 */
	private PTBTokenizerFactory<CoreLabel> factory;
	
	public StanfordTokenizer(){
		factory = PTBTokenizerFactory.newCoreLabelTokenizerFactory("normalizeParentheses=false,"
																+ "normalizeOtherBrackets=false,"
																+ "latexQuotes=false,"
																+ "unicodeQuotes=true,"
																+ "invertible=true");
	}
	
	public String[] tokenizeToString(String sentence){
		List<CoreLabel> tokens = tokenize(sentence);
		List<String> result = new ArrayList<String>();
		for(CoreLabel token: tokens){
			result.add(token.word());
		}
		return result.toArray(new String[result.size()]);
	}
	
	public List<CoreLabel> tokenize(String sentence){
		StringReader reader = new StringReader(sentence);
		PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>)factory.getTokenizer(reader);
		List<CoreLabel> tokens = tokenizer.tokenize();
		return tokens;
	}
}
