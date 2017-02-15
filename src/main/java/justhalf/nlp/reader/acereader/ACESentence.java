/**
 * 
 */
package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sentence in ACEDocument
 */
public class ACESentence {

	public ACEDocument sourceDoc;
	public Span span;
	public String text;
	public List<ACEEntityMention> entities;
	public List<ACERelationMention> relations;
	public List<ACEEventMention> events;
	public List<ACETimexMention> timexes;
	public List<ACEValueMention> values;
	
	public ACESentence(ACEDocument sourceDoc, Span span, String text){
		this(sourceDoc, span, text, null, null, null, null, null);
	}

	public ACESentence(ACEDocument sourceDoc, Span span, String text, List<ACEEntityMention> entities, List<ACERelationMention> relations, List<ACEEventMention> events, List<ACETimexMention> timexes, List<ACEValueMention> values) {
		this.sourceDoc = sourceDoc;
		this.span = span;
		this.text = text;
		this.entities = avoidNull(entities);
		this.relations = avoidNull(relations);
		this.events = avoidNull(events);
		this.timexes = avoidNull(timexes);
		this.values = avoidNull(values);
	}
	
	private <T> List<T> avoidNull(List<T> list){
		if(list == null){
			list = new ArrayList<T>();
		}
		return list;
	}
	
	public void addEntityMention(ACEEntityMention mention){
		this.entities.add(mention);
	}
	
	public void addRelationMention(ACERelationMention mention){
		this.relations.add(mention);
	}
	
	public void addEventMention(ACEEventMention mention){
		this.events.add(mention);
	}
	
	public void addTimexMention(ACETimexMention mention){
		this.timexes.add(mention);
	}
	
	public void addValueMention(ACEValueMention mention){
		this.values.add(mention);
	}
	
}
