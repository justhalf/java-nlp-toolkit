package justhalf.nlp.lemmatizer;

public interface Lemmatizer {
	public String lemmatize(String word);
	public String lemmatize(String word, String pos);
}
