package justhalf.nlp.depparser;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;
import justhalf.nlp.NLPInterface;

public interface DepParser extends NLPInterface {
	/**
	 * Parse the given sentence, presented as list of {@link CoreLabel}
	 * @param sentence
	 * 		The input sentence, where each word is represented as a {@link CoreLabel} object
	 * @return
	 * 		The list of labeled dependencies
	 */
	public List<TypedDependency> parse(List<CoreLabel> sentence);
}
