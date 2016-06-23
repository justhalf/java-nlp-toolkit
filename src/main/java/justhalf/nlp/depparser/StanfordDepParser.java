package justhalf.nlp.depparser;

import java.lang.reflect.Field;
import java.util.List;

import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * An implementation of {@link DepParser} using Stanford CoreNLP
 */
public class StanfordDepParser implements DepParser {
	
	/** The path to default dependency parser model for English with standard labels */
	public static final String STANDARD_ENGLISH = "edu/stanford/nlp/models/parser/nndep/english_SD.gz";

	/**
	 * The path to default dependency parser model for English with universal labels<br>
	 * See <a href="http://universaldependencies.org/">http://universaldependencies.org/</a> for more information
	 * on Universal Dependencies
	 */
	public static final String UNIVERSAL_ENGLISH = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";
	
	public DependencyParser dependencyParser;

	public StanfordDepParser() {
		this(DependencyParser.DEFAULT_MODEL);
	}
	
	public StanfordDepParser(String modelPath){
		dependencyParser = DependencyParser.loadFromModelFile(modelPath);
		Field _lang;
		try {
			_lang = DependencyParser.class.getDeclaredField("language");
			_lang.setAccessible(true);
			if(modelPath == STANDARD_ENGLISH){
				_lang.set(dependencyParser, Language.English);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
