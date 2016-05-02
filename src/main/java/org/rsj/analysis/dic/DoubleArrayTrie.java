package org.rsj.analysis.dic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DoubleArrayTrie {
	private final static int BUF_SIZE = 16384;
	private final static int UNIT_SIZE = 8; // size of int + int

	private static class Node {
		int code;
		int depth;
		int left;
		int right;
	};

	private int check[];
	private int base[];

	private boolean used[];
	private int size;
	private int allocSize;
	private List<String> key;
	private int keySize;
	private int length[];
	private int value[];
	private int progress;
	private int nextCheckPos;
	// boolean no_delete_;
	int error_;

	// int (*progressfunc_) (size_t, size_t);

	// inline _resize expanded
	private int resize(int newSize) {
		if(base == null) System.out.println("origin size: "+0+", resize: "+newSize);
		else System.out.println("origin size: "+base.length+", resize: "+newSize);
		int[] base2 = new int[newSize];
		int[] check2 = new int[newSize];
		boolean used2[] = new boolean[newSize];
		if (allocSize > 0) {
			System.arraycopy(base, 0, base2, 0, allocSize);
			System.arraycopy(check, 0, check2, 0, allocSize);
			System.arraycopy(used2, 0, used2, 0, allocSize);
		}

		base = base2;
		check = check2;
		used = used2;

		return allocSize = newSize;
	}

	/**
	 * 返回当前父节点有多少个子节点，并创建Node加到List中
	 * @param parent 父节点的Node
	 * @param siblings
	 * @return
	 */
	private int fetch(Node parent, List<Node> siblings) {
		if (error_ < 0)
			return 0;

		int prev = 0;

		for (int i = parent.left; i < parent.right; i++) {
			if ((length != null ? length[i] : key.get(i).length()) < parent.depth)//一般不会执行，只有对当前词的长度小于父节点的长度才会跳过
				continue;

			String tmp = key.get(i);

			int cur = 0;
			if ((length != null ? length[i] : tmp.length()) != parent.depth)//一般都会执行
				cur = (int) tmp.charAt(parent.depth) + 1;//取c=当前词的第depth（深度，或者说第几个）个字

			if (prev > cur) {
				error_ = -3;
				return 0;
			}
			//不与前一个兄弟节点相同，相同为同一个节点，没必要在插入
			if (cur != prev || siblings.size() == 0) {
				Node tmp_node = new Node();
				tmp_node.depth = parent.depth + 1;
				tmp_node.code = cur;
				tmp_node.left = i;
//				System.out.println("get a new Node of siblings, depth and core:"+tmp_node.depth+" , "+tmp_node.code);
				if (siblings.size() != 0)
					siblings.get(siblings.size() - 1).right = i;//加入新节点，则老节点的右极限就是当前位置

				siblings.add(tmp_node);
			}

			prev = cur;
		}

		if (siblings.size() != 0)
			siblings.get(siblings.size() - 1).right = parent.right;

		return siblings.size();
	}

	private int insert(List<Node> siblings) {
		if (error_ < 0)
			return 0;

		int begin = 0;
//		System.out.println("insert a set of siblings: "+siblings.size());
//		System.out.println("now nextCheckPos is: "+nextCheckPos);
		int pos = ((siblings.get(0).code + 1 > nextCheckPos) ? siblings.get(0).code + 1
				: nextCheckPos) - 1;
//		System.out.println("real check posotion is: "+pos);
		int nonzero_num = 0;
		int first = 0;

		if (allocSize <= pos)
			resize(pos + 1);

		outer: while (true) {//找到一个pos使得所有的siblings能存放，保存在begin中，作为这一层的子节点的开始位置
			pos++;

			if (allocSize <= pos)
				resize(pos + 1);

			if (check[pos] != 0) {
				nonzero_num++;
				continue;
			} else if (first == 0) {
				nextCheckPos = pos;
				first = 1;
			}

			begin = pos - siblings.get(0).code;
			if (allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {
				// progress can be zero
				double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0
						* keySize / (progress + 1);
				resize((int) (allocSize * l));
			}

			if (used[begin])
				continue;

			for (int i = 1; i < siblings.size(); i++)
				if (check[begin + siblings.get(i).code] != 0)
					continue outer;

			break;
		}
		
//		System.out.println("choose the begin is: "+begin);

		// -- Simple heuristics --
		// if the percentage of non-empty contents in check between the
		// index
		// 'next_check_pos' and 'check' is greater than some constant value
		// (e.g. 0.9),
		// new 'next_check_pos' index is written by 'check'.
		if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
			nextCheckPos = pos;

		used[begin] = true;
		size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size
				: begin + siblings.get(siblings.size() - 1).code + 1;

		for (int i = 0; i < siblings.size(); i++)
			check[begin + siblings.get(i).code] = begin;

		for (int i = 0; i < siblings.size(); i++) {
			List<Node> new_siblings = new ArrayList<Node>();

			if (fetch(siblings.get(i), new_siblings) == 0) {//判断这一层的第i个节点是不是有子节点，没有则base中的值为
				base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings
						.get(i).left] - 1) : (-siblings.get(i).left - 1);

				if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
					error_ = -2;
					return 0;
				}

				progress++;
				// if (progress_func_) (*progress_func_) (progress,
				// keySize);
			} else {
				int h = insert(new_siblings);
				base[begin + siblings.get(i).code] = h;
			}
		}
		return begin;
	}

	public DoubleArrayTrie() {
		check = null;
		base = null;
		used = null;
		size = 0;
		allocSize = 0;
		// no_delete_ = false;
		error_ = 0;
	}

	// no deconstructor

	// set_result omitted
	// the search methods returns (the list of) the value(s) instead
	// of (the list of) the pair(s) of value(s) and length(s)

	// set_array omitted
	// array omitted

	void clear() {
		// if (! no_delete_)
		check = null;
		base = null;
		used = null;
		allocSize = 0;
		size = 0;
		// no_delete_ = false;
	}

	public int getUnitSize() {
		return UNIT_SIZE;
	}

	public int getSize() {
		return size;
	}

	public int getTotalSize() {
		return size * UNIT_SIZE;
	}

	public int getNonzeroSize() {
		int result = 0;
		for (int i = 0; i < size; i++)
			if (check[i] != 0)
				result++;
		return result;
	}

	public int build(List<String> key) {
		return build(key, null, null, key.size());
	}

	public int build(List<String> _key, int _length[], int _value[],
			int _keySize) {
		if (_keySize > _key.size() || _key == null)
			return 0;

		// progress_func_ = progress_func;
		key = _key;
		length = _length;
		keySize = _keySize;
		value = _value;
		progress = 0;

		resize(65536 * 32);

		base[0] = 1;
		nextCheckPos = 0;

		Node root_node = new Node();
		root_node.left = 0;
		root_node.right = keySize;
		root_node.depth = 0;

		List<Node> siblings = new ArrayList<Node>();
		fetch(root_node, siblings);
		insert(siblings);

		// size += (1 << 8 * 2) + 1; // ???
		// if (size >= allocSize) resize (size);

		used = null;
		key = null;

		return error_;
	}

	public int exactMatchSearch(char[] key) {
		return exactMatchSearch(key, 0, 0, 0);
	}
	
	public int exactMatchSearch(char[] key, int pos) {
		return exactMatchSearch(key, pos, 0, 0);
	}

	public int exactMatchSearch(char[] key, int pos, int len, int nodePos) {
		if (len <= 0)
			len = key.length;
		if (nodePos <= 0)
			nodePos = 0;

		int result = -1;

//		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int p;

		for (int i = pos; i < len; i++) {
			p = b + (int) (key[i]) + 1;
			if (b == check[p])
				b = base[p];
			else
				return result;
		}

		p = b;
		int n = base[p];
		if (b == check[p] && n < 0) {
			result = -n - 1;
		}
		return result;
	}

	public LinkedList<Integer> commonPrefixSearch(char[] key) {
		return commonPrefixSearch(key, 0, 0, 0);
	}
	
	public LinkedList<Integer> commonPrefixSearch(char[] key, int pos) {
		return commonPrefixSearch(key, pos, 0, 0);
	}

	public LinkedList<Integer> commonPrefixSearch(char[] key, int pos, int len,
			int nodePos) {
		if (len <= 0)
			len = key.length;
		if (nodePos <= 0)
			nodePos = 0;

		LinkedList<Integer> result = new LinkedList<Integer>();

//		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int n;
		int p;

		for (int i = pos; i < len; i++) {
			p = b;
			n = base[p];

			if (b == check[p] && n < 0) {
//				System.out.println("match!");
				result.addFirst(-n - 1);
			}

			p = b + (int) (key[i]) + 1;
			if (b == check[p])
				b = base[p];
			else
				return result;
		}

		p = b;
		n = base[p];

		if (b == check[p] && n < 0) {
//			System.out.println("match!");
			result.addFirst(-n - 1);
		}

		return result;
	}

	// debug
	public void dump() {
		for (int i = 0; i < size; i++) {
			System.err.println("i: " + i + " [" + base[i] + ", " + check[i]
					+ "]");
		}
	}
}
