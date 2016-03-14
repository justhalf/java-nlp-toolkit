package justhalf.nlp.lemmatizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;

public class BioLemmatizer extends EnglishLemmatizer {
	
	public static final Logger LOGGER = LogManager.getLogger(BioLemmatizer.class,
															StringFormatterMessageFactory.INSTANCE);
	
	edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer lemmatizer;

	public BioLemmatizer() {
		LOGGER.info("Loading BioLemmatizer...");
		long start = System.nanoTime();
		lemmatizer = new edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer();
		long end = System.nanoTime();
		LOGGER.info("Loading BioLemmatizer done in %.3fs", (end-start)/1e9);
	}

	@Override
	public String lemmatize(String word) {
		for(String pos: new String[]{"VBZ", "NNS", "VBD", "VBG", "JJR", "JJS", "RBR", "RBS"}){
			String lemma = lemmatize(word, pos);
			if(!lemma.equals(word)){
				return lemma;
			}
		}
		return word;
	}

	@Override
	public String lemmatize(String word, String pos) {
		LemmataEntry entry = lemmatizer.lemmatizeByLexiconAndRules(word, pos);
		return entry.getLemmas().iterator().next().getLemma();
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}

}
