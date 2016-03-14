package justhalf.nlp.depparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import edu.emory.clir.clearnlp.component.mode.dep.AbstractDEPParser;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;
import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;

public class MedicalDepParser implements DepParser {
	
	public static final Logger LOGGER = LogManager.getLogger(MedicalDepParser.class, StringFormatterMessageFactory.INSTANCE);
	
	public static final String DEFAULT_CONFIG_FILE = "clearnlp-config_decode_med_dep.xml";
	
	public AbstractDEPParser parser;
	public Language language;
	
	public MedicalDepParser(){
		this(DEFAULT_CONFIG_FILE, Language.English);
	}

	public MedicalDepParser(String configFile, Language language) {
		try {
			LOGGER.info("Initializing MedicalDepParser using the config %s", configFile);
			long start = System.nanoTime();
			this.language = language;
			DEPConfiguration config = new DEPConfiguration(IOUtils.getInputStreamFromURLOrClasspathOrFileSystem(configFile));
			GlobalLexica.init(IOUtils.getInputStreamFromURLOrClasspathOrFileSystem(configFile));
			parser = NLPUtils.getDEPParser(TLanguage.ENGLISH, "medical-en-dep.xz", config);
			long end = System.nanoTime();
			LOGGER.info("Initializing MedicalDepParser done in %.3fs", (end-start)/1e9);
		} catch (IOException e) {
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
		List<DEPNode> words = new ArrayList<DEPNode>();
		int id=0;
		for(CoreLabel wordLabel: sentence){
			id++;
			DEPNode node = new DEPNode(id, wordLabel.word(), wordLabel.tag(), new DEPFeat());
			words.add(node);
		}
		DEPTree tree = new DEPTree(words);
		
		parser.process(tree);
		
		// The rest of this method is to convert into Stanford TypedDependency
		
		IndexedWord root = new IndexedWord(new Word("ROOT"));
		root.setIndex(0);
		List<TypedDependency> dependencies = new ArrayList<TypedDependency>();
		for(int i=0; i<words.size(); i++){
			DEPNode curWord = words.get(i);
			DEPNode headWord = curWord.getHead();
			String label = curWord.getLabel();
			IndexedWord dep = new IndexedWord(sentence.get(curWord.getID()-1));
			dep.setIndex(curWord.getID());
			IndexedWord gov = headWord.getID() == 0
								? root
								: new IndexedWord(sentence.get(headWord.getID()-1));
			gov.setIndex(headWord.getID());
			GrammaticalRelation rel = headWord.getID() == 0
										? GrammaticalRelation.ROOT
										: makeGrammaticalRelation(label);
			TypedDependency deparc = new TypedDependency(rel, gov, dep);
			dependencies.add(deparc);
		}
		GrammaticalStructure gr = makeGrammaticalStructure(dependencies, new TreeGraphNode(root));
		return gr.typedDependenciesCCprocessed();
	}

	private GrammaticalRelation makeGrammaticalRelation(String label) {
		GrammaticalRelation stored = EnglishGrammaticalRelations.shortNameToGRel.get(label);
		if (stored != null)
			return stored;

		return new GrammaticalRelation(language, label, null, GrammaticalRelation.DEPENDENT);
	}

	private GrammaticalStructure makeGrammaticalStructure(List<TypedDependency> dependencies, TreeGraphNode rootNode) {
		return new EnglishGrammaticalStructure(dependencies, rootNode);
	}

	private void check(List<CoreLabel> sentence){
		for(CoreLabel word: sentence){
			if(word.tag() == null || word.tag().length() == 0){
				throw new IllegalStateException("MedicalDepParser requires every word in the sentence to have a POS tag");
			}
		}
	}

}
