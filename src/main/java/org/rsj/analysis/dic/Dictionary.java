package org.rsj.analysis.dic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.core.Token;

/**
 * 具体的每一个词典类
 * @author kit
 *
 */
public class Dictionary {
	
	private String staticDictPath;   //静态词典
	private String dynamicDictPath;   //动态词典路径
	private boolean isContainDynamicDict;	//通过N-gram加入新词的新词典需要有定时检查，并重新建立
	private List<Word> wordsWithWordType;
	private List<String> words;
	private byte[][] keys;
	private Map<String,Long> dynamicWords;
	

	private DoubleArrayTrie1 dict;	//DAT树
	
	public Dictionary(Configuration cfg, boolean isContainDynamicDict) {
		this.isContainDynamicDict = isContainDynamicDict;
		if(isContainDynamicDict) {//加载主词典
			this.staticDictPath = cfg.getDictRoot() +"/"+ Dictionaries.MAIN_DICT;
//			this.dynamicDictPath = cfg.getDynamicWordsPath();
			this.wordsWithWordType = new ArrayList<Word>();
			readFromDictFileWithWordType(staticDictPath);
//			readFromDictFile(dynamicDictPath);
			long start = System.currentTimeMillis();
			Collections.sort(wordsWithWordType);
			System.out.println("sort time: "+ (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			keys = new byte[wordsWithWordType.size()][];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = wordsWithWordType.get(i).getWord().getBytes();
			}
			dict = new DoubleArrayTrie1();
			dict.build(keys);
			System.out.println("build time: "+ (System.currentTimeMillis() - start));
		}else {//加载停止词典
			this.staticDictPath = cfg.getDictRoot() + "/"+ Dictionaries.STOP_DICT;
			this.words = new ArrayList<String>();
			readFromDictFile(staticDictPath);
			keys = new byte[words.size()][];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = words.get(i).getBytes();
			}
			dict = new DoubleArrayTrie1();
			dict.build(keys);
		}
	}
	
	private void readFromDictFile(String Path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
	        while ((line = reader.readLine()) != null)
	        {
	        	Words.add(line.trim());
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void readFromDictFileWithWordType(String Path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
	        while ((line = reader.readLine()) != null)
	        {
	        	String[] t = line.split(" ");
	        	if(t.length != 2){System.out.println("dict wrong");return;}
	        	String word = t[0];
	        	Words.add(word);
	        	WordsXing.add(t[1]);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public LinkedList<Token> matchMaxToken(int offset, char[] charArray, int begin) {
		LinkedList<Integer> resultIndex = dict.commonPrefixSearch(charArray, begin);
		LinkedList<Token> resultTokens = new LinkedList<Token>();
		for (int index : resultIndex)
        {
			Token token = new Token(offset, begin, Words.get(index).length(), Token.CNWORD, WordsXing.get(index));
			resultTokens.add(token);
        }
		return resultTokens;
	}
	
	public int matchExactly(char[] charArray, int begin) {//输入上下文对象
		return dict.exactMatchSearch(charArray, begin);
	}
}
