package justhalf.nlp.tokenizer;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

public interface Tokenizer {
	
	/**
	 * Tokenize the given sentence into an array of String
	 * @param sentence
	 * @return
	 */
	public String[] tokenizeToString(String sentence);
	
	/**
	 * Tokenize the given sentence into a list of CoreLabel, which holds
	 * all information required to get the original string.
	 * @param sentence
	 * @return
	 */
	public List<CoreLabel> tokenize(String sentence);
}
