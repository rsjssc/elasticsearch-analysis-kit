package org.rsj.analysis.core;

import java.util.LinkedList;

public class SentenceSegment {
	private LinkedList<Token> tokensList;
	
	//这个冲突链表的的起始和终止位置
		private int begin;
		private int end;
		
		public SentenceSegment() {
			// TODO Auto-generated constructor stub
			this.tokensList = new LinkedList<Token>();
			this.begin = -1;
			this.end = -1;
		}
		
		public SentenceSegment(Token token) {
			// TODO Auto-generated constructor stub
			this.tokensList = new LinkedList<Token>();
			this.begin = token.getBegin();
			this.end = token.getBegin() + token.getLength();
			this.tokensList.add(token);
		}
		
		/**
		 * 链表由小到大排列，起始位置越靠前越小，长度越长越小
		 * @param token
		 * @return
		 */
		public void addToSentenceSeg(Token token) {
			if(this.tokensList.isEmpty()){
				this.addToken(token);
				this.begin = token.getBegin();
				this.end = token.getBegin() + token.getLength();
			}else{
				this.addToken(token);
				if(token.getBegin() + token.getLength() > this.end){
					this.end = token.getBegin() + token.getLength();
				}
			}
		}
		
//		private boolean conflictCheck(Token token) {
//			return (token.getBegin() >= this.begin && token.getBegin() < this.end) || 
//					(this.begin >= token.getBegin() && this.begin < token.getBegin() + token.getLength());
//		}
		
		private void addToken(Token token) {
			if(this.tokensList.isEmpty()){
				tokensList.add(token);
				return;
			}
			if(this.tokensList.peekLast().compareTo(token) < 0){//链表中最后一个词比token小，词元接入链表尾部
				tokensList.addLast(token);
			}else if(this.tokensList.peekFirst().compareTo(token) > 0){//链表中最后一个词比token大，词元接入链表头部
				tokensList.addFirst(token);
			}else{	
				System.out.println("inser");
				int index = tokensList.size() - 1;
				while(index > 0 && tokensList.get(index).compareTo(token) > 0) {
					index--;
				}
				if (tokensList.get(index).compareTo(token) < 0) {//防止等于0，相等不加
					tokensList.add(index + 1, token);
				}
			}
		}

		public LinkedList<Token> getConflictList() {
			return tokensList;
		}

		public int getBegin() {
			return begin;
		}

		public int getEnd() {
			return end;
		}
}
