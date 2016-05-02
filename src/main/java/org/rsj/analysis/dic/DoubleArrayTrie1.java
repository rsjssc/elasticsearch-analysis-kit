package org.rsj.analysis.dic;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rsj.analysis.details.DoubleArrayBuilder;
import org.rsj.analysis.details.Keyset;

public class DoubleArrayTrie1 {
	private static final int UNIT_SIZE = 4; // sizeof(int)
    private int[] _array;
    
    public void build(byte[][] keys) {
        Keyset keyset = new Keyset(keys, null);
        DoubleArrayBuilder builder = new DoubleArrayBuilder();
        builder.build(keyset);
        
        _array = builder.copy();
    }
    
    /**
     * Returns the corresponding value if the key is found. Otherwise returns -1.
     * This method converts the key into UTF-8.
     * @param key search key
     * @return found value
     */
    public int exactMatchSearch(String key) {
        int ret = 0;
        try {
            ret = exactMatchSearch(key.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException ex) {
            Logger.getLogger(DoubleArrayTrie1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    /**
     * Returns the corresponding value if the key is found. Otherwise returns -1.
     * @param key search key
     * @return found value
     */
    public int exactMatchSearch(byte[] key) {
        int unit = _array[0];
        int nodePos = 0;
        
        for (byte b : key) {
            // nodePos ^= unit.offset() ^ b
            nodePos ^= ((unit >>> 10) << ((unit & (1 << 9)) >>> 6)) ^ (b & 0xFF);
            unit = _array[nodePos];
            // if (unit.label() != b)
            if ((unit & ((1 << 31) | 0xFF)) != (b & 0xff)) {
                return -1;
            }
        }
        // if (!unit.has_leaf()) {
        if (((unit >>> 8) & 1) != 1) {
            return -1;
        }
        // unit = _array[nodePos ^ unit.offset()];
        unit = _array[nodePos ^ ((unit >>> 10) << ((unit & (1 << 9)) >>> 6))];
        // return unit.value();
        return unit & ((1 << 31) - 1);
    }
    
    /**
     * Returns the keys that begins with the given key and its corresponding values.
     * The first of the returned pair represents the length of the found key.
     * @param key
     * @param offset
     * @param maxResults
     * @return found keys and values
     */
    public List<Integer> commonPrefixSearch(byte[] key, int offset) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int unit = _array[0];
        int nodePos = 0;
        // nodePos ^= unit.offset();
        nodePos ^= ((unit >>> 10) << ((unit & (1 << 9)) >>> 6));
        for (int i = offset; i < key.length; ++i) {
            byte b = key[i];
            nodePos ^= (b & 0xff);
            unit = _array[nodePos];
            // if (unit.label() != b) {
            if ((unit & ((1 << 31) | 0xFF)) != (b & 0xff)) {
                return result;
            }

            // nodePos ^= unit.offset();
            nodePos ^= ((unit >>> 10) << ((unit & (1 << 9)) >>> 6));
            
            // if (unit.has_leaf()) {
            if (((unit >>> 8) & 1) == 1) {
//                if (result.size() < maxResults) {
                    // result.add(new Pair<i, _array[nodePos].value());
                    result.add(_array[nodePos] & ((1 << 31) - 1));
//                }
            }
        }
        return result;
    }
    
    public int size() {
        return _array.length;
    }
//    public class Pair<T, U> {
//        public final T first;
//        public final U second;
//
//        public Pair(T first, U second) {
//            this.first = first;
//            this.second = second;
//        }
//    }
    
}

