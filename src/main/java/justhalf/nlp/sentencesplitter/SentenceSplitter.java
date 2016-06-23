package justhalf.nlp.sentencesplitter;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import justhalf.nlp.NLPInterface;

/**
 * An interface for sentence splitters
 */
public interface SentenceSplitter extends NLPInterface{
	/**
	 * Split a text into a list of sentences as string.
	 * @param input
	 * 		A text which contains possibly multiple sentences.
	 * @return
	 * 		List of strings, each representing a sentence found in the input
	 */
	public String[] splitToString(String input);
	
	/**
	 * Split a text into a list of sentences.<br>
	 * 
	 * Each CoreLabel corresponds to one sentence.
	 * @param input
	 * 		A text which contains possibly multiple sentences. 
	 * @return
	 * 		List of {@link CoreLabel} objects, each representing a sentence found in the input
	 */
	public List<CoreLabel> split(String input);
}
