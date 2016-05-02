package org.rsj.analysis.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rsj.analysis.dic.Dictionaries;

public class AnalysisContext {
	//默认缓冲区大小
		private static final int BUFF_SIZE = 4096;
		//缓冲区耗尽的临界值
		private static final int BUFF_EXHAUST_CRITICAL = 100;	
	 
		//字符窜读取缓冲
	    private char[] segmentBuff;
	    //字符类型数组
	    private int[] charTypes;
	    
	    
	    //记录Reader内已分析的字串总长度
	    //在分多段分析词元时，该变量累计当前的segmentBuff相对于reader起始位置的位移
		private int buffOffset;	
	    //当前缓冲区位置指针
	    private int cursor;
	    //最近一次读入的,可处理的字串长度
		private int available;

		
		//子分词器锁
	    //该集合非空，说明分词器在占用segmentBuff,若正在分析，则不能读入，否则读入
	    private boolean buffLocker;
	    
	    //原始分词结果集合，未经歧义处理
	    private LinkedList<ConflictTokensList> orgListsWhitConflict;  
//	    //原始分词结果集合，未经歧义处理
//	    private LinkedList<SentenceSegment> segments;
	    //LexemePath位置索引表
//	    private Map<Integer , LexemePath> pathMap;    
//	             最终分词结果集
	    private LinkedList<Token> results;
//	    public void printlnAllResult() {
//	    	Iterator<Token> it = results.iterator();
//	    	while(it.hasNext())
//	    		System.out.println(it.next().getText);
//	    }
	    public List<Token> getResults() {
			return results;
		}

	    public AnalysisContext(){
	    	this.segmentBuff = new char[BUFF_SIZE];
	    	this.charTypes = new int[BUFF_SIZE];
	    	this.buffLocker = false;
	    	this.orgListsWhitConflict = new LinkedList<ConflictTokensList>();
//	    	this.segments = new LinkedList<SentenceSegment>();
	    	this.results = new LinkedList<Token>();
	    }
	    
	    int getCursor(){
	    	return this.cursor;
	    }
	    
	    char[] getSegmentBuff(){
	    	return this.segmentBuff;
	    }
	    
	    char getCurrentChar(){
	    	return this.segmentBuff[this.cursor];
	    }
	    
	    int getCurrentCharType(){
	    	return this.charTypes[this.cursor];
	    }
	    
	    int getBufferOffset(){
	    	return this.buffOffset;
	    }
		
	    /**
	     * 根据context的上下文情况，填充segmentBuff 
	     * @param reader
	     * @return 返回待分析的（有效的）字串长度
	     * @throws java.io.IOException
	     */
	    int fillBuffer(Reader reader) throws IOException{
	    	System.out.println("going to read, now the offset is: "+ buffOffset);
	    	int readCount = 0;
	    	if(this.buffOffset == 0){
	    		System.out.println("this is first time fillBuffer");
	    		//首次读取reader
	    		readCount = reader.read(segmentBuff);
	    	}else{
	    		int offset = this.available - this.cursor;
	    		if(offset > 0){
	    			//最近一次读取的>最近一次处理的，将未处理的字串拷贝到segmentBuff头部
	    			System.arraycopy(this.segmentBuff , this.cursor , this.segmentBuff , 0 , offset);
	    			readCount = offset;
	    		}
	    		//继续读取reader ，以onceReadIn - onceAnalyzed为起始位置，继续填充segmentBuff剩余的部分
	    		readCount += reader.read(this.segmentBuff , offset , BUFF_SIZE - offset);
	    	}            	
	    	//记录最后一次从Reader中读入的可用字符长度
	    	this.available = readCount;
	    	//重置当前指针
	    	this.cursor = 0;
	    	System.out.println("this time read in word count: "+ readCount);
	    	//不用重置ConflictTokensList因为在歧义处理的时候需要将这个List中所有的冲突List弹出放到results里
//	    	this.orgTokens = new ArrayList<Token>();
	    	return readCount;
	    }

	    /**
	     * 初始化buff指针，处理第一个字符
	     */
	    void initCursor(){
	    	this.cursor = 0;
	    	this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
	    	this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
	    }
	    
	    /**
	     * 指针+1
	     * 成功返回 true； 指针已经到了buff尾部，不能前进，返回false
	     * 并处理当前字符
	     */
	    boolean moveCursor(){
	    	if(this.cursor < this.available - 1){
	    		this.cursor++;
	        	this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
	        	this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
	    		return true;
	    	}else{
	    		return false;
	    	}
	    }
		
		/**
		 * 判断当前segmentBuff是否已经用完
		 * 当前执针cursor移至segmentBuff末端this.available - 1
		 * @return
		 */
		boolean isBufferConsumed(){
			return this.cursor == this.available - 1;
		}
		
		/**
		 * 判断segmentBuff是否需要读取新数据
		 * 
		 * 满足一下条件时，
		 * 1.available == BUFF_SIZE 表示buffer满载
		 * 2.buffIndex < available - 1 && buffIndex > available - BUFF_EXHAUST_CRITICAL表示当前指针处于临界区内
		 * 3.!context.isBufferLocked()表示当前字符为USELESS且不是字母或者数字连接符
		 * 要中断当前循环（buffer要进行移位，并再读取数据的操作）
		 * @return
		 */
		boolean needRefillBuffer(){
			return this.available == BUFF_SIZE 
				&& this.cursor < this.available - 1   
				&& this.cursor  > this.available - BUFF_EXHAUST_CRITICAL
				&& (this.getCurrentCharType() == CharacterUtil.CHAR_USELESS && !(CharacterUtil.isLetterConnector(this.getCurrentChar()) || CharacterUtil.isNumConnector(this.getCurrentChar())));
		}
		
		/**
		 * 累计当前的segmentBuff相对于reader起始位置的位移
		 */
		void markBufferOffset(){
			this.buffOffset += this.cursor;
		}
		
		/**
		 * 向分词结果集添加词元
		 * @param lexeme
		 */
		void addTokenToListWhitConflict(Token token){
			ConflictTokensList lastList = orgListsWhitConflict.peekLast();
			if(lastList != null && lastList.addConflictToken(token)) {
				
			}else {
				ConflictTokensList newTokenList = new ConflictTokensList(token);
				orgListsWhitConflict.add(newTokenList);
			}
		}
		
//		/**
//		 * 将句子片段添加到
//		 * @param lexeme
//		 */
//		void addSentenceSegment(SentenceSegment segment){
//			segments.add(segment);
//		}
		
//		/**
//		 * 添加分词结果路径
//		 * 路径起始位置 ---> 路径 映射表
//		 * @param path
//		 */
//		void addLexemePath(LexemePath path){
//			if(path != null){
//				this.pathMap.put(path.getPathBegin(), path);
//			}
//		}
		
		
		/**
		 * 返回原始分词结果
		 * @return
		 */
		public LinkedList<ConflictTokensList> getOrgListsWhitConflict(){
			return this.orgListsWhitConflict;
		}
		
//		/**
////		 * 返回原始分词结果
////		 * @return
////		 */
//		public LinkedList<SentenceSegment> getOrgListsWhitConflict(){
//			return this.segments;
//		}
		
		//无歧义的结果加入results
		public void addToResults(ConflictTokensList conflictList) {
			System.out.println("add no arbitrate list to results");
//			if(resultIndex < conflictList)
			for (Token token : conflictList.getConflictList()) {
				results.add(token);
			}
			
		}
//		/**
//		 * 清空原始分词结果
//		 * @return
//		 */
//		public void setOrgTokens(LinkedList<Token> tokens){
//			this.orgListWhitConflict = tokens;
//		}
		
//		/**
//		 * 推送分词结果到结果集合
//		 * 1.从buff头部遍历到this.cursor已处理位置
//		 * 2.将map中存在的分词结果推入results
//		 * 3.将map中不存在的CJDK字符以单字方式推入results
//		 */
//		void outputToResult(){
//			int index = 0;
//			for( ; index <= this.cursor ;){
//				//跳过非CJK字符
//				if(CharacterUtil.CHAR_USELESS == this.charTypes[index]){
//					index++;
//					continue;
//				}
//				//从pathMap找出对应index位置的LexemePath
//				LexemePath path = this.pathMap.get(index);
//				if(path != null){
//					//输出LexemePath中的lexeme到results集合
//					Lexeme l = path.pollFirst();
//					while(l != null){
//						this.results.add(l);
//						//将index移至lexeme后
//						index = l.getBegin() + l.getLength();					
//						l = path.pollFirst();
//						if(l != null){
//							//输出path内部，词元间遗漏的单字
//							for(;index < l.getBegin();index++){
//								this.outputSingleCJK(index);
//							}
//						}
//					}
//				}else{//pathMap中找不到index对应的LexemePath
//					//单字输出
//					this.outputSingleCJK(index);
//					index++;
//				}
//			}
//			//清空当前的Map
//			this.pathMap.clear();
//		}
		
			
		/**
		 * 返回token
		 * 同时处理合并
		 * @return
		 */
		Token getNextToken(){
			//从结果集取出，并移除第一个Lexme
			Token result = this.results.pollFirst();
//			while(result != null){
//	    		//数量词合并
////	    		this.compound(result);
//	    		if(Dictionaries.getSingleton().isStopWord(this.segmentBuff ,  result.getBegin() , result.getLength())){
//	       			//是停止词继续取列表的下一个
//	    			result = this.results.pollFirst(); 				
//	    		}else{
//		 			//不是停止词, 生成lexeme的词元文本,输出
//	    			//最后真正输出的时候才取到真正的文本
//		    		result.setLexemeText(String.valueOf(segmentBuff , result.getBegin() , result.getLength()));
//		    		break;
//	    		}
//			}
			return result;
		}
		
		/**
		 * 重置分词上下文状态
		 */
		void reset(){		
			this.buffLocker = false;
	        this.orgListsWhitConflict = new LinkedList<ConflictTokensList>();
	        this.available =0;
	        this.buffOffset = 0;
	    	this.charTypes = new int[BUFF_SIZE];
	    	this.cursor = 0;
	    	this.results.clear();
	    	this.segmentBuff = new char[BUFF_SIZE];
//	    	this.pathMap.clear();
		}
		
//		/**
//		 * 组合词元
//		 */
//		private void compound(Lexeme result){
//
//			if(!this.useSmart){
//				return ;
//			}
//	   		//数量词合并处理
//			if(!this.results.isEmpty()){
//
//				if(Lexeme.TYPE_ARABIC == result.getLexemeType()){
//					Lexeme nextLexeme = this.results.peekFirst();
//					boolean appendOk = false;
//					if(Lexeme.TYPE_CNUM == nextLexeme.getLexemeType()){
//						//合并英文数词+中文数词
//						appendOk = result.append(nextLexeme, Lexeme.TYPE_CNUM);
//					}else if(Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()){
//						//合并英文数词+中文量词
//						appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
//					}
//					if(appendOk){
//						//弹出
//						this.results.pollFirst(); 
//					}
//				}
//				
//				//可能存在第二轮合并
//				if(Lexeme.TYPE_CNUM == result.getLexemeType() && !this.results.isEmpty()){
//					Lexeme nextLexeme = this.results.peekFirst();
//					boolean appendOk = false;
//					 if(Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()){
//						 //合并中文数词+中文量词
//	 					appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
//	 				}  
//					if(appendOk){
//						//弹出
//						this.results.pollFirst();   				
//					}
//				}
//
//			}
//		}
}
