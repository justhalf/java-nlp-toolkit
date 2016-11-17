package justhalf.nlp.reader.acereader;

import java.util.List;

public interface ACEObjectType extends ACEEventArgumentType {
	/**
	 * Return the list of subtypes of this type
	 * @return
	 */
	public List<? extends ACEObjectSubType> subtypes();
	
	public default boolean satisfiedBy(ACEEventArgumentType givenType){
		return this == givenType || subtypes().contains(givenType);
	}
}