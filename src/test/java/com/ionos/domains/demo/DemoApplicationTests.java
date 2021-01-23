package com.ionos.domains.demo;

import com.ionos.domains.demo.service.SimilarityService;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.*;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.relationship.AsymmetricRelationship;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@SpringBootTest
class DemoApplicationTests {
	Map<String, Double> wordNetSim = new HashMap<>();
	Map<String, Double> lSim = new HashMap<>();
	Map<String, Double> embeddingSim = new HashMap<>();

	@Test
	void contextLoads() {
		List<String> ngrams = Arrays.asList("grand", "small house");
		System.out.println(String.join(",", ngrams));
	}

	@Test
	void wordNet() throws JWNLException, CloneNotSupportedException {
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		//IndexWord word = dictionary.getIndexWord(POS.VERB, "accomplish");
		IndexWordSet word = dictionary.lookupAllIndexWords("grand");
		for (IndexWord indexWord : word.getIndexWordArray()) {
			System.out.println("==== " + indexWord.getLemma() + " ====");
			for (Synset synset : indexWord.getSenses()) {
				System.out.println(synset.getGloss());
				for (Word w : synset.getWords()) {
					System.out.println(w.getLemma());
				}
			}
		}
	}

	@Test
	void wordNetHyponym() throws JWNLException, CloneNotSupportedException {
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		//IndexWord word = dictionary.getIndexWord(POS.VERB, "accomplish");
		IndexWordSet word = dictionary.lookupAllIndexWords("grand");
		for (IndexWord indexWord : word.getIndexWordArray()) {
			System.out.println("==== " + indexWord.getLemma() + " ====");
			for (Synset synset : indexWord.getSenses()) {
				PointerUtils.getDirectHypernyms(synset).print();
				System.out.println(synset.getGloss());
				for (Word w : synset.getWords()) {
					System.out.println(w.getLemma());
				}
			}
		}
	}

	@Test
	void similarity() {
		printSimilarities("dog", "cat");
		System.out.println("===============");
		printSimilarities("dog", "animal");
		System.out.println("===============");
		printSimilarities("dog", "soul");
	}

	private void printSimilarities(String word1, String word2) {
		ILexicalDatabase db = new NictWordNet();
		RelatednessCalculator[] rcs = {
				new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
				new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
		};

		WS4JConfiguration.getInstance().setMFS(true);
		for ( RelatednessCalculator rc : rcs ) {
			double s = rc.calcRelatednessOfWords(word1, word2);
			System.out.println( rc.getClass().getName()+"\t"+s );
		}
	}
}
