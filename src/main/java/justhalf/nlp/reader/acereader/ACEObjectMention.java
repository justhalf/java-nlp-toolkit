package justhalf.nlp.reader.acereader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ACEObjectMention<T extends ACEObject> {
	
	private static final Pattern SHORT_ID_PATTERN = Pattern.compile("(\\d+-(\\d+))$");

	String id;
	String fullID;
	String shortID;
	/** The main span of this object. */
	public Span span;
	public String text;
	public T parent;
	
	public ACEObjectMention(String id, Span span, String text, T parent) {
		this.id = id;
		this.span = span;
		this.text = text;
		this.parent = parent;
	}
	
	public String getText(String text){
		return span.getText(text);
	}
	
	public String getFullID(){
		if(this.fullID == null){
			setupIDs();
		}
		return this.fullID;
	}
	
	public String getShortID(){
		if(this.shortID == null){
			setupIDs();
		}
		return this.shortID;
	}
	
	public T getParentObject(){
		return parent;
	}
	
	public String getParentID(){
		return parent.id;
	}
	
	public void setupIDs(){
		String timexID = getParentID();
		Matcher matcher = SHORT_ID_PATTERN.matcher(this.id);
		if(matcher.find()){
			this.shortID = matcher.group(1);
			this.fullID = timexID+"-"+matcher.group(2);
		} else {
			throw new RuntimeException("Unrecognized timex mention ID pattern: "+this.id);
		}
	}
	
	public String toString(String text){
		return this.toString()+": "+this.getText(text);
	}

}
