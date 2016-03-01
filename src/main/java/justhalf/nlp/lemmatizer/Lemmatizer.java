package justhalf.nlp.lemmatizer;

import justhalf.nlp.NLPInterface;

public interface Lemmatizer extends NLPInterface{
	public String lemmatize(String word);
	public String lemmatize(String word, String pos);
}
