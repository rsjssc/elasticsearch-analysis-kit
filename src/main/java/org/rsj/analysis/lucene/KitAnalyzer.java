package org.rsj.analysis.lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
/**
 * KitAnalysis 中文分词 1.0
 * 2016.5.4
 * @author kit
 *
 */
public final class KitAnalyzer extends Analyzer{
	//这里2.0可以加入适合搜索引擎的smart模式，即把句子结构相同的无歧义分词方式都作为有效的分词
	/**
	 * KIT分词器Lucene  Analyzer接口实现类
	 * 
	 * 默认细粒度切分算法
	 */
	public KitAnalyzer(){
	}


	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer _KitTokenizer = new KitTokenizer();
		return new TokenStreamComponents(_KitTokenizer);
    }
}
