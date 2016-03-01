package justhalf.nlp.lemmatizer;

import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;

public class NLP4JLemmatizer extends EnglishLemmatizer{
	
	private MorphAnalyzer lemmatizer;
	
	public NLP4JLemmatizer() {
		 lemmatizer = new EnglishMorphAnalyzer();
	}

	@Override
	public String lemmatize(String word) {
		for(String pos: new String[]{"VBZ", "NN", "NNS", "VB", "VBD", "VBG", "JJR", "JJS", "RB", "RBR", "RBS"}){
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

}
