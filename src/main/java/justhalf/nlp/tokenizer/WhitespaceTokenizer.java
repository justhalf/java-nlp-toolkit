package justhalf.nlp.tokenizer;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

public class WhitespaceTokenizer implements Tokenizer {
	
	private RegexTokenizer regexTokenizer = new RegexTokenizer("[ \\t\\r\\n]+");

	@Override
	public String[] tokenizeToString(String sentence) {
		return regexTokenizer.tokenizeToString(sentence);
	}

	@Override
	public List<CoreLabel> tokenize(String sentence) {
		return regexTokenizer.tokenize(sentence);
	}

}
