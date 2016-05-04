package org.rsj.analysis.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.rsj.analysis.core.Segmenter;
import org.rsj.analysis.core.Token;

/**
 * kit分词器 Lucene Tokenizer适配器类
 */
public final class KitTokenizer extends Tokenizer {
	private Segmenter KitImplement;
	
	//词元文本属性
	private final CharTermAttribute termAtt;
	//词元位移属性
	private final OffsetAttribute offsetAtt;
	//词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
	private final TypeAttribute typeAtt;
	//记录最后一个词元的结束位置
	private int endPosition;

   	private int skippedPositions;

   	private PositionIncrementAttribute posIncrAtt;


    /**
	 * Tokenizer适配器类构造函数
	 * 声明分词需要返回的属性，包含偏移量，term，类型
	 * 可以加入smart模式
	 * @param in
     */
	public KitTokenizer(){
	    super();
	    offsetAtt = addAttribute(OffsetAttribute.class);
	    termAtt = addAttribute(CharTermAttribute.class);
	    typeAtt = addAttribute(TypeAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);

        KitImplement = new Segmenter(input);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.TokenStream#incrementToken()
	 */
	@Override
	public boolean incrementToken() throws IOException {
		//清除所有的词元属性
		clearAttributes();
        skippedPositions = 0;

        Token token = KitImplement.next();//这里调用了Segmenter中的分词函数取下一个token
		if(token != null){
            posIncrAtt.setPositionIncrement(skippedPositions +1 );

			//设置词元文本
			termAtt.append(token.getText());
			//设置词元长度
			termAtt.setLength(token.getLength());
			//设置词元位移
            offsetAtt.setOffset(correctOffset(token.getAbsBeginPosition()), correctOffset(token.getAbsEndPosition()));

            //记录分词的最后位置
			endPosition = token.getAbsEndPosition();
			//记录词元分类
			typeAtt.setType(token.getTokenType());			
			//返会true告知还有下个词元
			return true;
		}
		//返会false告知词元输出完毕
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader)
	 */
	@Override
	public void reset() throws IOException {
		super.reset();
		KitImplement.reset();
        skippedPositions = 0;
	}	
	
	@Override
	public final void end() throws IOException {
        super.end();
	    // set final offset
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
        posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
	}
}
