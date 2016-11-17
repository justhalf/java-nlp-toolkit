/**
 * 
 */
package justhalf.nlp.reader.acereader;

import static justhalf.nlp.reader.acereader.ACEDocument.unescape;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import justhalf.nlp.reader.acereader.ACEEntity.ACEEntitySubType;
import justhalf.nlp.reader.acereader.ACEEntity.ACEEntityType;
import justhalf.nlp.reader.acereader.ACEEvent.ACEEventSubType;
import justhalf.nlp.reader.acereader.ACEEvent.ACEEventType;
import justhalf.nlp.reader.acereader.ACERelation.ACERelationSubType;
import justhalf.nlp.reader.acereader.ACERelation.ACERelationType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueType;
import justhalf.nlp.tokenizer.StanfordTokenizer;
import justhalf.nlp.tokenizer.Tokenizer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * 
 */
public class Main {
	
	private static class CommaSplitter implements ArgumentAction{

		/* (non-Javadoc)
		 * @see net.sourceforge.argparse4j.inf.ArgumentAction#run(net.sourceforge.argparse4j.inf.ArgumentParser, net.sourceforge.argparse4j.inf.Argument, java.util.Map, java.lang.String, java.lang.Object)
		 */
		@Override
		public void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs, String flag, Object value)
				throws ArgumentParserException {
			attrs.put("sections", Arrays.asList(((String)value).split(",")));
		}

		/* (non-Javadoc)
		 * @see net.sourceforge.argparse4j.inf.ArgumentAction#onAttach(net.sourceforge.argparse4j.inf.Argument)
		 */
		@Override
		public void onAttach(Argument arg) {}

		/* (non-Javadoc)
		 * @see net.sourceforge.argparse4j.inf.ArgumentAction#consumeArgument()
		 */
		@Override
		public boolean consumeArgument() {
			return true;
		}
		
	}

	public static void main(String[] args){
//		String sgm = "data/ACE2005/English/nw/adj/AFP_ENG_20030304.0250.sgm";
//		String sgm = "data/ACE2004/English/nwire/APW20001001.2021.0521.sgm";
//		File ace2004Dir = new File("data/ACE2004/English/");
//		for(File subdir: ace2004Dir.listFiles()){
//			if(!subdir.isDirectory()){
//				continue;
//			}
//			if(!subdir.getName().matches("bnews|nwire")) continue;
//			for(File sgmFile: subdir.listFiles()){
//				if(!sgmFile.getName().endsWith(".sgm")){
//					continue;
//				}
//				fileList.add(sgmFile);
//			}
//		}
		ArgumentParser argParser = ArgumentParsers.newArgumentParser("ACEReader")
				.defaultHelp(true)
				.description("Read ACE dataset (2004 or 2005) and prints the statistics");
		ArgumentGroup inputGroup = argParser.addArgumentGroup("inputDirectory")
				.description("The directory containing ACE dataset");
		inputGroup.addArgument("--inputDir04")
				.help("The directory containing ACE 2004 dataset");
		inputGroup.addArgument("--inputDir05")
				.help("The directory containing ACE 2005 dataset");
		argParser.addArgument("--sections")
				.action(new CommaSplitter())
				.setDefault(new String[]{"arabic_treebank", "bnews", "chinese_treebank", "fisher_transcripts",
										"nwire", "bc", "bn", "cts", "nw", "un", "wl"})
				.help("The sections to be used.\n"
						+ "For ACE 2004 this could be \"arabic_treebank\", \"bnews\", \"chinese_treebank\", "
						+ "\"fisher_transcripts\", or \"nwire\"\n"
						+ "\n"
						+ "For ACE 2005 this could be \"bc\", \"bn\", \"cts\", \"nw\", \"un\", or \"wl\".\n"
						+ "\n"
						+ "You can specify more than one sections, separated by comma.");
		Namespace ns = null;
		try{
			ns = argParser.parseArgs(args);
		} catch (ArgumentParserException e){
			argParser.handleError(e);
			System.exit(1);
		}
		if(ns.getString("inputDir04") == null && ns.getString("inputDir05") == null){
			System.out.println("One of --inputDir04 or --inputDir05 is required");
			argParser.printUsage();
			System.exit(1);
		}
		String inputDirectory04 = ns.getString("inputDir04");
		String inputDirectory05 = ns.getString("inputDir05");
		List<String> sections = ns.getList("sections");
		
		List<File> fileList = new ArrayList<File>();
		
		if(inputDirectory04 != null){
			System.out.println("Reading ACE2004 dataset at: "+inputDirectory04);
			readFileList(inputDirectory04, sections, fileList, true);
		}
		if(inputDirectory05 != null){
			System.out.println("Reading ACE2005 dataset at: "+inputDirectory05);
			readFileList(inputDirectory05, sections, fileList, false);
		}
		System.out.println("Sections considered: "+sections);
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
		Tokenizer tokenizer = new StanfordTokenizer();
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
				
				// Count mentions and objects
				count(doc, doc.entities, entityTypeCount, entityTypeMentionCount, entityCount, entityMentionCount);
				count(doc, doc.relations, relationTypeCount, relationTypeMentionCount, relationCount, relationMentionCount);
				count(doc, doc.values, valueTypeCount, valueTypeMentionCount, valueCount, valueMentionCount);
				count(doc, doc.events, eventTypeCount, eventTypeMentionCount, eventCount, eventMentionCount);
				
				// Count mention overlaps
				for(int i=0; i<doc.mentions.size(); i++){
					ACEEntityMention mention1 = doc.mentions.get(i);
					boolean hasOverlap = false;
					for(int j=0; j<doc.mentions.size(); j++){
						if(j==i) continue;
						ACEEntityMention mention2 = doc.mentions.get(j);
						hasOverlap |= mention1.overlapsWith(mention2);
					}
					overlapCount += hasOverlap ? 1 : 0;
					int wordCount = tokenizer.tokenize(mention1.text).size();
					int count = wordCountInMention.getOrDefault(wordCount, 0);
					wordCountInMention.put(wordCount, count+1);
				}
				
				// Count lowercased documents
				allLowercaseCount += doc.textInLowercase ? 1 : 0;
				
				docs.add(doc);
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
	}

	private static void readFileList(String inputDirectory, List<String> sections, List<File> fileList, boolean is2004) {
		File aceDir = new File(inputDirectory);
		for(File subdir: aceDir.listFiles()){
			if(!subdir.isDirectory()){
				continue;
			}
			if(!sections.contains(subdir.getName())) continue;
			File fp1 = new File(subdir.getAbsolutePath()+(is2004 ? "" : "/timex2norm"));
			for(File sgmFile: fp1.listFiles()){
				if(!sgmFile.getName().endsWith(".sgm")){
					continue;
				}
				fileList.add(sgmFile);
			}
		}
	}
	
	private static void count(ACEDocument doc, List<? extends ACEObject> objects, Map<? extends ACEEventArgumentType, Integer> objectCountMap, Map<? extends ACEEventArgumentType, Integer> mentionCountMap, int[] objectCount, int[] mentionCount){
		for(ACEObject object: objects){
			if(object.mentions().isEmpty()){
				System.out.println("Empty mention at "+doc.uri+": "+object.id);
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



}
