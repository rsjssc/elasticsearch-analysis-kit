package org.rsj.analysis.core;

import java.io.File;
import java.nio.file.Paths;

import org.rsj.analysis.dic.Dictionaries;


public class Configuration {
	
	
	/**
	 * 返回自带词典的根目录，不同运行环境下自带词典位置不同
	 * @return
	 */
	public String getDictRoot() {
		return Paths.get(
				new File(Dictionaries.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent(),"config")
				.toAbsolutePath().toString();
    }
	
	/**
	 * 返回配置文件中指定的动态词的目录 
	 * 使用过程是spark+本分词器+N-gram的输出目录
	 * @return
	 */
	public String getDynamicWordsPath() {
		return "";
	}
}
