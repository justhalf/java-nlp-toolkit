package justhalf.nlp.postagger;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import justhalf.nlp.NLPInterface;

public interface POSTagger extends NLPInterface{
	/**
	 * Tokenize and tag the sentence with POS tags
	 * @param sentence
	 * @return
	 */
	public List<String> tag(String sentence);
	
	/**
	 * Tag the sequence of words with POS tags
	 * @param sentence
	 * @return
	 */
	public List<String> tag(List<String> sentence);
	
	/**
	 * Tag the sequence of CoreLabels
	 * @param sentence
	 * @return
	 */
	public List<CoreLabel> tagCoreLabels(List<CoreLabel> sentence);
}
