package justhalf.nlp.reader.acereader;

import java.util.List;

public abstract class ACEObject {
	
	public String id;
	
	public ACEObject(String id){
		this.id = id;
	}
	
	public abstract <T extends ACEObject, U extends ACEObjectMention<T>> List<U> mentions();
	public abstract <T extends ACEObjectType> T type();
	public abstract <T extends ACEObjectSubType> T subtype();
}
