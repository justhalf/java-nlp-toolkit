/**
 * 
 */
package justhalf.nlp.reader.acereader;

import static justhalf.nlp.reader.acereader.ACEDocument.unescape;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.StringUtils;
import justhalf.nlp.postagger.POSTagger;
import justhalf.nlp.postagger.StanfordPOSTagger;
import justhalf.nlp.reader.acereader.ACEEntity.ACEEntitySubType;
import justhalf.nlp.reader.acereader.ACEEntity.ACEEntityType;
import justhalf.nlp.reader.acereader.ACEEvent.ACEEventSubType;
import justhalf.nlp.reader.acereader.ACEEvent.ACEEventType;
import justhalf.nlp.reader.acereader.ACERelation.ACERelationSubType;
import justhalf.nlp.reader.acereader.ACERelation.ACERelationType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueType;
import justhalf.nlp.sentencesplitter.SentenceSplitter;
import justhalf.nlp.sentencesplitter.StanfordSentenceSplitter;
import justhalf.nlp.tokenizer.RegexTokenizer;
import justhalf.nlp.tokenizer.StanfordTokenizer;
import justhalf.nlp.tokenizer.Tokenizer;

/**
 * <p>The main class to read raw ACE documents as ACEDocuments objects.</p>
 */
public class ACEReader {
	
	/** The complete list of domains in ACE 2004 */
	public static final List<String> ACE2004_DOMAINS = Arrays.asList(new String[]{
			"arabic_treebank", "bnews", "chinese_treebank", "fisher_transcripts", "nwire"
	});
	
	/** The complete list of domains in ACE 2005 */
	public static final List<String> ACE2005_DOMAINS = Arrays.asList(new String[]{
			"bc", "bn", "cts", "nw", "un", "wl"
	});
	
	public static void main(String[] args) throws FileNotFoundException{
		String ace2004DirName = null;
		String ace2005DirName = null;
		HashSet<String> ace2004Domains = new LinkedHashSet<String>(ACE2004_DOMAINS);
		HashSet<String> ace2005Domains = new LinkedHashSet<String>(ACE2005_DOMAINS);
		
		double[] datasplit = null;
		boolean convert = false;
		boolean convertEntities = false;
		String ace2004OutputDir = null;
		String ace2005OutputDir = null;
		
		boolean tokenize = false;
		boolean posTag = false;
		Tokenizer tokenizer = null;
		POSTagger posTagger = null;
		SentenceSplitter splitter = null;
		
		boolean toCoNLL = false;
		boolean ignoreOverlaps = false;
		boolean useBILOU = false;
		boolean splitByDocument = true;
		
		int argIndex = 0;
		while(argIndex < args.length){
			switch(args[argIndex]){
			case "-ace2004Dir":
				ace2004DirName = args[argIndex+1];
				argIndex += 2;
				break;
			case "-ace2005Dir":
				ace2005DirName = args[argIndex+1];
				argIndex += 2;
				break;
			case "-ace2004IncludeDomains":
				ace2004Domains.clear();
				ace2004Domains.addAll(Arrays.asList(args[argIndex+1].split(",")));
				argIndex += 2;
				break;
			case "-ace2004ExcludeDomains":
				ace2004Domains.removeAll(Arrays.asList(args[argIndex+1].split(",")));
				argIndex += 2;
				break;
			case "-ace2005IncludeDomains":
				ace2005Domains.clear();
				ace2005Domains.addAll(Arrays.asList(args[argIndex+1].split(",")));
				argIndex += 2;
				break;
			case "-ace2005ExcludeDomains":
				ace2005Domains.removeAll(Arrays.asList(args[argIndex+1].split(",")));
				argIndex += 2;
				break;
			case "-convertEntitiesToInline":
				convertEntities = true;
				convert = true;
				argIndex += 1;
				break;
			case "-dataSplit":
				String[] tokens = args[argIndex+1].split(",");
				datasplit = new double[3];
				double sum = 0;
				for(int i=0; i<tokens.length; i++){
					datasplit[i] = Double.parseDouble(tokens[i]);
					sum += datasplit[i];
				}
				for(int i=0; i<tokens.length; i++){
					datasplit[i] /= sum;
				}
				if(datasplit.length == 2){
					datasplit[2] = datasplit[1];
					datasplit[1] = 0;
				}
				argIndex += 2;
				break;
			case "-ace2004OutputDir":
				ace2004OutputDir = args[argIndex+1];
				argIndex += 2;
				break;
			case "-ace2005OutputDir":
				ace2005OutputDir = args[argIndex+1];
				argIndex += 2;
				break;
			case "-tokenizer":
				tokenize = true;
				switch(args[argIndex+1]){
				case "stanford":
					tokenizer = new StanfordTokenizer();
					break;
				case "regex":
					tokenizer = new RegexTokenizer();
					break;
				default:
					System.out.println("Unrecognized tokenizer \""+args[argIndex+1]+"\", using stanford.");
					tokenizer = new StanfordTokenizer();
					break;
				}
				argIndex += 2;
				break;
			case "-posTagger":
				posTag = true;
				switch(args[argIndex+1]){
				case "stanford":
					posTagger = new StanfordPOSTagger();
					break;
				default:
					System.out.println("Unrecognized POS tagger \""+args[argIndex+1]+"\", using stanford.");
					posTagger = new StanfordPOSTagger();
					break;
				}
				argIndex += 2;
				break;
			case "-splitter":
				switch(args[argIndex+1]){
				case "stanford":
					splitter = new StanfordSentenceSplitter();
					break;
				default:
					System.out.println("Unrecognized sentence splitter \""+args[argIndex+1]+"\", using stanford.");
					splitter = new StanfordSentenceSplitter();
					break;
				}
				argIndex += 2;
				break;
			case "-toCoNLLFormat":
				toCoNLL = true;
				argIndex += 1;
				break;
			case "-ignoreOverlaps":
				ignoreOverlaps = true;
				argIndex += 1;
				break;
			case "-useBILOU":
				useBILOU = true;
				argIndex += 1;
				break;
			case "-splitBySentences":
				splitByDocument = false;
				argIndex += 1;
				break;
			case "-h":
			case "--help":
				printHelp();
				System.exit(0);
				break;
			default:
				printHelp("Unrecognized option: "+args[argIndex]);
				System.exit(0);
				break;
			}
		}
		if(ace2004DirName == null && ace2005DirName == null){
			printHelp("Please specify the input directories for either ACE2004 (-ace2004Dir)"
					+ " or ACE2005 (-ace2005Dir)");
			System.exit(0);
		}
		if(convert){
			if(ace2004DirName != null && ace2004OutputDir == null){
				printHelp("Please specify the output directory for ACE2004.");
				System.exit(0);
			}
			if(ace2005DirName != null && ace2005OutputDir == null){
				printHelp("Please specify the output directory for ACE2005.");
				System.exit(0);
			}
			if(datasplit == null){
				printHelp("Please specify the datasplit with -dataSplit option.");
				System.exit(0);
			}
			if(splitter == null){
				splitter = new StanfordSentenceSplitter();
			}
		}
		if(tokenizer == null){
			tokenizer = new StanfordTokenizer();
		}
		
		// Get documents list
		List<File> fileList = new ArrayList<File>();
		if(ace2004DirName != null){
			extractDocList(fileList, ace2004DirName, ace2004Domains);
		}
		if(ace2005DirName != null){
			extractDocList(fileList, ace2005DirName, ace2005Domains, "/timex2norm");
		}
		
		String dataset = "";
		if(ace2004DirName != null){
			dataset += "ACE2004 ("+StringUtils.join(ace2004Domains, ",")+")";
		}
		if(ace2005DirName != null){
			if(dataset != ""){
				dataset += " and ";
			}
			dataset += "ACE2005 ("+StringUtils.join(ace2005Domains, ",")+")";
		}
		System.out.println("Extracting data from "+dataset);
		
		// Start reading data
		List<ACEDocument> ace2004Docs = new ArrayList<ACEDocument>();
		List<ACEDocument> ace2005Docs = new ArrayList<ACEDocument>();
		List<ACEDocument> docs = new ArrayList<ACEDocument>();
		int docCount = 0;
		int[] entityCount = new int[1];
		int[] entityMentionCount = new int[1];
		int[] valueCount = new int[1];
		int[] valueMentionCount = new int[1];
		int[] relationCount = new int[1];
		int[] relationMentionCount = new int[1];
		int[] eventCount = new int[1];
		int[] eventMentionCount = new int[1];
		int overlapCount = 0;
		int allLowercaseCount = 0;
		
		Map<Integer, Integer> wordCountInMention = new HashMap<Integer, Integer>();
		Map<ACEEntityType, Integer> entityTypeCount = new HashMap<ACEEntityType, Integer>();
		Map<ACERelationType, Integer> relationTypeCount = new HashMap<ACERelationType, Integer>();
		Map<ACEValueType, Integer> valueTypeCount = new HashMap<ACEValueType, Integer>();
		Map<ACEEventType, Integer> eventTypeCount = new HashMap<ACEEventType, Integer>();
		Map<ACEEntityType, Integer> entityTypeMentionCount = new HashMap<ACEEntityType, Integer>();
		Map<ACERelationType, Integer> relationTypeMentionCount = new HashMap<ACERelationType, Integer>();
		Map<ACEValueType, Integer> valueTypeMentionCount = new HashMap<ACEValueType, Integer>();
		Map<ACEEventType, Integer> eventTypeMentionCount = new HashMap<ACEEventType, Integer>();
		for(File sgmFile: fileList){
			try {
				ACEDocument doc = new ACEDocument(sgmFile.getAbsolutePath());
				docCount++;
//				printMentions(doc, doc.mentions);
				
				// Count mentions and objects
				count(doc, doc.entities, entityTypeCount, entityTypeMentionCount, entityCount, entityMentionCount);
				count(doc, doc.relations, relationTypeCount, relationTypeMentionCount, relationCount, relationMentionCount);
				count(doc, doc.values, valueTypeCount, valueTypeMentionCount, valueCount, valueMentionCount);
				count(doc, doc.events, eventTypeCount, eventTypeMentionCount, eventCount, eventMentionCount);
				
				// Count mention overlaps
				for(int i=0; i<doc.entityMentions.size(); i++){
					ACEEntityMention mention1 = doc.entityMentions.get(i);
					boolean hasOverlap = false;
					for(int j=0; j<doc.entityMentions.size(); j++){
						if(j==i) continue;
						ACEEntityMention mention2 = doc.entityMentions.get(j);
						hasOverlap |= mention1.overlapsWith(mention2);
					}
					overlapCount += hasOverlap ? 1 : 0;
					int wordCount = fixTokens(tokenizer.tokenize(mention1.text)).size();
					int count = wordCountInMention.getOrDefault(wordCount, 0);
					wordCountInMention.put(wordCount, count+1);
				}
				
				// Count lowercased documents
				allLowercaseCount += doc.textInLowercase ? 1 : 0;
				
				docs.add(doc);
				if(doc.versionIsACE2004){
					ace2004Docs.add(doc);
				} else {
					ace2005Docs.add(doc);
				}
			} catch (IOException | SAXException e) {
				System.err.println(sgmFile);
				e.printStackTrace();
			}
		}
		System.out.println("Total documents: "+docCount);
		System.out.println("Total lowercased documents: "+allLowercaseCount);
		System.out.println("Total entities: "+entityCount[0]);
		System.out.println("Total mentions: "+entityMentionCount[0]);
		System.out.println("Total mentions overlaps: "+overlapCount);
		System.out.println("Word count stats in entity mentions:");
		for(int key: wordCountInMention.keySet()){
			System.out.println("\t"+key+": "+wordCountInMention.get(key));
		}
		System.out.println();
		System.out.println("Total relations: "+relationCount[0]);
		System.out.println("Total relation mentions: "+relationMentionCount[0]);
		System.out.println("Total values: "+valueCount[0]);
		System.out.println("Total value mentions: "+valueMentionCount[0]);
		System.out.println("Total events: "+eventCount[0]);
		System.out.println("Total event mentions: "+eventMentionCount[0]);

		System.out.println();
		System.out.println("Entity mention type counts:");
		for(ACEEntityType type: ACEEntityType.values()){
			System.out.print(type+": "+entityTypeMentionCount.getOrDefault(type, 0));
			System.out.println(" ("+entityTypeCount.getOrDefault(type, 0)+")");
			for(ACEEntitySubType subtype: type.subtypes()){
				if(subtype.text.equals("") && type.subtypes().size() == 1) continue;
				System.out.print("\t"+subtype+": "+entityTypeMentionCount.getOrDefault(subtype, 0));
				System.out.print(" ("+entityTypeCount.getOrDefault(subtype, 0)+")");
				System.out.printf(" [%s%s%s]\n", subtype.in2004 ? "2004" : "", subtype.in2004 && subtype.in2005 ? "+" : "", subtype.in2005 ? "2005" : "");
			}
		}
		System.out.println();
		System.out.println("Relation mention type counts:");
		for(ACERelationType type: ACERelationType.values()){
			System.out.print(type+": "+relationTypeMentionCount.getOrDefault(type, 0));
			System.out.print(" ("+relationTypeCount.getOrDefault(type, 0)+")");
			System.out.printf(" [%s%s%s]\n", type.in2004 ? "2004" : "", type.in2004 && type.in2005 ? "+" : "", type.in2005 ? "2005" : "");
			for(ACERelationSubType subtype: type.subtypes()){
				if(subtype.text.equals("") && type.subtypes().size() == 1) continue;
				System.out.print("\t"+subtype+": "+relationTypeMentionCount.getOrDefault(subtype, 0));
				System.out.print(" ("+relationTypeCount.getOrDefault(subtype, 0)+")");
				System.out.printf(" [%s%s%s]\n", subtype.in2004 ? "2004" : "", subtype.in2004 && subtype.in2005 ? "+" : "", subtype.in2005 ? "2005" : "");
			}
		}
		System.out.println();
		System.out.println("Value mention type counts: (all from ACE 2005)");
		for(ACEValueType type: ACEValueType.values()){
			System.out.print(type+": "+valueTypeMentionCount.getOrDefault(type, 0));
			System.out.println(" ("+valueTypeCount.getOrDefault(type, 0)+")");
			for(ACEValueSubType subtype: type.subtypes()){
				if(subtype.text.equals("") && type.subtypes().size() == 1) continue;
				System.out.print("\t"+subtype+": "+valueTypeMentionCount.getOrDefault(subtype, 0));
				System.out.println(" ("+valueTypeCount.getOrDefault(subtype, 0)+")");
			}
		}
		System.out.println();
		System.out.println("Event mention type counts: (all from ACE 2005)");
		for(ACEEventType type: ACEEventType.values()){
			System.out.print(type+": "+eventTypeMentionCount.getOrDefault(type, 0));
			System.out.println(" ("+eventTypeCount.getOrDefault(type, 0)+")");
			for(ACEEventSubType subtype: type.subtypes()){
				if(subtype.text.equals("") && type.subtypes().size() == 1) continue;
				System.out.print("\t"+subtype+": "+eventTypeMentionCount.getOrDefault(subtype, 0));
				System.out.println(" ("+eventTypeCount.getOrDefault(subtype, 0)+")");
			}
		}
		
		if(convert){
			if(ace2004Docs.size() > 0){
				System.out.println("Printing ACE2004 dataset to "+ace2004OutputDir+"/{train,dev,test}.data");
				printDataset(ace2004OutputDir, ace2004Docs, datasplit, convertEntities,
						(tokenize || toCoNLL) ? tokenizer : null, posTag ? posTagger : null, splitter,
								toCoNLL, ignoreOverlaps, useBILOU, splitByDocument);
			}
			if(ace2005Docs.size() > 0){
				System.out.println("Printing ACE2005 dataset to "+ace2005OutputDir+"/{train,dev,test}.data");
				printDataset(ace2005OutputDir, ace2005Docs, datasplit, convertEntities,
						(tokenize || toCoNLL) ? tokenizer : null, posTag ? posTagger : null,
								splitter, toCoNLL, ignoreOverlaps, useBILOU, splitByDocument);
			}
		}
	}

	private static void printDataset(String outputDir, List<ACEDocument> docs, double[] datasplit,
			boolean convertEntities, Tokenizer tokenizer, POSTagger posTagger, SentenceSplitter splitter,
			boolean toCoNLL, boolean ignoreOverlaps, boolean useBILOU, boolean splitByDocument) throws FileNotFoundException {
		List<ACESentence> trainSentences = new ArrayList<ACESentence>();
		List<ACESentence> devSentences = new ArrayList<ACESentence>();
		List<ACESentence> testSentences = new ArrayList<ACESentence>();
		if(splitByDocument){
			List<ACEDocument> trainDocs = new ArrayList<ACEDocument>();
			List<ACEDocument> devDocs = new ArrayList<ACEDocument>();
			List<ACEDocument> testDocs = new ArrayList<ACEDocument>();
			splitData(docs, trainDocs, devDocs, testDocs, datasplit);
			trainSentences = getSentences(trainDocs, splitter, ignoreOverlaps);
			devSentences = getSentences(devDocs, splitter, ignoreOverlaps);
			testSentences = getSentences(testDocs, splitter, ignoreOverlaps);
		} else {
			List<ACESentence> aceSentences = getSentences(docs, splitter, ignoreOverlaps);
			trainSentences = new ArrayList<ACESentence>();
			devSentences = new ArrayList<ACESentence>();
			testSentences = new ArrayList<ACESentence>();
			splitData(aceSentences, trainSentences, devSentences, testSentences, datasplit);
		}
		writeData(trainSentences, outputDir, "/train.data", tokenizer, posTagger, toCoNLL, useBILOU);
		writeData(devSentences, outputDir, "/dev.data", tokenizer, posTagger, toCoNLL, useBILOU);
		writeData(testSentences, outputDir, "/test.data", tokenizer, posTagger, toCoNLL, useBILOU);
	}

	/**
	 * Split documents into sentences with their corresponding annotations (entities, relations,
	 * events, timexes, values)
	 * @param docs The list of ACEDocument to be split
	 * @param splitter The sentence splitter
	 * @param ignoreOverlappingEntities Whether to ignore overlapping entities by removing the
	 * 									shorter one when there is an overlap.
	 * @return
	 */
	public static List<ACESentence> getSentences(List<ACEDocument> docs, SentenceSplitter splitter,
			boolean ignoreOverlappingEntities) {
		List<ACESentence> aceSentences = new ArrayList<ACESentence>();
		for(ACEDocument doc: docs){
			for(CoreLabel sentence: fixSplit(splitter.split(doc.text))){
				Span sentenceSpan = new Span(sentence.beginPosition(), sentence.endPosition());
				ACESentence aceSentence = new ACESentence(doc, sentenceSpan, sentence.value());
				for(ACEEntityMention mention: doc.entityMentions){
					if(sentenceSpan.contains(mention.span)){
						ACEEntityMention newMention = new ACEEntityMention(mention);
						newMention.span.start -= sentenceSpan.start;
						newMention.span.end -= sentenceSpan.start;
						newMention.headSpan.start -= sentenceSpan.start;
						newMention.headSpan.end -= sentenceSpan.start;

						boolean add = true;
						if(ignoreOverlappingEntities){
							for(int i=aceSentence.entities.size()-1; i >= 0; i--){
								ACEEntityMention existingMention = aceSentence.entities.get(i);
								if(newMention.overlapsWith(existingMention)){
									if(newMention.span.length() > existingMention.span.length()){
										aceSentence.entities.remove(i);
									} else {
										add = false;
										break;
									}
								}
							}
						}
						if(add){
							aceSentence.addEntityMention(newMention);
						}
					}
				}
				for(ACERelationMention relation: doc.relationMentions){
					if(sentenceSpan.contains(relation.span)){
						aceSentence.addRelationMention(relation);
					}
				}
				for(ACEEventMention event: doc.eventMentions){
					if(sentenceSpan.contains(event.span)){
						aceSentence.addEventMention(event);
					}
				}
				for(ACETimexMention timex: doc.timexMentions){
					if(sentenceSpan.contains(timex.span)){
						aceSentence.addTimexMention(timex);
					}
				}
				for(ACEValueMention value: doc.valueMentions){
					if(sentenceSpan.contains(value.span)){
						aceSentence.addValueMention(value);
					}
				}
				aceSentences.add(aceSentence);
			}
		}
		return aceSentences;
	}
	
	private static List<CoreLabel> fixSplit(List<CoreLabel> sentences){
		List<CoreLabel> result = new ArrayList<CoreLabel>();
		for(int i=0; i<sentences.size(); i++){
			CoreLabel sentence = sentences.get(i);
			CoreLabel nextSentence = null;
			
			while(sentence != null){
				if(i < sentences.size()-1){
					nextSentence = sentences.get(i+1);
				} else {
					nextSentence = null;
				}
				if (sentence.value().contains("\n\n") || (sentence.endPosition() <= 70 && sentence.value().contains("\n"))){
					// Split if contains double new lines or new lines in the beginning of document
					int startIndex = -1;
					int startIndexOrig = -1;
					if(sentence.endPosition() <= 70 && sentence.value().contains("\n")){
						startIndex = sentence.value().indexOf("\n");
						startIndexOrig = sentence.originalText().indexOf("\n");
					} else {
						startIndex = sentence.value().indexOf("\n\n");
						startIndexOrig = sentence.originalText().indexOf("\n\n");
					}
					int nextSentenceIndex = -1;
					int nextSentenceIndexOrig = -1;
					Matcher matcher = java.util.regex.Pattern.compile("[\n\t ]+").matcher(sentence.value().substring(startIndex));
					if(matcher.find()){
						nextSentenceIndex = matcher.end() + startIndex;
					}
					matcher = java.util.regex.Pattern.compile("[\n\t ]+").matcher(sentence.originalText().substring(startIndexOrig));
					if(matcher.find()){
						nextSentenceIndexOrig = matcher.end() + startIndexOrig;
					}

					CoreLabel newSentence = new CoreLabel();
					newSentence.setBeginPosition(nextSentenceIndex+sentence.beginPosition());
					newSentence.setEndPosition(sentence.endPosition());
					newSentence.setAfter(sentence.after());
					newSentence.setBefore(sentence.value().substring(startIndex, nextSentenceIndex));
					newSentence.setOriginalText(sentence.originalText().substring(nextSentenceIndexOrig));
					newSentence.setWord(sentence.word().substring(nextSentenceIndex));
					newSentence.setValue(sentence.value().substring(nextSentenceIndex));
					sentence.setEndPosition(startIndex+sentence.beginPosition());
					sentence.setAfter(newSentence.before());
					sentence.setOriginalText(sentence.originalText().substring(0, startIndexOrig));
					sentence.setWord(sentence.word().substring(0, startIndex));
					sentence.setValue(sentence.value().substring(0, startIndex));
					result.add(sentence);
					sentence = newSentence;
				} else if(nextSentence != null && nextSentence.beginPosition() == sentence.endPosition()){
					// Combine with next if no space
					sentence.setAfter(nextSentence.after());
					sentence.setEndPosition(nextSentence.endPosition());
					sentence.setOriginalText(sentence.originalText() + nextSentence.before() + nextSentence.originalText());
					sentence.setWord(sentence.word() + nextSentence.before() + nextSentence.word());
					sentence.setValue(sentence.value() + nextSentence.before() + nextSentence.value());
					i += 1;
				} else {
					result.add(sentence);
					sentence = null;
				}
			}
		}
		return result;
	}
	
	private static List<CoreLabel> fixTokens(List<CoreLabel> tokens){
		List<CoreLabel> result = new ArrayList<CoreLabel>();
		for(int i=0; i<tokens.size(); i++){
			CoreLabel token = tokens.get(i);
//			CoreLabel nextToken = null;
			
			while(token != null){
//				if(i < tokens.size()-1){
//					nextToken = tokens.get(i+1);
//				} else {
//					nextToken = null;
//				}
				if (token.value().matches("([^-]*-[A-Z].*|[^-]*[A-Z][^-]*-[^-]+-[^-]*)")){
					// Split if contains dash followed by an uppercase letter or contains two dashes
					int startIndex = -1;
					int startIndexOrig = -1;
					Matcher matcher = Pattern.compile("-").matcher(token.value());
					if(matcher.find()){
						startIndex = matcher.start();
					}
					matcher = Pattern.compile("-").matcher(token.originalText());
					if(matcher.find()){
						startIndexOrig = matcher.start();
					}
					int nextTokenIndex = startIndex+1;
					int nextTokenIndexOrig = startIndexOrig+1;

					CoreLabel newToken = new CoreLabel();
					newToken.setBeginPosition(nextTokenIndex+token.beginPosition());
					newToken.setEndPosition(token.endPosition());
					newToken.setAfter(token.after());
					newToken.setBefore(token.value().substring(startIndex, nextTokenIndex));
					newToken.setOriginalText(token.originalText().substring(nextTokenIndexOrig));
					newToken.setWord(token.word().substring(nextTokenIndex));
					newToken.setValue(token.value().substring(nextTokenIndex));
					token.setEndPosition(startIndex+token.beginPosition());
					token.setAfter(newToken.before());
					token.setOriginalText(token.originalText().substring(0, startIndexOrig));
					token.setWord(token.word().substring(0, startIndex));
					token.setValue(token.value().substring(0, startIndex));
					result.add(token);
					token = newToken;
				} else {
					result.add(token);
					token = null;
				}
			}
		}
		return result;
	}
	
	private static <T> void splitData(List<T> aceObjects, List<T> trainObjects, List<T> devObjects, List<T> testObjects, double[] datasplit){
		int total = aceObjects.size();
		int trainSize = (int)(datasplit[0]*total);
		int devSize = (int)(datasplit[1]*total);
		int testSize = (int)(datasplit[2]*total);
		if(trainSize + devSize + testSize != total){
			trainSize -= trainSize + devSize + testSize - total;
		}
		trainObjects.addAll(aceObjects.subList(0, trainSize));
		devObjects.addAll(aceObjects.subList(trainSize, trainSize + devSize));
		testObjects.addAll(aceObjects.subList(trainSize + devSize, total));
		String typeName = aceObjects.get(0).getClass().getName();
		typeName = typeName.substring(typeName.lastIndexOf(".")+1);
		System.out.println("Number of objects ("+typeName+"):");
		System.out.println("Training: "+trainObjects.size());
		System.out.println("Dev: "+devObjects.size());
		System.out.println("Test: "+testObjects.size());
	}
	
	private static void writeData(List<ACESentence> sentences, String outputDir, String name,
			Tokenizer tokenizer, POSTagger posTagger,
			boolean toCoNLL, boolean useBILOU) throws FileNotFoundException{
		PrintWriter printer = new PrintWriter(new File(outputDir+name));
		for(ACESentence sentence: sentences){
			if(tokenizer != null){
				List<CoreLabel> tokens = fixTokens(tokenizer.tokenize(sentence.text));
				if(posTagger != null){
					posTagger.tagCoreLabels(tokens);
				}
				if(toCoNLL){
					List<WordLabel> outputTokens = spansToLabels(sentence.entities, tokens, useBILOU);
					if(posTagger != null){
						for(int i=0; i<tokens.size(); i++){
							printer.println(String.format("%s\t%s\t%s",tokens.get(i).value(), tokens.get(i).tag(), outputTokens.get(i).form));
						}
					} else {
						for(int i=0; i<tokens.size(); i++){
							printer.println(String.format("%s\t%s",tokens.get(i).value(), outputTokens.get(i).form));
						}
					}
					printer.println();
				} else {
					StringBuilder stringBuilder = new StringBuilder();
					for(CoreLabel token: tokens){
						if(stringBuilder.length() > 0){
							stringBuilder.append(" ");
						}
						stringBuilder.append(token.value());
						token.setWord(escapeBracket(token.word()));
					}
					printer.println(stringBuilder.toString());
					if(posTagger != null){
						stringBuilder = new StringBuilder();
						for(CoreLabel token: tokens){
							if(stringBuilder.length() > 0){
								stringBuilder.append(" ");
							}
							stringBuilder.append(token.tag());
						}
						printer.println(stringBuilder.toString());
					}
					stringBuilder = new StringBuilder();
					for(ACEEntityMention mention: sentence.entities){
						Span wordSpan = findWordSpan(mention.span, tokens);
						Span headWordSpan = findWordSpan(mention.headSpan, tokens);
						if(stringBuilder.length() > 0){
							stringBuilder.append("|");
						}
						stringBuilder.append(String.format("%s,%s,%s,%s %s", wordSpan.start, wordSpan.end, headWordSpan.start, headWordSpan.end, mention.label.form));
					}
					printer.println(stringBuilder.toString());
					printer.println();
				}
			} else {
				printer.println(sentence.text.replaceAll("[\n\t]", " "));
				StringBuilder stringBuilder = new StringBuilder();
				for(ACEEntityMention mention: sentence.entities){
					Span span = mention.span;
					Span headSpan = mention.headSpan;
					if(stringBuilder.length() > 0){
						stringBuilder.append("|");
					}
					stringBuilder.append(String.format("%s,%s,%s,%s %s", span.start, span.end, headSpan.start, headSpan.end, mention.label.form));
				}
				printer.println(stringBuilder.toString());
				printer.println();
			}
		}
		printer.close();
	}
	
	private static List<WordLabel> spansToLabels(List<ACEEntityMention> mentions, List<CoreLabel> tokens, boolean useBILOU){
		WordLabel[] result = new WordLabel[tokens.size()];
		Arrays.fill(result, null);
		for(ACEEntityMention mention: mentions){
			Span span = findWordSpan(mention.span, tokens);
			String type = mention.label.form;
			for(int i=span.start; i<span.end; i++){
				String addition = "";
				if(result[i] != null){
					addition = "H";
				}
				if(i == span.start && i == span.end-1){
					if(useBILOU){
						result[i] = WordLabel.get("U"+addition+"-"+type);
					} else {
						result[i] = WordLabel.get("B"+addition+"-"+type);
					}
				} else if (i == span.end-1){
					if(useBILOU){
						result[i] = WordLabel.get("L"+addition+"-"+type);
					} else {
						result[i] = WordLabel.get("I"+addition+"-"+type);
					}
				} else if (i == span.start){
					result[i] = WordLabel.get("B"+addition+"-"+type);
				} else {
					result[i] = WordLabel.get("I"+addition+"-"+type);
				}
			}
		}
		for(int i=0; i<result.length; i++){
			if(result[i] == null){
				result[i] = WordLabel.get("O");
			}
		}
		return Arrays.asList(result);
	}
	
	private static String escapeBracket(String word){
		if(word.contains("(")){
			return "-LRB-";
		} else if(word.contains(")")){
			return "-RRB-";
		} else if(word.contains("[")){
			return "-LSB-";
		} else if(word.contains("]")){
			return "-RSB-";
		} else if(word.contains("{")){
			return "-LCB-";
		} else if(word.contains("}")){
			return "-RCB-";
		} else {
			return word;
		}
	}
	
	private static Span findWordSpan(Span mention, List<CoreLabel> tokens){
		int start = -1;
		int end = -1;
		for(int i=0; i<tokens.size(); i++){
			CoreLabel token = tokens.get(i);
			if(token.beginPosition() <= mention.start && token.endPosition() > mention.start&& start == -1){
				start = i;
			}
			if(token.beginPosition() < mention.end && token.endPosition() >= mention.end){
				end = i+1;
			}
		}
		if(start == -1 || end == -1){
			System.out.println("Mention ["+mention.start+","+mention.end+"] not found in ["+tokens.get(0).beginPosition()+","+tokens.get(tokens.size()-1).endPosition()+"]");
			System.out.println(tokens);
		}
		return new Span(start, end);
	}
	
	/**
	 * Reads directories containing ACE 2004 and/or ACE 2005 data and return them as {@link #ACEDocument} objects.
	 * @param ace2004DirName The path to ACE 2004 directory. Can be null.
	 * @param ace2005DirName The path to ACE 2005 directory. Can be null.
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<ACEDocument> readDocuments(String ace2004DirName, String ace2005DirName) throws IOException, SAXException{
		return readDocuments(ace2004DirName, ace2005DirName, ACE2004_DOMAINS, ACE2005_DOMAINS);
	}
	
	/**
	 * Reads directories containing ACE 2004 and/or ACE 2005 data and return them as {@link #ACEDocument} objects.
	 * @param ace2004DirName The path to ACE 2004 directory. Can be null.
	 * @param ace2005DirName The path to ACE 2005 directory. Can be null.
	 * @param ace2004Domains The list of domains for ACE 2004 to be included.
	 * @param ace2005Domains The list of domains for ACE 2005 to be included.
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<ACEDocument> readDocuments(String ace2004DirName, String ace2005DirName, String[] ace2004Domains, String[] ace2005Domains) throws IOException, SAXException{
		return readDocuments(ace2004DirName, ace2005DirName, Arrays.asList(ace2004Domains), Arrays.asList(ace2005Domains));
	}
	
	/**
	 * Reads directories containing ACE 2004 and/or ACE 2005 data and return them as {@link #ACEDocument} objects.
	 * @param ace2004DirName The path to ACE 2004 directory. Can be null.
	 * @param ace2005DirName The path to ACE 2005 directory. Can be null.
	 * @param ace2004Domains The list of domains for ACE 2004 to be included.
	 * @param ace2005Domains The list of domains for ACE 2005 to be included.
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<ACEDocument> readDocuments(String ace2004DirName, String ace2005DirName, List<String> ace2004Domains, List<String> ace2005Domains) throws IOException, SAXException{
		List<ACEDocument> result = new ArrayList<ACEDocument>();
		List<File> fileList = new ArrayList<File>();
		if(ace2004DirName != null){
			extractDocList(fileList, ace2004DirName, ace2004Domains);
		}
		if(ace2005DirName != null){
			extractDocList(fileList, ace2005DirName, ace2005Domains, "/timex2norm");
		}
		for(File sgmFile: fileList){
			result.add(new ACEDocument(sgmFile.getAbsolutePath()));
		}
		return result;
	}

	private static void extractDocList(List<File> fileList, String aceDirName, Collection<String> aceDomains, String... additionalPath) {
		File aceDir = new File(aceDirName);
		for(File subdir: aceDir.listFiles()){
			if(!subdir.isDirectory()){
				continue;
			}
			if(!aceDomains.contains(subdir.getName())) continue;
			if(additionalPath.length > 0){
				subdir = new File(subdir.getAbsolutePath()+additionalPath[0]);
			}
			for(File sgmFile: subdir.listFiles()){
				if(!sgmFile.getName().endsWith(".sgm")){
					continue;
				}
				fileList.add(sgmFile);
			}
		}
	}
	
	private static void count(ACEDocument doc, List<? extends ACEObject> objects, Map<? extends ACEEventArgumentType, Integer> objectCountMap, Map<? extends ACEEventArgumentType, Integer> mentionCountMap, int[] objectCount, int[] mentionCount){
		for(ACEObject object: objects){
			if(object.mentions().isEmpty() && object.type() != ACERelation.ACERelationType.METONYMY){
				System.out.println("Non-metonymy empty mention set at "+doc.uri+": "+object.id);
			}
			int count = objectCountMap.getOrDefault(object.type(), 0);
			objectCountMap.put(object.type(), count+1);
			count = objectCountMap.getOrDefault(object.subtype(),  0);
			objectCountMap.put(object.subtype(), count+1);
			count = mentionCountMap.getOrDefault(object.type(), 0);
			// Metonymy relations do not have mentions
			mentionCountMap.put(object.type(), count+Math.max(1, object.mentions().size()));
			count = mentionCountMap.getOrDefault(object.subtype(),  0);
			mentionCountMap.put(object.subtype(), count+Math.max(1, object.mentions().size()));
			objectCount[0] += 1;
			mentionCount[0] += object.mentions().size();
			for(ACEObjectMention<?> mention: object.mentions()){
				if(!mention.text.equals(unescape(mention.getText(doc.text)))){
					System.err.println("===TEXT===");
					System.err.println(doc.text);
					System.err.println("===FULL TEXT===");
					System.err.println(doc.fullText);
					System.err.println("===SGM===");
					System.err.println(doc.uri);
					System.err.println("===TEXT LENGTH===");
					System.err.println(doc.text.length());
					System.err.println("===OFFSET===");
					System.err.println(doc.offset);
					System.err.println("===MENTION===");
					System.err.println(mention.text);
					System.err.println(mention.span);
					throw new RuntimeException(mention.text+" != "+unescape(mention.getText(doc.text)));
				}
			}
		}
	}
	
	private static void printHelp(){
		printHelp(null);
	}
	
	private static void printHelp(String message){
		if(message != null){
			System.out.println(message);
			System.out.println();
		}
		System.out.println(
				"Usage: java -jar acereader-0.1.jar -ace2004Dir <dirname> -ace2005Dir <dirname>\n"
				+ "\t[-ace2004IncludeDomains (arabic_treebank,bnews,chinese_treebank,fisher_transcripts,nwire)]\n"
				+ "\t[-ace2004ExcludeDomains (arabic_treebank,bnews,chinese_treebank,fisher_transcripts,nwire)]\n"
				+ "\t[-ace2005IncludeDomains (bc,bn,cts,nw,un,bl)]\n"
				+ "\t[-ace2005ExcludeDomains (bc,bn,cts,nw,un,bl)]\n"
				+ "\t[-convertEntitiesToInline]\n"
				+ "\t[-ace2004OutputBasePath]\n"
				+ "\t[-ace2005OutputBasePath]\n"
				+ "\t[-dataSplit <two_or_three_comma_separated_values>]\n"
				+ "\t[-tokenizer (stanford|regex)]\n"
				+ "\t[-posTagger (stanford)]\n"
				+ "\t[-splitter (stanford)]\n"
				+ "\t[-toCoNLLFormat]\n"
				+ "\t[-ignoreOverlaps]\n"
				+ "\t[-useBILOU]\n"
				+ "\t[-splitBySentences]\n"
				
				+ "\n"
				
				+ "-ace2004Dir <dirname>\n"
				+ "\tPath to ACE2004 directory containing the domain subdirectories.\n"
				
				+ "\n"
				
				+ "-ace2005Dir <dirname>\n"
				+ "\tPath to ACE2004 directory containing the domain subdirectories.\n"
				+ "\tOnly the data from timex2norm version will be used.\n"
				
				+ "\n"
				
				+ "-ace2004{Include,Exclude}Domains <domains>\n"
				+ "\tTo include/exclude certain domains from ACE2004.\n"
				+ "\tOnly one of -ace2004IncludeDomains -ace2004ExcludeDomains will take effect.\n"
				+ "\tIf -ace2004IncludeDomains is specified, only those domains will be included.\n"
				+ "\tIf -ace2004ExcludeDomains is specified, all except those domains will be included.\n"
				+ "\tPut a subset of these separated by comma:\n"
				+ "\t- arabic_treebank\n"
				+ "\t- bnews\n"
				+ "\t- chinese_treebank\n"
				+ "\t- fisher_transcripts\n"
				+ "\t- nwire\n"
				
				+ "\n"
				
				+ "-ace2005{Include,Exclude}Domains <domains>\n"
				+ "\tTo include/exclude certain domains from ACE2005.\n"
				+ "\tOnly one of -ace2005IncludeDomains -ace2005ExcludeDomains will take effect.\n"
				+ "\tIf -ace2005IncludeDomains is specified, only those domains will be included.\n"
				+ "\tIf -ace2005ExcludeDomains is specified, all except those domains will be included.\n"
				+ "\tPut a subset of these separated by comma:\n"
				+ "\t- bc\n"
				+ "\t- bn\n"
				+ "\t- cts\n"
				+ "\t- nw\n"
				+ "\t- un\n"
				+ "\t- bl\n"
				
				+ "\n"
				
				+ "-convertEntitiesToInline\n"
				+ "\tPrint the entities into files.\n"
				+ "\tNeed -ace2004OutputBasePath, -ace2005OutputBasePath, and -dataSplit options.\n"
				
				+ "\n"
				
				+ "-ace{2004,2005}OutputDir <path>\n"
				+ "\tThe directory for ACE2004 and ACE2005 inline output.\n"
				
				+ "\n"
				
				+ "-dataSplit <two_or_three_comma_separated_values>\n"
				+ "\tSplit into multiple files according to the ratio given.\n"
				+ "\tYou can give two (train+test) or three (train+dev+test) values.\n"
				+ "\tExamples:\n"
				+ "\t-dataSplit 90,10 to split into 90% training and 10% test\n"
				+ "\t-dataSplit 0.8,0.1,0.1 to split into 80% training, 10% dev, and 10% test\n"
				
				+ "\n"
				
				+ "-tokenizer (stanford,regex)\n"
				+ "\tIf specified, the sentences will be tokenized, and the spans will be token-based.\n"
				+ "\tCurrently there are two tokenizers supported: Stanford and regex-based.\n"
				
				+ "\n"
				
				+ "-posTagger (stanford)\n"
				+ "\tIf specified, the output files will contain POS tags.\n"
				+ "\tCurrently only Stanford POS tagger is supported.\n"
				
				+ "\n"
				
				+ "-splitter (stanford)\n"
				+ "\tThe sentence splitter to split the data.\n"
				+ "\tCurrently only Stanford Splitter is supported.\n"
				
				+ "\n"
				
				+ "-toCoNLLFormat\n"
				+ "\tOutput conversion in CoNLL format.\n"
				
				+ "\n"
				
				+ "-ignoreOverlaps\n"
				+ "\tIgnore overlapping entities by removing the shorter entity in an overlap.\n"
				
				+ "\n"
				
				+ "-useBILOU\n"
				+ "\tTo use BILOU (Begin, Inside, Last, Outside, Unit) format instead of BIO.\n"
				+ "\tOnly applicable when -toCoNLLFormat is used.\n"
				
				+ "\n"
				
				+ "-splitBySentences\n"
				+ "\tSplit into training, development, and test based on sentences instead of documents.\n"
				
				);
		if(message != null){
			System.out.println("===");
			System.out.println(message);
		}
	}
}
