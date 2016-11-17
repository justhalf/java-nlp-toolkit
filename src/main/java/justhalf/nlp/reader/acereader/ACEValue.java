package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.List;

public class ACEValue extends ACEObject {
	
	public static enum ACEValueType implements ACEObjectType {
		NUMERIC("Numeric"),
		CONTACT_INFO("Contact-Info"),
		TIME("Time"),
		CRIME("Crime"),
		SENTENCE("Sentence"),
		JOB_TITLE("Job-Title"),
		;
		
		public final String text;
		private List<ACEValueSubType> subtypes;
		
		private ACEValueType(String text){
			this.text = text;
		}
		
		public List<ACEValueSubType> subtypes(){
			if(subtypes == null){
				List<ACEValueSubType> subtypeList = new ArrayList<ACEValueSubType>();
				for(ACEValueSubType subtype: ACEValueSubType.values()){
					if(subtype.type == this){
						subtypeList.add(subtype);
					}
				}
				subtypes = subtypeList;
			}
			return subtypes;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public static enum ACEValueSubType implements ACEObjectSubType {
		NUMERIC_PERCENT("Percent", ACEValueType.NUMERIC),
		NUMERIC_MONEY("Money", ACEValueType.NUMERIC),
		
		CONTACT_INFO_PHONE_NUMBER("Phone-Number", ACEValueType.CONTACT_INFO),
		CONTACT_INFO_E_MAIL("E-mail", ACEValueType.CONTACT_INFO),
		CONTACT_INFO_URL("URL", ACEValueType.CONTACT_INFO),
		
		TIME_TIME("", ACEValueType.TIME),
		
		CRIME_CRIME("", ACEValueType.CRIME),
		
		SENTENCE_SENTENCE("", ACEValueType.SENTENCE),
		
		JOB_TITLE_JOB_TITLE("", ACEValueType.JOB_TITLE),
		;
		
		public final String text;
		public final ACEValueType type;
		
		private ACEValueSubType(String text, ACEValueType type){
			this.text = text;
			this.type = type;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public String id;
	public ACEValueType type;
	public ACEValueSubType subtype;
	public List<ACEValueMention> mentions;

	public ACEValue(String id, String type, String subtype) {
		super(id);
		this.type = ACEValueType.valueOf(type.toUpperCase().replace("-", "_"));
		if(subtype.length() == 0){
			subtype = type;
		}
		this.subtype = ACEValueSubType.valueOf((type+"_"+subtype).toUpperCase().replace("-", "_"));
		this.mentions = new ArrayList<ACEValueMention>();
	}
	
	public void addMention(ACEValueMention mention){
		this.mentions.add(mention);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACEValueMention> mentions() {
		return mentions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEValueType type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEValueSubType subtype() {
		return subtype;
	}

}
