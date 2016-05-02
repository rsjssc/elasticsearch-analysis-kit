/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsj.analysis.details;

/**
 *
 * @author manabe
 */
public class Keyset {

    private byte[][] _keys;
    private int[] _values;
    
    public Keyset(byte[][] keys, int[] values) {
        _keys = keys;
        _values = values;
    }
    
    int numKeys() {
        return _keys.length;
    }
    
    byte[] getKey(int id) {
        return _keys[id];
    }
    
    byte getKeyByte(int keyId, int byteId) {
        if (byteId >= _keys[keyId].length) {
            return 0;
        }
        return _keys[keyId][byteId];
    }
    
    boolean hasValues() {
        return _values != null;
    }
    
    int getValue(int id) {
        if (hasValues()) {
            return _values[id];
        }
        return id;
    }
}
