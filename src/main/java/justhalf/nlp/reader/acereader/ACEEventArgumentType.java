package justhalf.nlp.reader.acereader;

import justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueType;

public interface ACEEventArgumentType {
	/**
	 * Whether this argument type is satisfied by the given type.<br>
	 * For example, the type {@link ACEValueType#NUMERIC} is satisfied by 
	 * {@link ACEValueType#NUMERIC} and also by
	 * {@link ACEValueSubType#NUMERIC_MONEY} 
	 * @param givenType
	 * @return
	 */
	public boolean satisfiedBy(ACEEventArgumentType givenType);
}
