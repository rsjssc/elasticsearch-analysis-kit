package org.rsj.analysis.test;

import java.util.*;

public class testTT {

	public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int x = in.nextInt();
        int k = in.nextInt();
        int temp = x;
        int result = x;
        int count = 0;
        int bit = 0;
        while(count != k) {
        	System.out.println("temp:"+temp);
        	System.out.println("bit:"+bit);
        	if((temp & 0x01) == 0){
        		result |= (0x01 << bit);
        		System.out.println("result:"+result);
        		count++;
        	}
        	bit++;
        	temp = temp>>1;
        }
        System.out.println(result^x);
        in.close();
    }
}
