package org.rsj.analysis.core;


/**
 * 分词得到的基本词元抽象 token
 * @author kit
 *
 */
public class Token {
	public static final String CNWORD = "CNWORD";
	public static final String ARABIC = "ARABIC";//这里是纯数字
	public static final String LETTER = "LETTER";//注意这里是英文和数组的组合，不能拆，因为类似邮箱，用户名等常常是英文和数字的组合
	public static final String OTHER = "OTHER";
	
	private int offset;//本token所属的section在整个文本中的起始位置
	private int begin;//相对section起始位置的偏移
	private int length;//token长度
	private String text;//token的内容，这里只有在最后输出的时候才会给text赋值，其余时刻为空
	private String textType;//词性
	private String tokenType;//token类型
	
	public Token(int begin, int length) {
		this.begin = begin;
		this.length = length;
	}
	
	public Token(int begin, int length, String tokenType) {
		this.begin = begin;
		this.length = length;
		this.tokenType = tokenType;
	}
	
	public Token(int offset, int begin, int length, String tokenType,String textType) {
		this.offset = offset;
		this.begin = begin;
		this.length = length;
		this.tokenType = tokenType;
		this.textType = textType;
	}
	
	public int getBegin() {
		return begin;
	}

	public int getLength() {
		return length;
	}

	/**
	 * 获取token的绝对起始位置
	 * @return
	 */
	public int getAbsBeginPosition() {
		return offset + begin;
	}
	
	/**
	 * 获取token在文本中的绝对结束位置
	 * @return int
	 */
	public int getAbsEndPosition(){
		return offset + begin + length;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getTextType() {
		return this.textType;
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.getAbsBeginPosition()).append("-").append(this.getAbsEndPosition());
		strBuilder.append(" : ").append(this.text).append(" : \t");
		strBuilder.append(this.tokenType).append(" : \t");
		strBuilder.append(" : ").append(this.textType);
		return strBuilder.toString();
	}
	
	/**
	 * token之间的比较函数，-1表示当前的比other小，返回1表示当前位置比other大，0表示相同
	 * 1、先比较起始位置，
	 * 2、起始位置相等则表长度，长的词放在前面，所以当前词的长度比other长的时候返回-1
	 * @param other
	 * @return
	 */
	public int compareTo(Token other) {
		//起始位置优先
        if(this.begin < other.getBegin()){
            return -1;
        }else if(this.begin == other.getBegin()){
        	//词元长度优先
        	if(this.length > other.getLength()){
        		return -1;
        	}else if(this.length == other.getLength()){
        		return 0;
        	}else {//this.length < other.getLength()
        		return 1;
        	}
        	
        }else{//this.begin > other.getBegin()
        	return 1;
        }
	}
	
	/**
	 * ICTPOS3.0词性标记集
	 */
}
