package com.mycompany.who.Share;

import java.util.*;

public class Array_Splitor
{
	public static ArrayList<String> And_Same(ArrayList<String> d1,ArrayList<String> d2){
		//合并相同元素
		if(d1==null&&d2==null){
			return null;
		}
		if(d1==null)
			return d2;
		if(d2==null)
			return d1;
		ArrayList<String> end = new ArrayList<String>();
		for(Object o: d1){
			if(d2.contains((String)o))
				end.add((String)o);
		}
		return end;
	}
	
	public static void delSame(Collection<String> dst,Collection<String> src){
		//删除dst中与src中相同的元素
		for(Object o: dst.toArray()){
			if(src.contains((String)o))
				dst.remove(o);
		}
	}
	public static void delSame(Collection<String> dst,String[] src){
		for(Object o: dst.toArray()){
			if(indexOf((String)o,src)!=-1)
				dst.remove(o);
		}
	}

	public static void delNumber(Collection<String> dst){
		//删除数字
		for(Object o: dst.toArray()){
			if(String_Splitor. indexOfNumber((String)o)){
				dst.remove(o);
			}
		}
	}
	
	public static int indexOf(char ch,char[]fuhao){
		//字符是否在排好序的数组
		if(fuhao==null)
			return -1;
		int low = 0;   
		int high = fuhao.length-1;   
		while(low <= high) {   
			int middle = (low + high)/2;   
			if(ch == fuhao[middle]) 
				return middle;   
			else if(ch<fuhao[middle])
				high = middle - 1;   
			else 
				low = middle + 1;
		}  
		return -1;  
	}

	public static int indexOf(String str,String[] keyword) {	
		//字符串是否在排好序的数组
	    if(str.length()==0|| keyword==null)
			return -1;
		int start=0;
		for(;start<keyword.length;start++)
			if(keyword[start].charAt(0)==str.charAt(0)){			
				break;
			}
		for(;start<keyword.length && str.charAt(0)==keyword[start].charAt(0) ;start++)
			if(keyword[start].equals(str))
				return start;
		return -1;
	}

    public static ArrayList<String> indexsOf(String str,String[] keyword,int start,Idea i) {	
		//查找数组中所有出现了str的元素
		if(str.length()==0 || keyword==null||keyword.length==0)
			return null;
	    ArrayList<String> words = new ArrayList<String>();
		for(String word:keyword){
			if(i.can(word,str,start)){
				words.add(word);
			}
		}
		if(words.size()==0)
			return null;
		return words;
	}


	public static ArrayList<String> indexsOf(String str,Collection<String> keyword,int start,Idea i) {	
		//查找集合中所有出现了str的元素
		if(str.length()==0 || keyword==null||keyword.size()==0)
			return null;
	    ArrayList<String> words = new ArrayList<String>();
		for(String word:keyword){
			if(i.can(word,str,start)){
				words.add(word);
			}
		}
		if(words.size()==0)
			return null;
		return words;
	}
	
	
	
	public static void sort(ArrayList<String> words){
		//按长度排序
		words.sort(new Comparator<String>(){
				@Override
				public int compare(String p1, String p2)
				{
					if(p1.length()>p2.length())
						return 1;
					else
						return -1;

				}
			});
	}

	public static void sort2(ArrayList<String> words){
		//将大写字符放后面
		words.sort(new Comparator<String>(){
				@Override
				public int compare(String p1, String p2)
				{
					if(p1.charAt(0)<p2.charAt(0)){
						return 1;
					}
					else if(p1.charAt(0)==p2.charAt(0))
						return 0;
					else
						return -1;
				}
			});
	}
	
	public static Idea getNo(){
		return new Idea(){

			@Override
			public boolean can(String s,String want,int start)
			{
				if(s.toLowerCase().indexOf(want.toLowerCase(),start)==start){
					//字符串出现位置必须在start
					return true;
				}
				return false;
			}
		};
	}
	
	public static Idea getyes(){
		return new Idea(){

			@Override
			public boolean can(String s,String want,int start)
			{
				if(s.toLowerCase().indexOf(want.toLowerCase(),start)!=-1){
					////字符串出现位置可以在start后
					return true;
				}
				return false;
			}
		};
	}
}


	
