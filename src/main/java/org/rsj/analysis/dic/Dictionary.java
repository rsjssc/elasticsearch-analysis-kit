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
	

	private DoubleArrayTrie dict;	//DAT树
	
	public Dictionary(Configuration cfg, boolean isContainDynamicDict) {
		this.isContainDynamicDict = isContainDynamicDict;
		if(isContainDynamicDict) {//加载主词典
			this.staticDictPath = cfg.getDictRoot() +"/"+ Dictionaries.MAIN_DICT;
//			this.dynamicDictPath = cfg.getDynamicWordsPath();//可以通过配置文件定义动态词典的位置
			this.dynamicDictPath = cfg.getDictRoot() +"/"+ Dictionaries.Dynamic_DICT;
			this.wordsWithWordType = new ArrayList<Word>();
			long start = System.currentTimeMillis();
			readFromDictFileWithWordType(staticDictPath);
			readFromDictFileWithWordType(dynamicDictPath);
			System.out.println("read file time: "+ (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			Collections.sort(wordsWithWordType);
			System.out.println("sort time: "+ (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			keys = new byte[wordsWithWordType.size()][];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = wordsWithWordType.get(i).getWord().getBytes();
			}
			dict = new DoubleArrayTrie();
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
			dict = new DoubleArrayTrie();
			dict.build(keys);
		}
	}
	/**
	 * 读取没有词性的词典
	 * @param Path
	 */
	private void readFromDictFile(String Path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
	        while ((line = reader.readLine()) != null)
	        {
	        	words.add(line.trim());
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
	/**
	 * 读取有词性的词典
	 * @param Path
	 */
	private void readFromDictFileWithWordType(String Path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
	        while ((line = reader.readLine()) != null)
	        {
	        	Word newWord = new Word(line);
	        	wordsWithWordType.add(newWord);
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
			Token token = new Token(offset, begin, wordsWithWordType.get(index).getWord().length(), Token.CNWORD, wordsWithWordType.get(index).getWordType());
			resultTokens.add(token);
        }
		return resultTokens;
	}
	
	public int matchExactly(String text) {//输入上下文对象
		return dict.exactMatchSearch(text);
	}
}
