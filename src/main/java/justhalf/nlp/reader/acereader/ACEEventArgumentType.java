package justhalf.nlp.reader.acereader;

public interface ACEEventArgumentType {
	/**
	 * Whether this argument type is satisfied by the given type.<br>
	 * For example, the type {@link justhalf.nlp.reader.acereader.ACEValue.ACEValueType#NUMERIC} is satisfied by 
	 * {@link justhalf.nlp.reader.acereader.ACEValue.ACEValueType#NUMERIC} and also by
	 * {@link justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType#NUMERIC_MONEY} 
	 * @param givenType
	 * @return
	 */
	public boolean satisfiedBy(ACEEventArgumentType givenType);
}
