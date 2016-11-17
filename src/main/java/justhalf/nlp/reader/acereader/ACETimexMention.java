package justhalf.nlp.reader.acereader;

import edu.stanford.nlp.util.Comparators;

public class ACETimexMention extends ACEObjectMention<ACETimex> implements Comparable<ACETimexMention>{
	
	public ACETimex timex;

	public ACETimexMention(String id, Span span, String text, ACETimex timex) {
		super(id, span, text, timex);
		this.timex = timex;
	}

	@Override
	public String getParentID() {
		return timex.id;
	}
	
	public boolean equals(Object o){
		if(o instanceof ACETimexMention){
			ACETimexMention s = (ACETimexMention)o;
			if(!span.equals(s.span)) return false;
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(ACETimexMention o) {
		int result = Comparators.nullSafeCompare(span, o.span);
		if(result != 0) return result;
		return Comparators.nullSafeCompare(getFullID(), o.getFullID());
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("["+span.toString()+"]");
		result.append("[ID="+this.id+"]");
		return result.toString();
	}

}
