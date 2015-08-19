package justhalf.nlp.test;

import java.util.Scanner;

import justhalf.nlp.sentenceparser.SentenceParser;
import justhalf.nlp.sentenceparser.StanfordSentenceParser;

public class SentenceParserTest extends TestHelper{
	
	public static void main(String[] args){
		SentenceParser parser = new StanfordSentenceParser();
		Scanner sc = new Scanner(System.in);
		String line;
		while((line = getNextLine(sc)) != null){
			System.out.println(parser.parse(line));
		}
		sc.close();
	}
}