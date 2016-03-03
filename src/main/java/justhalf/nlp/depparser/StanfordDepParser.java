package justhalf.nlp.depparser;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class StanfordDepParser implements DepParser {
	
	public DependencyParser dependencyParser;

	public StanfordDepParser() {
		dependencyParser = DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public List<TypedDependency> parse(List<CoreLabel> sentence) {
		check(sentence);
		GrammaticalStructure structure = dependencyParser.predict(sentence);
		return structure.typedDependenciesCCprocessed();
	}
	
	private void check(List<CoreLabel> sentence){
		for(CoreLabel word: sentence){
			if(word.tag() == null || word.tag().length() == 0){
				throw new IllegalStateException("StanfordDepParser requires every word in the sentence to have a POS tag");
			}
		}
	}

}
