package org.rsj.analysis.dic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.core.Token;


/**
 * 单例模式运行的所有词典管理类，启动时加载所有的词典
 * @author kit
 *
 */
public class Dictionaries {
	private static Dictionaries atom;
	
	private Dictionary MainDict;	//固定词典加上动态更新的词典
	private Dictionary StopWordDict; //停止词，输出tokens的时候，如果token在停止词词典中则去除，这里的停止词不是诸如“的”“了”这种词，这种词在取一个分词的段落的时候就会作为分隔
	
	private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private static Configuration configuration;
	private Dictionaries() {}
	
	public static final String MAIN_DICT = "main1.dict";
	public static final String STOP_DICT = "stopword.dict";
	/**
	 * 词典初始化
	 * 由于my-analyzer的词典采用Dictionary类的静态方法进行词典初始化
	 * 只有当Dictionaries类被实际调用时，才会开始载入词典，
	 * 这将延长首次分词操作的时间
	 * 该方法提供了一个在应用加载阶段就初始化字典的手段
	 * @return Dictionaries
	 */
	public static synchronized Dictionaries initial(Configuration cfg){
		if(atom == null){
			synchronized(Dictionaries.class){
				if(atom == null){
					atom = new Dictionaries();
					Dictionaries.configuration = cfg;
					atom.loadMainDict();
					atom.loadStopWordDict();

					//建立监控线程
					//10 秒是初始延迟可以修改的  60是间隔时间  单位秒
					pool.scheduleAtFixedRate(new Monitor(cfg.getDynamicWordsPath()), 10, 60, TimeUnit.SECONDS);

					return atom;
				}
			}
		}
		return atom;
	}
	/**
	 * 获取词典原子实例
	 * @return Dictionaries 单例对象
	 */
	public static Dictionaries getSingleton(){
		if(atom == null){
			throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
		}
		return atom;
	}
	/**
	 * 加载主词典
	 */
	private void loadMainDict() {
		MainDict = new Dictionary(Dictionaries.configuration,true);//true表示是主词典
	}
	
	private void loadStopWordDict() {
		StopWordDict = new Dictionary(Dictionaries.configuration, false);//false不会加载动态词语
	}
	
	/**
	 * 检索匹配主词典
	 * @return 返回匹配上的token
	 */
	public LinkedList<Token> matchInMainDict(int offset, char[] charArray){
		return matchInMainDict(offset, charArray, 0);
	}

	/**
	 * 检索匹配主词典
	 * @return Hit 匹配结果描述
	 */
	public LinkedList<Token> matchInMainDict(int offset, char[] charArray , int begin){
		return atom.MainDict.matchMaxToken(offset, charArray, begin);
	}
	
	public boolean isStopWord(char[] charArray , int begin) {
		int result = atom.StopWordDict.matchExactly(charArray, begin);
		if(-1 != result)
			return true;
		return false;
	}
}
