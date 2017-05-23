package justhalf.nlp.reader.acereader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.util.XMLUtils;
import justhalf.nlp.reader.acereader.ACERelation.ACERelationType;

/**
 * <p>Represents an ACE document</p>
 * 
 * <p>The data structure defined here is based on the specifications found here:
 * <a href="https://www.ldc.upenn.edu/collaborations/past-projects/ace/annotation-tasks-and-specifications">
 * https://www.ldc.upenn.edu/collaborations/past-projects/ace/annotation-tasks-and-specifications</a></p>
 * 
 * <p>This class can represent either documents from ACE 2004 and ACE 2005, as marked by the flag
 * {@link #versionIsACE2004}, which is detected automatically based on the XML header of the
 * source SGM file.</p>
 * 
 * <p>The flag {@link #textInLowercase} marks whether the original source text is all in lowercase,
 * as in the case for Fisher transcript corpus in ACE 2004.</p>
 * 
 * There are various list of canonical entities, grouped by class:
 * <ul>
 * <li>{@link #entities}: for named entities</li>
 * <li>{@link #relations}: for relations</li>
 * <li>{@link #events}: for events (only in ACE 2005)</li>
 * <li>{@link #timexes}: for time expression (only in ACE 2005)</li>
 * <li>{@link #values}: for other values (only in ACE 2005)</li>
 * </ul>
 * 
 * And also the associated mentions:
 * <ul>
 * <li>{@link #entityMentions}: for named entities</li>
 * <li>{@link #relationMentions}: for relations</li>
 * <li>{@link #eventMentions}: for events (only in ACE 2005)</li>
 * <li>{@link #timexMentions}: for time expression (only in ACE 2005)</li>
 * <li>{@link #valueMentions}: for other values (only in ACE 2005)</li>
 * </ul>
 * 
 * <p>The canonical entities typically contain a list of mentions (except {@link ACERelationType#METONYMY}
 * relations), which will actually mark the relevant spans in the text.
 * 
 * Maps of entity IDs and entity mention IDs to the corresponding objects are
 * available as {@link #objectsById} and {@link #objectMentionsById}.
 * </p>
 * 
 * <p>The {@link #uri} stores the filename as given in the URI attribute in source_file tag
 * in the APF file.</p>
 * 
 * <p>The text (the relevant annotated texts) and full text (everything in the document) are available as
 * {@link #text} and {@link #fullText}.</p>
 * 
 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
 *
 */
public class ACEDocument implements Serializable{
	
	private static final boolean CHECK_ESCAPED_ENTITIES = false;
	private static final boolean CHECK_OFFSET_TEXT = false;
	private static final boolean CHECK_OOB_MENTIONS = false;
	private static final boolean REMOVE_OOB_MENTIONS = true;
	private static final boolean TEST_STRICT_PARSING = false;
	private static final long serialVersionUID = -4698300709681532759L;

	public String text;
	public String fullText;
	public int offset;
	
	public String uri;
	public boolean versionIsACE2004;
	public boolean textInLowercase;
	public List<ACEEntity> entities;
	public List<ACEEntityMention> entityMentions;
	public List<ACEValue> values;
	public List<ACEValueMention> valueMentions;
	public List<ACETimex> timexes;
	public List<ACETimexMention> timexMentions;
	public List<ACERelation> relations;
	public List<ACERelationMention> relationMentions;
	public List<ACEEvent> events;
	public List<ACEEventMention> eventMentions;
	public Map<String, ACEObject> objectsById;
	public Map<String, ACEObjectMention<? extends ACEObject>> objectMentionsById;
	
	public ACEDocument(String sgmFilename) throws IOException, SAXException {
		this(sgmFilename, false);
	}
	
	public ACEDocument(String sgmFilename, boolean excludeMetadata) throws IOException, SAXException {
		this(sgmFilename, sgmFilename.replace(".sgm", ".apf.xml"), excludeMetadata);
	}
	
	public ACEDocument(String sgmFilename, String apfFilename, boolean excludeMetadata) throws IOException, SAXException {
		this(IOUtils.getInputStreamFromURLOrClasspathOrFileSystem(sgmFilename),
			 IOUtils.getInputStreamFromURLOrClasspathOrFileSystem(apfFilename),
			 excludeMetadata);
	}
	
	/**
	 * Read an ACE document from the given source sgmStream and annotations apfStream.<br>
	 * 
	 * @param sgmStream
	 * @param apfStream
	 * @param excludeMetadata
	 * @throws IOException
	 * @throws SAXException
	 */
	public ACEDocument(InputStream sgmStream, InputStream apfStream, boolean excludeMetadata) throws IOException, SAXException{
		DOMParser parser = new DOMParser();
		String sgmText = IOUtils.slurpInputStream(sgmStream, "UTF-8");
		sgmText = sgmText.replaceAll("<(/)?BODY>", "<$1BODY_TEXT>");
		parser.parse(new InputSource(new StringReader(sgmText)));
		Document sgm = parser.getDocument();
		if(TEST_STRICT_PARSING){
			DocumentBuilder docBuilder = XMLUtils.getXmlParser();
			docBuilder.parse(new InputSource(new StringReader(sgmText)));
		}
		this.fullText = unescape(sgm.getDocumentElement().getTextContent());
		if(excludeMetadata){
			// This should supposedly be TEXT tag, but some annotations are present even outside the TEXT tag
			this.text = unescape(sgm.getElementsByTagName("BODY_TEXT").item(0).getTextContent());
		} else {
			this.text = this.fullText;
		}
		this.textInLowercase = this.text.equals(this.text.toLowerCase());
		this.offset = fullText.indexOf(text);
		
		this.entities = new ArrayList<ACEEntity>();
		this.entityMentions = new ArrayList<ACEEntityMention>();
		this.values = new ArrayList<ACEValue>();
		this.valueMentions = new ArrayList<ACEValueMention>();
		this.timexes = new ArrayList<ACETimex>();
		this.timexMentions = new ArrayList<ACETimexMention>();
		this.relations = new ArrayList<ACERelation>();
		this.relationMentions = new ArrayList<ACERelationMention>();
		this.events = new ArrayList<ACEEvent>();
		this.eventMentions = new ArrayList<ACEEventMention>();
		
		this.objectsById = new HashMap<String, ACEObject>();
		this.objectMentionsById = new HashMap<String, ACEObjectMention<? extends ACEObject>>();
		String apfText = IOUtils.slurpInputStream(apfStream, "UTF-8");
		apfText = apfText.replaceAll("<(/)?head>", "<$1head_extent>");
		parser = new DOMParser();
		parser.parse(new InputSource(new StringReader(apfText)));
		Document apf = parser.getDocument();
		setMetadata(apf);
		extractEntities(apf);
		extractValues(apf);
		extractTimexes(apf);
		extractRelations(apf);
		extractEvents(apf);
	}
	
	private void setMetadata(Document apf){
		NamedNodeMap sourceAttributes = apf.getElementsByTagName("SOURCE_FILE").item(0).getAttributes();
		String version = getAttribute(sourceAttributes, "VERSION");
		this.versionIsACE2004 = version.equals("4.0"); // ACE 2005 doesn't have version
		this.uri = getAttribute(sourceAttributes, "URI");
	}
	
	private Span getSpan(Node charseq){
		NamedNodeMap attributes = charseq.getAttributes();
		int start = Integer.parseInt(getAttribute(attributes, "START"));
		int end = Integer.parseInt(getAttribute(attributes, "END"))+1;
		start -= this.offset;
		end -= this.offset;
		return new Span(start, end);
	}

	private void extractEntities(Document apf) throws NumberFormatException, DOMException {
		NodeList entities = apf.getElementsByTagName("ENTITY");
		for(int i=0; i<entities.getLength(); i++){
			Node entity = entities.item(i);
			NamedNodeMap attributes = entity.getAttributes();
			String id = getAttribute(attributes, "ID");
			String type = getAttribute(attributes, "TYPE");
			String subtype = getAttribute(attributes, "SUBTYPE");
			String entityClass = getAttribute(attributes, "CLASS");
			ACEEntity aceEntity = new ACEEntity(id, type, subtype, entityClass);
			NodeList entityMentions = ((Element)entity).getElementsByTagName("ENTITY_MENTION");
			for(int j=0; j<entityMentions.getLength(); j++){
				Node entityMention = entityMentions.item(j);
				ACEEntityMention mention = getMention(entityMention, aceEntity);
				aceEntity.addMention(mention);
				this.entityMentions.add(mention);
				this.objectMentionsById.put(mention.getFullID(), mention);
			}
			this.entities.add(aceEntity);
			this.objectsById.put(id, aceEntity);
		}
		Collections.sort(this.entityMentions);
		checkAndFixMentions(this.entityMentions);
	}
	
	private void checkAndFixMentions(List<? extends ACEObjectMention<?>> mentions){
		int lastDiff = 0;
		List<ACEObjectMention<?>> toBeRemoved = new ArrayList<ACEObjectMention<?>>();
		for(ACEObjectMention<?> mention: mentions){
			if(mention instanceof ACEEntityMention){
				fixSpan(lastDiff, toBeRemoved, mention, ((ACEEntityMention)mention).headSpan, ((ACEEntityMention)mention).headText);
			}
			lastDiff = fixSpan(lastDiff, toBeRemoved, mention, mention.span, mention.text);
		}
		for(ACEObjectMention<?> mention: toBeRemoved){
			mentions.remove(mention);
			if(mention instanceof ACEEntityMention){
				((ACEEntityMention)mention).entity.mentions.remove(mention);
			} else if(mention instanceof ACERelationMention){
				((ACERelationMention)mention).relation.mentions.remove(mention);
			} else if(mention instanceof ACETimexMention){
				((ACETimexMention)mention).timex.mentions.remove(mention);
			}
		}
		if(toBeRemoved.size() > 0){
			System.out.println("Removed "+toBeRemoved.size()+" out-of-bounds mentions from "+uri);
		}
	}

	private int fixSpan(int lastDiff, List<ACEObjectMention<?>> toBeRemoved, ACEObjectMention<?> mention,
			Span span, String text) throws RuntimeException {
		String originalText = text;
		String unescapedOriginalText = unescape(originalText);
		String actualText = null;
		try{
			actualText = span.getText(this.text);
		} catch (StringIndexOutOfBoundsException e){
			actualText = "";
			if(CHECK_OOB_MENTIONS){
				if(!(mention instanceof ACETimexMention)){
					System.out.printf("%-45s[%d,%d]: %s\n", mention.getFullID(), span.start, span.end, unescapedOriginalText.replace("\n", " "));
				}
			}
		}
		if(CHECK_ESCAPED_ENTITIES){
			if(unescapedOriginalText.contains("&")){
				System.out.println(unescapedOriginalText.contains(";")+" "+unescapedOriginalText.replace("\n", " "));
			}
		}
		// The second disjunction to handle the case at APW_ENG_20030325.0786.sgm offset 905, which happens to map to the exact same word at 913
		if(!actualText.equals(unescapedOriginalText) || actualText.equals("Welch")){
			int index = this.text.lastIndexOf(unescapedOriginalText, Math.min(this.text.length(), span.start-lastDiff));
			if(index == -1){
				if(REMOVE_OOB_MENTIONS){
					toBeRemoved.add(mention);
					return lastDiff;
				}
				System.err.println("Cannot find "+unescapedOriginalText+" in "+this.text);
				throw new RuntimeException();
			}
			int diff = span.start - index;
			span.start = index;
			span.end = index+unescapedOriginalText.length();
			lastDiff = diff;
			if(CHECK_OFFSET_TEXT){
				if(diff > unescapedOriginalText.length()){
					System.out.printf("%-45s[%4d->%4d]: %s_%s_%s\n", mention.getFullID(), index+diff, index,
							this.text.substring(Math.max(0, span.start-10), span.start).replace("\n", " "),
							unescapedOriginalText.replace("\n", " "),
							this.text.substring(span.end, Math.min(this.text.length(), span.end+10)).replace("\n", " "));
				}
			}
		}
		return lastDiff;
	}
	
	private ACEEntityMention getMention(Node entityMention, ACEEntity aceEntity){
		NamedNodeMap mentionAttributes = entityMention.getAttributes();
		String mentionId = getAttribute(mentionAttributes, "ID");
		String mentionType = getAttribute(mentionAttributes, "TYPE");
		String ldcMentionType = getAttribute(mentionAttributes, "LDCTYPE");
		String ldcAttr = getAttribute(mentionAttributes, "LDCATR");
		Node extent = ((Element)entityMention).getElementsByTagName("EXTENT").item(0);
		Node extentCharseq = ((Element)extent).getElementsByTagName("CHARSEQ").item(0);
		// All entities in ACE are contiguous
		Span span = getSpan(extentCharseq);
		String aceText = extentCharseq.getTextContent();
		Node head = ((Element)entityMention).getElementsByTagName("HEAD_EXTENT").item(0);
		Node headCharseq = head == null ? null : ((Element)head).getElementsByTagName("CHARSEQ").item(0);
		Span headSpan = headCharseq == null ? null : getSpan(headCharseq);
		String aceHeadText = headCharseq == null ? "" : headCharseq.getTextContent();
		ACEEntityMention mention = new ACEEntityMention(mentionId, mentionType, ldcMentionType, ldcAttr, aceEntity,
														span, headSpan, aceText, aceHeadText, SpanLabel.get(aceEntity.type.name()));
		return mention;
	}
	
	private void extractValues(Document apf){
		NodeList values = apf.getElementsByTagName("VALUE");
		for(int i=0; i<values.getLength(); i++){
			Node value = values.item(i);
			NamedNodeMap attributes = value.getAttributes();
			String id = getAttribute(attributes, "ID");
			String type = getAttribute(attributes, "TYPE");
			String subtype = getAttribute(attributes, "SUBTYPE");
			ACEValue aceValue = new ACEValue(id, type, subtype);
			NodeList valueMentions = ((Element)value).getElementsByTagName("VALUE_MENTION");
			for(int j=0; j<valueMentions.getLength(); j++){
				Node valueMention = valueMentions.item(j);
				ACEValueMention aceValueMention = getValueMention(valueMention, aceValue);
				aceValue.addMention(aceValueMention);
				this.valueMentions.add(aceValueMention);
				this.objectMentionsById.put(aceValueMention.getFullID(), aceValueMention);
			}
			this.values.add(aceValue);
			this.objectsById.put(id, aceValue);
		}
		Collections.sort(this.valueMentions);
		checkAndFixMentions(this.valueMentions);
	}
	
	private ACEValueMention getValueMention(Node valueMention, ACEValue aceValue){
		NamedNodeMap attributes = valueMention.getAttributes();
		String id = getAttribute(attributes, "ID");
		Node charseq = getMentionCharseq(valueMention, "EXTENT");
		Span span = getSpan(charseq);
		String text = charseq.getTextContent();
		return new ACEValueMention(id, span, text, aceValue);
	}
	
	private void extractTimexes(Document apf){
		NodeList timexes = apf.getElementsByTagName("TIMEX2");
		for(int i=0; i<timexes.getLength(); i++){
			Node timex = timexes.item(i);
			NamedNodeMap attributes = timex.getAttributes();
			String id = getAttribute(attributes, "ID");
			String val = getAttribute(attributes, "VAL");
			String mod = getAttribute(attributes, "MOD");
			String anchorVal = getAttribute(attributes, "ANCHOR_VAL");
			String anchorDir = getAttribute(attributes, "ANCHOR_DIR");
			String set = getAttribute(attributes, "SET");
			String comment = getAttribute(attributes, "COMMENT");
			ACETimex aceTimex = new ACETimex(id, val, mod, anchorVal, anchorDir, set, comment);
			NodeList timexMentions = ((Element)timex).getElementsByTagName("TIMEX2_MENTION");
			for(int j=0; j<timexMentions.getLength(); j++){
				Node timexMention = timexMentions.item(j);
				ACETimexMention aceTimexMention = getTimexMention(timexMention, aceTimex);
				aceTimex.addMention(aceTimexMention);
				this.timexMentions.add(aceTimexMention);
				this.objectMentionsById.put(aceTimexMention.getFullID(), aceTimexMention);
			}
			this.timexes.add(aceTimex);
			this.objectsById.put(id, aceTimex);
		}
		Collections.sort(this.timexMentions);
		checkAndFixMentions(this.timexMentions);
	}
	
	private ACETimexMention getTimexMention(Node timexMention, ACETimex aceTimex){
		NamedNodeMap attributes = timexMention.getAttributes();
		String id = getAttribute(attributes, "ID");
		Node charseq = getMentionCharseq(timexMention, "EXTENT");
		Span span = getSpan(charseq);
		String text = charseq.getTextContent();
		return new ACETimexMention(id, span, text, aceTimex);
	}
	
	private void extractRelations(Document apf){
		NodeList relations = apf.getElementsByTagName("RELATION");
		for(int i=0; i<relations.getLength(); i++){
			Node relation = relations.item(i);
			NamedNodeMap attributes = relation.getAttributes();
			String id = getAttribute(attributes, "ID");
			String type = getAttribute(attributes, "TYPE");
			String subtype = getAttribute(attributes, "SUBTYPE");
			String tense = getAttribute(attributes, "TENSE");
			String modality = getAttribute(attributes, "MODALITY");
			ACEEntity[] args = new ACEEntity[2];
			ACETimex[] timestamp = new ACETimex[1];
			String[] timestampType = new String[1];
			getRelationArguments(relation, args, timestamp, timestampType);
			ACERelation aceRelation = new ACERelation(args, id, type, subtype, tense, modality, timestamp[0], timestampType[0]);
			NodeList relationMentions = ((Element)relation).getElementsByTagName("RELATION_MENTION");
			for(int j=0; j<relationMentions.getLength(); j++){
				Node relationMention = relationMentions.item(j);
				ACERelationMention aceRelationMention = getRelationMention(relationMention, aceRelation);
				aceRelation.addMention(aceRelationMention);
				this.relationMentions.add(aceRelationMention);
			}
			this.relations.add(aceRelation);
		}
		Collections.sort(this.relationMentions);
		checkAndFixMentions(this.relationMentions);
	}
	
	private ACERelationMention getRelationMention(Node relationMention, ACERelation aceRelation){
		ACETimexMention[] timestamp = new ACETimexMention[1];
		String[] timestampType = new String[1];
		ACEEntityMention[] args = new ACEEntityMention[2];
		getRelationMentionArguments(relationMention, aceRelation, args, timestamp, timestampType);
		NamedNodeMap attributes = relationMention.getAttributes();
		String id = getAttribute(attributes, "ID");
		String syntacticClass = getAttribute(attributes, versionIsACE2004 ? "LDCLEXICALCONDITION" : "LEXICALCONDITION");
		Node charseq = getMentionCharseq(relationMention, versionIsACE2004 ? "LDC_EXTENT" : "EXTENT");
		Span span = getSpan(charseq);
		String text = charseq.getTextContent();
		return new ACERelationMention(args, id, syntacticClass, span, text, timestamp[0], timestampType[0], aceRelation);
	}
	
	private Node getMentionCharseq(Node mention, String extentTagName){
		NodeList extents = ((Element)mention).getElementsByTagName(extentTagName);
		for(int i=0; i<extents.getLength(); i++){
			Node extent = extents.item(i);
			NodeList charseqs = extent.getChildNodes();
			for(int j=0; j<charseqs.getLength(); j++){
				Node charseq = charseqs.item(j);
				if(!charseq.getNodeName().equals("CHARSEQ")){
					continue;
				}
				return charseq;
			}
		}
		throw new RuntimeException("No <charseq> found in the mention: "+mention.getTextContent());
	}
	
	private void getRelationMentionArguments(Node relationMention, ACERelation aceRelation,
											 ACEEntityMention[] _entityMentions, ACETimexMention[] _timestamp, String[] _timestampType){
		NodeList relationMentionArgs = ((Element)relationMention).getElementsByTagName(versionIsACE2004 ? "REL_MENTION_ARG" : "RELATION_MENTION_ARGUMENT");
		for(int i=0; i<relationMentionArgs.getLength(); i++){
			Node relationMentionArg = relationMentionArgs.item(i);
			NamedNodeMap argAttributes = relationMentionArg.getAttributes();
			String entityMentionID = getAttribute(argAttributes, versionIsACE2004 ? "ENTITYMENTIONID" : "REFID");
			String argNumStr = getAttribute(argAttributes, versionIsACE2004 ? "ARGNUM" : "ROLE");
			try{
				int argNum = Integer.parseInt(argNumStr.substring(versionIsACE2004 ? 0 : 4));
				String fullEntityMentionID = entityMentionID;
				if(versionIsACE2004){
					fullEntityMentionID = aceRelation.args[argNum-1].id+entityMentionID.substring(entityMentionID.indexOf("-"));
				}
				_entityMentions[argNum-1] = (ACEEntityMention)this.objectMentionsById.get(fullEntityMentionID);
			} catch (NumberFormatException e){ // A timestamp
				String timestampType = argNumStr;
				String fullTimexMentionID = entityMentionID;
				ACETimexMention timexMention = (ACETimexMention)objectMentionsById.get(fullTimexMentionID);
				_timestamp[0] = timexMention;
				_timestampType[0] = timestampType;
			}
		}
	}
	
	private void getRelationArguments(Node relation,
									  ACEEntity[] _entities, ACETimex[] _timestamp, String[] _timestampType){
		NodeList args = relation.getChildNodes();
		for(int i=0; i<args.getLength(); i++){
			Node relationArg = args.item(i);
			if(!relationArg.getNodeName().equals(versionIsACE2004 ? "REL_ENTITY_ARG" : "RELATION_ARGUMENT")){
				continue;
			}
			NamedNodeMap relationArgAtts = relationArg.getAttributes();
			String entityID = getAttribute(relationArgAtts, versionIsACE2004 ? "ENTITYID" : "REFID");
			String roleNumStr = getAttribute(relationArgAtts, versionIsACE2004 ? "ARGNUM" : "ROLE");
			// ACE2004: 1 or 2, ACE2005: Arg-1 or Arg-2
			try{
				int argNum = Integer.parseInt(roleNumStr.substring(versionIsACE2004 ? 0 : 4));
				_entities[argNum-1] = (ACEEntity)this.objectsById.get(entityID);
			} catch (NumberFormatException e){ // Means that this is timestamp
				String timestampType = roleNumStr;
				String timestampID = entityID;
				_timestamp[0] = (ACETimex)this.objectsById.get(timestampID);
				_timestampType[0] = timestampType;
			}
		}
	}
	
	private void extractEvents(Document apf){
		NodeList events = apf.getElementsByTagName("EVENT");
		for(int i=0; i<events.getLength(); i++){
			Node event = events.item(i);
			NamedNodeMap attributes = event.getAttributes();
			String id = getAttribute(attributes, "ID");
			String type = getAttribute(attributes, "TYPE");
			String subtype = getAttribute(attributes, "SUBTYPE");
			String tense = getAttribute(attributes, "TENSE");
			String genericity = getAttribute(attributes, "GENERICITY");
			String polarity = getAttribute(attributes, "POLARITY");
			String modality = getAttribute(attributes, "MODALITY");
			ACEEvent aceEvent = new ACEEvent(id, type, subtype, tense, genericity, polarity, modality);
			getEventArguments(event, aceEvent);
			NodeList eventMentions = ((Element)event).getElementsByTagName("EVENT_MENTION");
			for(int j=0; j<eventMentions.getLength(); j++){
				Node eventMention = eventMentions.item(j);
				ACEEventMention aceEventMention = getEventMention(eventMention, aceEvent);
				aceEvent.addMention(aceEventMention);
				this.eventMentions.add(aceEventMention);
			}
			this.events.add(aceEvent);
		}
		Collections.sort(this.eventMentions);
		checkAndFixMentions(this.eventMentions);
	}
	
	private void getEventArguments(Node event, ACEEvent aceEvent){
		NodeList eventArguments = ((Element)event).getElementsByTagName("EVENT_ARGUMENT");
		for(int i=0; i<eventArguments.getLength(); i++){
			Node eventArgument = eventArguments.item(i);
			NamedNodeMap attributes = eventArgument.getAttributes();
			String refId = getAttribute(attributes, "REFID");
			String roleName = getAttribute(attributes, "ROLE");
			if(roleName.startsWith("Time")){
				aceEvent.addArgument("Time", this.objectsById.get(refId));
				aceEvent.setTimestampType(roleName);
			} else {
				aceEvent.addArgument(roleName, this.objectsById.get(refId));
			}
		}
	}
	
	private ACEEventMention getEventMention(Node eventMention, ACEEvent aceEvent){
		ACEObjectMention<?>[] args = getEventMentionArguments(eventMention, aceEvent);
		NamedNodeMap attributes = eventMention.getAttributes();
		String id = getAttribute(attributes, "ID");
		Node charseq = getMentionCharseq(eventMention, "EXTENT");
		Span span = getSpan(charseq);
		String text = charseq.getTextContent();
		Node scopeCharseq = getMentionCharseq(eventMention, "LDC_SCOPE");
		Span scopeSpan = getSpan(scopeCharseq);
		String scopeText = scopeCharseq.getTextContent();
		Node anchorCharseq = getMentionCharseq(eventMention, "ANCHOR");
		Span anchorSpan = getSpan(anchorCharseq);
		String anchorText = anchorCharseq.getTextContent();
		return new ACEEventMention(id, span, text, aceEvent, scopeSpan, scopeText, anchorSpan, anchorText, args);
	}
	
	private ACEObjectMention<?>[] getEventMentionArguments(Node eventMention, ACEEvent aceEvent){
		NodeList eventMentionArgs = ((Element)eventMention).getElementsByTagName("EVENT_MENTION_ARGUMENT");
		ACEObjectMention<?>[] result = new ACEObjectMention<?>[eventMentionArgs.getLength()];
		for(int i=0; i<eventMentionArgs.getLength(); i++){
			Node eventMentionArg = eventMentionArgs.item(i);
			NamedNodeMap attributes = eventMentionArg.getAttributes();
			String refId = getAttribute(attributes, "REFID");
//			String roleName = getAttribute(attributes, "ROLE"); // Not used
			result[i] = this.objectMentionsById.get(refId);
		}
		return result;
	}
	
	private static String getAttribute(NamedNodeMap attrs, String attrName){
		String result = "";
		try{
			result = attrs.getNamedItem(attrName.toLowerCase()).getTextContent();
		} catch (NullPointerException e){}
		return result;
	}
	
	public static void printMentions(ACEDocument doc, List<? extends ACEObjectMention<?>> mentions){
		for(ACEObjectMention<?> mention: mentions){
			try{
				System.out.println(mention.toString(doc.text));
			} catch (RuntimeException e){
				System.out.println("===TEXT===");
				System.out.println(doc.text);
				System.out.println("===FULL TEXT===");
				System.out.println(doc.fullText);
				System.out.println("===SGM===");
				System.out.println(doc.uri);
				System.out.println("===TEXT LENGTH===");
				System.out.println(doc.text.length());
				System.out.println("===OFFSET===");
				System.out.println(doc.offset);
				System.out.println("===MENTION===");
				System.out.println(mention.text);
				System.out.println(mention.span);
				throw e;
			}
		}
	}
	
	public static String unescape(String xml){
		String result = xml.replaceAll("(?i)&amp;", "&");
		result = result.replaceAll("(?i)&lt;", "<");
		result = result.replaceAll("(?i)&gt;", ">");
//		result = result.replaceAll("(?i)&lt;", "<");
//		result = result.replaceAll("(?i)&lt;", "<");
		return result;
	}
}
