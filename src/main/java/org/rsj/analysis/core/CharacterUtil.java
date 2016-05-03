package org.rsj.analysis.core;

import java.util.Arrays;

public class CharacterUtil {
	public static final int CHAR_USELESS = 0;
	public static final int CHAR_ARABIC = 0x01;
	public static final int CHAR_ENGLISH = 0x02;
	public static final int CHAR_CHINESE = 0x04;
	
	//letter链接符号
	private static final char[] Letter_Connector = new char[]{'#' , '&' , '+' , '-' , '.' , '@' , '_'};
	//数字符号
	private static final char[] Num_Connector = new char[]{',' , '.'};
	
	/**
	 * 识别字符类型
	 * @param input
	 * @return int CharacterUtil定义的字符类型常量
	 */
	static int identifyCharType(char input){
		if(input >= '0' && input <= '9'){
			return CHAR_ARABIC;
			
		}else if((input >= 'a' && input <= 'z')
				|| (input >= 'A' && input <= 'Z')){
			return CHAR_ENGLISH;
			
		}else {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(input);
			if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A){
				//目前已知的中文字符UTF-8集合
				return CHAR_CHINESE;
				
			}
		}
		//其他的不做处理的字符
		return CHAR_USELESS;
	}
	/**
	 * 进行字符规格化（全角转半角，大写转小写处理）
	 * @param input
	 * @return char
	 */
	static char regularize(char input){
        if (input == 12288) {
            input = (char) 32;
            
        }else if (input > 65280 && input < 65375) {
            input = (char) (input - 65248);
            
        }else if (input >= 'A' && input <= 'Z') {
        	input += 32;
		}
        return input;
	}
	
	/**
	 * 判断是否是字母连接符号
	 */
	static boolean isLetterConnector(char input){
		int index = Arrays.binarySearch(Letter_Connector, input);
		return index >= 0;
	}
	/**
	 * 判断是否是数字连接符号
	 */
	static boolean isNumConnector(char input){
		int index = Arrays.binarySearch(Num_Connector, input);
		return index >= 0;
	}
	
	public static byte[] charToBytes(char inputChar){
		byte[] outputByte;
		if (inputChar < 0x80) {
			outputByte = new byte[1];
			outputByte[0] = (byte)inputChar;
			return outputByte;
		}else {
			outputByte = new byte[3];
			outputByte[0] = (byte)(0xe0 | ((inputChar >> 12)) & 0x0f);
			outputByte[1] = (byte)(0x80 | ((inputChar >> 6) & 0x3f));
			outputByte[2] = (byte)(0x80 | (inputChar & 0x3f));
			return outputByte;
		} 
	}
	
}
