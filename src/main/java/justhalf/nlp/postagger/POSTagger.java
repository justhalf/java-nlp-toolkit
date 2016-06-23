package justhalf.nlp.postagger;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import justhalf.nlp.NLPInterface;

/**
 * An interface for POS taggers
 */
public interface POSTagger extends NLPInterface{
	/**
	 * Tokenize and tag the sentence with POS tags
	 * @param sentence
	 * 		The input sentence to be POS-tagged.
	 * 		This assumes that the sentence has not been tokenized.
	 * @return
	 * 		The list of POS tags as strings
	 */
	public List<String> tag(String sentence);
	
	/**
	 * Tag the sequence of words with POS tags
	 * @param sentence
	 * 		The tokenized input sentence to be POS-tagged.
	 * @return
	 * 		The list of POS tags as strings
	 */
	public List<String> tag(List<String> sentence);
	
	/**
	 * Tag the sequence of CoreLabels
	 * @param sentence
	 * 		The input sentence to be POS-tagged, as {@link CoreLabel} objects
	 * @return
	 * 		The input list of CoreLabel, with the {@link CoreLabel#tag()} set
	 */
	public List<CoreLabel> tagCoreLabels(List<CoreLabel> sentence);
}
