package justhalf.nlp.tokenizer;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import justhalf.nlp.NLPInterface;

/**
 * An interface for tokenizers
 */
public interface Tokenizer extends NLPInterface{
	
	/**
	 * Tokenize the given sentence into an array of String
	 * @param sentence
	 * 		The sentence to be tokenized
	 * @return
	 * 		The list of tokens
	 */
	public String[] tokenizeToString(String sentence);
	
	/**
	 * Tokenize the given sentence into a list of CoreLabel, which holds
	 * all information required to get the original string.
	 * @param sentence
	 * 		The sentence to be tokenized
	 * @return
	 * 		The list of tokens as {@link CoreLabel} objects<br>
	 * 		Each object holds the spacing information surrounding the token it represents,
	 * 		enabling faithful restoration of the original string.
	 */
	public List<CoreLabel> tokenize(String sentence);
}
