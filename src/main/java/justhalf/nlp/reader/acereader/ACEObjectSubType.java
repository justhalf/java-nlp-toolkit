package justhalf.nlp.reader.acereader;

public interface ACEObjectSubType extends ACEEventArgumentType {
	public default boolean satisfiedBy(ACEEventArgumentType givenType){
		return this == givenType;
	}
}