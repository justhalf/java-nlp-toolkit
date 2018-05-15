package justhalf.nlp.sentenceparser;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

/**
 * An implementation of {@link SentenceParser} from Stanford CoreNLP
 */
public class StanfordSentenceParser implements SentenceParser {
	
	/** The path to default lexical model for English */
	public static final String MODEL_LEXICAL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	
	private LexicalizedParser parser;
	
	public StanfordSentenceParser(){
		parser = LexicalizedParser.loadModel(MODEL_LEXICAL);
	}

	@Override
	public Tree parse(String sentence) {
		return parser.parse(sentence);
	}
	
	@Override
	public Tree parse(List<String> sentence) {
		return parser.parseStrings(sentence);
	}
	
	@Override
	public Tree parseCoreLabel(List<CoreLabel> sentence) {
		return parser.parse(sentence);
	}
	
	@Override
	public boolean isThreadSafe(){
		return true;
	}

}
