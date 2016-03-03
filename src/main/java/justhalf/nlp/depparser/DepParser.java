package justhalf.nlp.depparser;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;
import justhalf.nlp.NLPInterface;

public interface DepParser extends NLPInterface {
	public List<TypedDependency> parse(List<CoreLabel> sentence);
}
