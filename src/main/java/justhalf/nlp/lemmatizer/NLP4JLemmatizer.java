package justhalf.nlp.lemmatizer;

import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;

/**
 * An implementation of {@link Lemmatizer} using NLP4J
 */
public class NLP4JLemmatizer extends EnglishLemmatizer{
	
	private static MorphAnalyzer lemmatizer;
	
	public NLP4JLemmatizer() {
		getMorphAnalyzer();
	}

	@Override
	public String lemmatize(String word) {
		for(String pos: new String[]{"VBZ", "NNS", "VBD", "VBG", "JJR", "JJS", "RBR", "RBS"}){
			String lemma = lemmatizer.lemmatize(word, pos);
			if(!lemma.equals(word)){
				return lemma;
			}
		}
		return word;
	}

	@Override
	public String lemmatize(String word, String pos) {
		String lemma = lemmatizer.lemmatize(word, pos);
		return lemma;
	}
	
	@Override
	public boolean isThreadSafe(){
		return true;
	}
	
	/**
	 * Since the lemmatizer from NLP4J is thread-safe, we use singleton pattern,
	 * and this method will return the singleton object of lemmatizer from NLP4J,
	 * properly initializing it first if it has not been initialized.
	 * @return
	 * 		The internal lemmatizer from NLP4J
	 */
	public static MorphAnalyzer getMorphAnalyzer(){
		if(lemmatizer == null){
			synchronized (MorphAnalyzer.class){
				if(lemmatizer == null){
					 lemmatizer = new EnglishMorphAnalyzer();					
				}
			}
		}
		return lemmatizer;
	}

}
