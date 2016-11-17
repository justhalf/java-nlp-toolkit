package justhalf.nlp.reader.acereader;

import java.io.Serializable;

/**
 * A helper data structure to store pure span
 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
 *
 */
public class Span implements Comparable<Span>, Serializable {
	
	private static final long serialVersionUID = -8289374024425020194L;
	public int start;
	public int end;

	public Span(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public boolean contains(Span s){
		return start <= s.start && end >= s.end;
	}
	
	public boolean equals(Object o){
		if(o instanceof Span){
			Span s = (Span)o;
			return start == s.start && end == s.end;
		}
		return false;
	}
	
	public String getText(String text){
		return text.substring(start, end);
	}
	
	public String toString(){
		return start+"-"+end;
	}

	@Override
	public int compareTo(Span o) {
		if(start < o.start) return -1;
		if(start > o.start) return 1;
		if(end < o.end) return 1;
		if(end > o.end) return -1;
		return 0;
	}

}
