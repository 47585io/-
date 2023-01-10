package com.mycompany.who.Share;

import android.text.*;
import java.util.*;

public class String_Splitor
{
	public static Spanned colorText(String text,String color) {
		//返回具有样式的Spanned
		return Html.fromHtml("<font color='"+color+"'>"+text+"</font>",Html.FROM_HTML_OPTION_USE_CSS_COLORS);
	}
	public static String textColor(String src,String color){
		//返回原文转换成保留格式的HTML文本
		String target="";
		int tmp =src.indexOf('\n');
		target=target.replaceAll("<","&lt;");
		target=target.replaceAll(">","&gt;");
		//替换被HTML解析的字符
		target="<pre style='display:inline-block;color:"+color+";'>"+src+"</pre>";

		if(tmp!=-1 && tmp<src.length()-1){
		    target="<br/>"+target;
		}
		else if(tmp!=-1){
			target+="<br/>";	
		}
		//如果有换行在最后，就在最后加一个"<br/>"，否则在最前面加
		return target;
	}
	
	
	public static boolean IsAtoz(char ch){
		if(ch>='a' && ch <='z')
			return true;
		else if(ch>='A' && ch <='Z')
			return true;
		return false;
	}

	public static boolean indexOfNumber(char ch){
		if(ch>='0'&&ch<='9')
			return true;
		return false;
	}

	public static boolean indexOfNumber(String src){
		int i;
	    for(i=0;i<src.length();i++){
			if(!indexOfNumber(src.charAt(i)))
				return false;
		}
		return true;
	}
	
	public static ArrayList<Integer> indexsOf(String str,String text){
		//查找文本中所有出现str的index
		if(str.length()==0||text.length()==0)
			return null;
		int index = 0-str.length();
		ArrayList<Integer> indexs = new ArrayList<Integer>();
		while(true){
		    index = text.indexOf(str,index+str.length());
			if(index==-1)
				break;
			indexs.add(index);
		}
		return indexs;
	}
	
	public static int calaN(String src,int index){
		int count = 0;
		while(index<src.length()&&(src.charAt(index)==' '||src.charAt(index)=='\t')){
			if(src.charAt(index)=='\t'){
				count+=4;
			}
			count++;
			index++;
		}
		return count;
	}
	public static String getNStr(String src,int n){
		StringBuffer arr= new StringBuffer();
		while(n-- !=0){
			arr.append(src);
		}
		return arr.toString();
	}
	
	
}
