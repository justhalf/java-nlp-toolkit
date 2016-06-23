package justhalf.nlp.tokenizer;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * A very simple implementation of {@link Tokenizer} by splitting the input on whitespaces.<br>
 * 
 * Note that this does not split on special characters which resemble whitespace such as
 * non-breaking space (&amp;nbsp;)
 */
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
	
	@Override
	public boolean isThreadSafe(){
		return true;
	}

}
