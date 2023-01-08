package com.mycompany.who;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.lang.reflect.*;
import java.util.*;
import com.mycompany.who.CodeEdit.*;

public class CodeEdit extends EditText
{
	
	//一百行代码实现代码染色
	public static wordAndColor WordLib = new wordAndColor();
	public OtherWords WordLib2;
	public boolean IsModify=false;
	//你应该在所有会修改文本的函数添加设置IsModify，并在ontextChange中适当判断，避免死循环
	public boolean Enabled=false;
	private int tryLines=3;
	
	CodeEdit(Context cont,boolean Enabled){
		super(cont);
		WordLib2=new OtherWords();
		this.Enabled=Enabled;
		config();
	}
	
	public void config(){	
		setTextColor(0xffabb2bf);
		setBackgroundResource(0);
		setTypeface(Typeface.MONOSPACE);
		setHighlightColor(0xff515a6b);
		setTextSize(20);
		setLetterSpacing(0.01f);
		setLineSpacing(1.5f,1.5f);
	}

	public String getToDraw(int start,int end){
		IsModify=true;
		//获取选中文本
		if(end-start==0)
			return null;
		String text = getText().toString().substring(start,end);
		getText().replace(start,end,text);
		//清除上次的颜料
		IsModify=false;
		return text;
	}

	public ArrayList<wordIndex> startFind(String src,ArrayList<DoAnyThing> totalList){
		//开始查找，为了保留换行空格等，只replace单词本身，而不是src文本
		//Spanned本质是用html样式替换原字符串
		//html中，多个连续空格会压缩成一个，换行会替换成空格
		//防止重复（覆盖），只遍历一次
		StringBuffer nowWord = new StringBuffer();
		int nowIndex;
		ArrayList<wordIndex> nodes = new ArrayList<wordIndex>();
		for(nowIndex=0;nowIndex<src.length();nowIndex++){
			nowWord.append(src.charAt(nowIndex));
	//每次追加一个字符，交给totalList中的任务过滤
	//注意是先追加，index后++
	
			//如果是其它的，可以使用用户过滤方案
			for(DoAnyThing total:totalList){
				try{
				    int index= total.dothing(src,nowWord,nowIndex,nodes);
				    if(index>=nowIndex){
				        //单词已经找到了，不用找了
						nowIndex=index;
						break;
					}
				}catch(Exception e){}
			}
		}
		return nodes;
	}
	
	public void Draw(int start,int end,ArrayList<wordIndex> nodes){
		//反向染色，前面不受后面已有Spanned影响
		IsModify=true;
		int i;
		Editable editor=getText();
		String text = editor.toString().substring(start,end);
		for(i=nodes.size()-1;i>-1;i--)
		//在Edit中的真实下标开始，将范围内的单词染色
		    editor.replace(nodes.get(i).start+start,nodes.get(i).end+start,colorText(text.substring(nodes.get(i).start,nodes.get(i).end),WordLib.fromByteToColor(nodes.get(i).b)));
		IsModify=false;
	}
	public void DrawS(int start,int end,ArrayList<wordIndexS> nodes){
		//兼容wordIndexS的重载
		IsModify=true;
		int i;
		Editable editor=getText();
		for(i=nodes.size()-1;i>-1;i--)
		    editor.replace(nodes.get(i).start+start,nodes.get(i).end+start,colorText(nodes.get(i).to,WordLib.fromByteToColor(nodes.get(i).b)));
		IsModify=false;
	}
	
	public String getHTML(ArrayList<wordIndex> nodes,String text){
		//中间函数，用于生成HTML文本
		StringBuffer arr = new StringBuffer();
		int index=0;
		arr.append("<!DOCTYPE HTML><html><meta charset='UTF-8'/>   <style> * {  padding: 0%; margin: 0%; outline: 0; border: 0; color: #abb2bf;background-color: rgb(28, 32, 37);font-size: 10px;font-weight: 600p;tab-size: 4;overflow: scroll;font-family:monospace;line-height:16px;} *::selection {background-color: rgba(62, 69, 87, 0.4);}</style><body>");
		for(wordIndex node:nodes){
			//如果在上个node下个node之间有空缺的未染色部分，在html文本中也要用默认的颜色染色
			if(node.start>index)
				arr.append(textColor(text.substring(index,node.start),WordLib.Default));
			arr.append(textColor(text.substring(node.start,node.end),WordLib.fromByteToColor(node.b)));
			index=node.end;
		}
		if(index<text.length())
			arr.append(textColor(text.substring(index,text.length()),WordLib.Default));
		arr.append("<br><br><br><hr><br><br></body></html>");
		return arr.toString();
	}
	
	public void clearRepeatNode(ArrayList<wordIndex> nodes){
		//清除优先级低且位置重复的node
		int i,j;
		for(i=0;i<nodes.size();i++){
			wordIndex now = nodes.get(i);
			for(j=i+1;j<nodes.size();j++){
				if( nodes.get(j).equals(now)){
					nodes.remove(j);
					j--;
				}
			}
		}
	}
	
	public String reDrawColor(int start,int end){
		//立即进行一次默认的完整的染色
		
		String text = getToDraw(start,end);
	    ArrayList<DoAnyThing> totalList = new ArrayList<DoAnyThing>();
		AnyThingForJava AllThings = new AnyThingForJava();
		ArrayList<wordIndex> nodes = null;
		String HTML=null;
		
		if(text==null)
			return null;
			
		try{
					
			totalList.add(AllThings.getSans_TryFunc());	
			totalList.add(AllThings.getSans_TryVillber());
			totalList.add(AllThings.getSans_TryType());
			startFind(text,totalList);
			//试探一次，得到信息
			
			WordLib.delSame(WordLib2.lastFunc,WordLib.keyword);
			//函数名不可是关键字，但可以和变量或类型重名	
			WordLib.delSame(WordLib2.beforeType,WordLib.keyword);
			//类型不可是关键字
			WordLib.delSame(WordLib2.beforeType,WordLib2.historyVillber);
			//类型不可是变量，类型可以和函数重名
			WordLib.delSame(WordLib2.beforeType,WordLib.constword);
			//类型不可是保留字
			WordLib.delSame(WordLib2.historyVillber,WordLib.keyword);
			//变量不可是关键字
			WordLib.delSame(WordLib2.historyVillber,WordLib.constword);
			//变量不可是保留字
			WordLib.delNumber(WordLib2.beforeType);
			WordLib.delNumber(WordLib2.historyVillber);
			WordLib.delNumber(WordLib2.lastFunc);
			//去掉数字
			
			totalList.clear();
			totalList.add(AllThings.getGoTo_zhuShi());	
			totalList.add(AllThings.getGoTo_Str());
			totalList.add(AllThings.getNoSans_Keyword());
			totalList.add(AllThings.getNoSans_Func());
			totalList.add(AllThings.getNoSans_Villber());
			totalList.add(AllThings.getNoSans_Type());
			totalList.add(AllThings.getNoSans_Char());
			//请您在任何时候都加入getChar，因为它可以适时切割单词
			
		    nodes = startFind(text,totalList);
			clearRepeatNode(nodes);	
			//用已有信息查找一次，并清理重复的node
		    Draw(start,end,nodes);
			
			if(Enabled){
				HTML= getHTML(nodes,text);
			}
		}catch(Exception e){}
		
		totalList.clear();
		//清除上次的任务
		return HTML;
	}
	public String reDrawXML(int start,int end){

		String text = getToDraw(start,end);
	    ArrayList<DoAnyThing> totalList = new ArrayList<DoAnyThing>();
		AnyThingForJava AllThings = new AnyThingForJava();
		ArrayList<wordIndex> nodes = null;
		String HTML=null;

		if(text==null)
			return null;
			
		try{	
		totalList.clear();
		
		totalList.add(AllThings.getGoTo_zhuShi());	
		totalList.add(AllThings.getGoTo_Str());
		totalList.add(new AnyThingForXML().getDraw_Tag());
		totalList.add(new AnyThingForXML().getDraw_Attribute());
		totalList.add(AllThings.getNoSans_Char());
		
		nodes = startFind(text,totalList);
		clearRepeatNode(nodes);	
		//用已有信息查找一次，并清理重复的node
		Draw(start,end,nodes);
		
		if(Enabled){
			HTML= getHTML(nodes,text);
		}
		}catch(Exception e){}
		
		totalList.clear();
		//清除上次的任务
		return HTML;
	}


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
	public wordIndex tryWord(String src,int index){
		//试探前面的单词
		wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			while(WordLib.indexOf(src.charAt(index),WordLib.fuhao)!=-1)
				index--;
			tmp.end=index+1;
			while(WordLib.indexOf(src.charAt(index),WordLib.fuhao)==-1)
				index--;
			tmp.start=index+1;
		}catch(Exception e){
			return new wordIndex(0,0,(byte)0);
		}
		return tmp;
	}
	public wordIndex tryWordAfter(String src,int index){
		//试探后面的单词
		wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			while(WordLib.indexOf(src.charAt(index),WordLib.fuhao)!=-1)
				index++;
			tmp.start=index;
			while(WordLib.indexOf(src.charAt(index),WordLib.fuhao)==-1)
				index++;
			tmp.end=index;
		}catch(Exception e){
			return new wordIndex(0,0,(byte)0);
		}
		return tmp;
	}
	public int tryAfterIndex(String src,int index){
		//试探后面的非分隔符
		while(index<src.length()
			&&src.charAt(index)!='<'
			&&src.charAt(index)!='>'
			&&WordLib.indexOf(src.charAt(index),WordLib.spilt)!=-1){
			index++;
		}
		return index;
	}
	public int tryLine_Start(String src,int index){
		//试探当前下标所在行的起始
		int start= src.lastIndexOf('\n',index-1);	
		if(start==-1)
			start=0;
	    else
			start+=1;
		return start;
	}
	public int tryLine_End(String src,int index){
		//试探当前下标所在行的末尾
		int end=src.indexOf('\n',index);
		if(end==-1)
			end=src.length();
		return end;
	}
	
	abstract class DoAnyThing{
		abstract int dothing(String src,StringBuffer nowWord,int nowIndex,ArrayList<wordIndex> nodes);
	}
	
	class AnyThingForXML{
		public DoAnyThing getDraw_Tag(){
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
					//简单的一个xml方案
					wordIndex node;
					if(src.charAt(nowIndex)=='<'){
						node=tryWordAfter(src,nowIndex+1);
						node.b=3;
						WordLib2.Tag.add(src.substring(node.start,node.end));
						nodes.add(node);
						nowIndex=node.end-1;
						return nowIndex;
					}
					
					return -1;
				}
			};
		}
		public DoAnyThing getDraw_Attribute(){
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
					wordIndex node;
					if(src.charAt(nowIndex)=='='){
						node=tryWord(src,nowIndex-1);
						node.b=4;
						WordLib2.Attribut.add(src.substring(node.start,node.end));
						nodes.add(node);
						nowIndex=node.end-1;
						return nowIndex;
					}
					return -1;
				}
			};
		}
	}
	
	class AnyThingForJava{    
	
	//勇往直前的GoTo块，会突进一大段并阻拦其它块
		public DoAnyThing getGoTo_zhuShi(){
			//获取注释
			return new DoAnyThing(){		
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
					String key = WordLib.indexOfKey(src,nowIndex);
					if(key!=null){
						//如果它是一个任意的注释，找到对应的另一个，并把它们之间染色
						String value= WordLib.zhu_key_value.get(key);
						int nextindex = src.indexOf(value,nowIndex+key.length());
						if(nextindex!=-1){
							if(src.charAt(nextindex)=='\n')
							//防止对应的字符是\n
							//別把起始字符设为\n
								nodes.add(new wordIndex( nowIndex,nextindex+value.length()-1,(byte)8));
							else
								nodes.add(new wordIndex( nowIndex,nextindex+value.length(),(byte)8));
							nowIndex=nextindex+value.length()-1;
						}
						else{
							//如果找不到默认认为到达了末尾
							nodes.add(new wordIndex( nowIndex,src.length(),(byte)8));
							nowIndex=src.length()-1;
						}
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}
					
					return -1;
				}
			};
		}	
		public DoAnyThing getGoTo_Str(){
			//获取字符串
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
				   if(src.charAt(nowIndex)=='"'){
						//如果它是一个"，一直找到对应的"
						int endIndex = src.indexOf('"',nowIndex+1);
						if(endIndex!=-1){
							nodes.add(new wordIndex(nowIndex,endIndex+1,(byte)1));
							nowIndex=endIndex;
						}
						else{
							//如果找不到默认认为到达了末尾
							nodes.add(new wordIndex( nowIndex,src.length(),(byte)1));
							nowIndex=src.length()-1;
						}
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}		
					else if(src.charAt(nowIndex)=='\''){
						//如果它是'字符，将之后的字符加进来
						if(src.charAt(nowIndex+1)=='\\'){
							nodes.add(new wordIndex(nowIndex,nowIndex+4,(byte)1));
							nowIndex+=3;	
						}
						else{		
						    nodes.add(new wordIndex(nowIndex,nowIndex+3,(byte)1));
						    nowIndex+=2;  
						}
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}	
					
					return -1;
				}
			};
		}
		
		//不回溯的NoSans块，用已有信息完成判断
		public DoAnyThing getNoSans_Keyword(){
			//获取关键字和保留字
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					//找到一个单词 或者 未找到单词就遇到特殊字符，就把之前累计字符串清空

					if(WordLib.indexOf(nowWord.toString(),WordLib.keyword)!=-1&&!WordLib.IsAtoz(src.charAt(nowIndex+1))){
						//如果当前累计的字符串是一个关键字并且后面没有a～z这些字符，就把它加进nodes
						nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)3));
						nowWord.replace(0,nowWord.length(),"");
					    return nowIndex;
					}
					else if(WordLib.indexOf(nowWord.toString(),WordLib.constword)!=-1&&!WordLib.IsAtoz(src.charAt(nowIndex+1))){
						//否则如果当前累计的字符串是一个保留字并且后面没有a～z这些字符，就把它加进nodes
						nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)5));
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}		
				
					//关键字和保留字和变量不重复，所以只要中了其中一个，则就是那个
					//如果能进关键字和保留字和变量的if块，则说明当前字符一定不是特殊字符
					
					return -1;
				}	
			};
		}
		public DoAnyThing getNoSans_Char(){
			//获取字符
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					if(WordLib.indexOfNumber(src.charAt(nowIndex))&& !WordLib.IsAtoz(src.charAt(nowIndex-1))){
						//否则如果当前的字符是一个数字，就把它加进nodes
						//由于关键字和保留字一定没有数字，所以可以清空之前的字符串
						nodes.add(new wordIndex(nowIndex,nowIndex+1,(byte)4));
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}	
					else if(WordLib.indexOf(src.charAt(nowIndex),WordLib.fuhao)!=-1){	
						//否则如果它是一个特殊字符，就更不可能了，清空之前累计的字符串
						if(WordLib.indexOf(src.charAt(nowIndex),WordLib.spilt)==-1)
						//如果它不是会被html文本压缩的字符，将它自己加进nodes
						//这是为了保留换行空格等
							nodes.add(new wordIndex(nowIndex,nowIndex+1,(byte)7));

						nowWord.replace(0,nowWord.length(),"");
						//清空之前累计的字符串
						return nowIndex;
					}
					return -1;
				}
			};
		}
		public DoAnyThing getNoSans_Func(){
			//获取函数
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					
					if(WordLib2.lastFunc.contains(nowWord.toString())){
						//否则如果当前累计的字符串是一个函数并且后面是（ 字符，就把它加进nodes
						int afterIndex=tryAfterIndex(src,nowIndex+1);
						if(src.charAt(afterIndex)=='('){
						    nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)2));
						    nowWord.replace(0,nowWord.length(),"");
						    return afterIndex-1;
						}
					}
					return -1;
				}
			};
		}
		public DoAnyThing getNoSans_Villber(){
			//获得变量
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					if(WordLib2.historyVillber.contains(nowWord.toString())&&!WordLib.IsAtoz(src.charAt(nowIndex+1))){
						int afterIndex=tryAfterIndex(src,nowIndex+1);
						if(src.charAt(afterIndex)!='('){
						//否则如果当前累计的字符串是一个变量并且后面没有a～z和（ 这些字符，就把它加进nodes
						    nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)6));
						    nowWord.replace(0,nowWord.length(),"");
						    return afterIndex-1;
						}
					}
					return -1;
				}
			};
		}
		public DoAnyThing getNoSans_Type(){
			//获取类型
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					if(WordLib2.beforeType.contains(nowWord.toString())&&!WordLib.IsAtoz(src.charAt(nowIndex+1))){
						int afterIndex=tryAfterIndex(src,nowIndex+1);
						if(src.charAt(afterIndex)!='('){
						//否则如果当前累计的字符串是一个类型并且后面没有a～z和（ 这些字符，就把它加进nodes
						    nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)3));
						    nowWord.replace(0,nowWord.length(),"");
						    return afterIndex-1;
						}
					}
					return -1;
				}
			};
		}
		
		//会回溯的Sans块，试探并记录单词
		public DoAnyThing getSans_TryFunc(){
			//试探函数
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
					wordIndex node;
					if(src.charAt(nowIndex)=='('){
						//如果它是(字符，将之前的函数名存起来
						node=tryWord(src,nowIndex-1);
						WordLib2.lastFunc.add(src.substring(node.start,node.end));
						return nowIndex;
					}
						
					return -1;	
				}	
			};
		}
		public DoAnyThing getSans_TryVillber(){
			//试探变量和类型
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{	
				    
					wordIndex node;
					if((src.charAt(nowIndex)=='='||src.charAt(nowIndex)=='.')){
					//如果它是.或=字符，将之前的对象名或变量存起来	
						//=前后必须是分割符或普通的英文字符，不能是任何与=结合的算术字符
						node=tryWord(src,nowIndex-1);
						if(src.charAt(nowIndex)=='='&&!WordLib2.historyVillber.contains(src.substring(node.start,node.end))
						   &&WordLib.indexOf(src.charAt(nowIndex-1),arr)==-1
						   &&WordLib.indexOf(src.charAt(nowIndex+1),arr)==-1){
							//二次试探，得到类型
							//变量必须首次出现才有类型
							wordIndex tmp = tryWord(src,node.start-1);
							WordLib2.beforeType.add(src.substring(tmp.start,tmp.end));
						}
						WordLib2.historyVillber.add(src.substring(node.start,node.end));
						return nowIndex;
					}
					return -1;
			    }
			};
		}
		public DoAnyThing getSans_TryType(){
			//试探类型
			return new DoAnyThing(){
				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{	
					wordIndex node;
					if(src.charAt(nowIndex)=='>'){
						node = tryWord(src,nowIndex-1);
						while(src.charAt(node.start-1)=='<'){
							WordLib2.beforeType.add(src.substring(node.start,node.end));
							node=tryWord(src,node.start-1);
							WordLib2.beforeType.add(src.substring(node.start,node.end));
						}
					}
					return -1;
				}
			};
		}
		
		private char arr[]; 
		AnyThingForJava(){
			arr= new char[]{'!','~','+','-','*','/','%','^','|','&','<','>','='};
		    Arrays.sort(arr);
		}
		
	}
	
	public class wordIndex{
		//单个词的范围和颜料标签
		//对于查找词和替换，它们是要替换单个词的范围，以及要替换字符串重复的次数
		wordIndex(int start,int end,byte b){
			this.start=start;
			this.end=end;
			this.b=b;
		}

		@Override
		public boolean equals(Object other)
		{
			if(start==((wordIndex)other).start && end==((wordIndex)other).end )
				return true;
			return false;
		}
		
		public int start;
		public int end;
		public byte b;
	}
	public class wordIndexS extends wordIndex{
		//扩展了更强大的功能，例如现在你可以将<和>替换为&lt;和&gt;了
		//不过为了节约内存，不建议使用
		wordIndexS(int start,int end,byte b,String to){
			super(start,end,b);
			this.to=to;
		}
		public String to;
	}
	
	public void reSAll(int start,int end,String want,String to){
		IsModify=true;
		Editable editor = getText();
		String src=getText().toString().substring(start,end);
		int nowIndex = src.lastIndexOf(want);
		while (nowIndex != -1)
		{
			//从起始位置开始，反向把字符串中的want替换为to
			editor.replace(nowIndex+start, nowIndex+start + 1, to);	
			nowIndex=src.lastIndexOf(want,nowIndex-1);
		}
		IsModify=false;
	}
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
			
		if (lengthAfter != 0)
		{	//如果正被修改，不允许再次修改
		    if(IsModify){
			    return;
			}
			//试探起始行和之前之后的tryLines行，并染色
			wordIndex tmp=new wordIndex(0,0,(byte)0);
			tmp.start=tryLine_Start(text.toString(),start);
			tmp.end=tryLine_End(text.toString(),start+lengthAfter);
			for(int i=1;i<tryLines;i++){
				tmp.start=tryLine_Start(text.toString(),tmp.start-1);
				tmp.end=tryLine_End(text.toString(),tmp.end+1);
			}
			reDrawColor(tmp.start,tmp.end);
		}
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		
	}
	
}


class wordAndColor{
	//所有单词和颜色
	public String[] keyword = new String[]{
		"goto","const",
		"enum","assert",
		"package","import",
		"final","static","this","super",
		"try","catch","finally","throw","throws",
		"public","protected","private","friendly",
		"native","strictfp","synchronized","transient","volatile",
		"class","interface","abstract","implements","extends","new",
		"byte","char","boolean","short","int","float","long","double","void","String",
		"if","else","while","do","for","switch","case","default","break","continue","return","instanceof",
		"Override","Deprecated","SuppressWarnings","SafeVarargs","FunctionalInterface","param",
	};

	public String[] constword = new String[]{"null","true","false"};
	public char[] fuhao= new char[]{
		'(',')','{','}','[',']',
		'=',';',',','.',':',
		'+','-','*','/','%',
		'^','|','&','<','>','?','@',
		'!','~','\'','\n',' ','\t'
	};
	public char[] spilt= new char[]{
        '\n',' ','\t','<','>',
	};
	
	public HashMap<String,String> zhu_key_value = new HashMap<String,String>();
	
	public String Str="#98c379";//青草奶油
	public String Villber ="#ff9090";//橙红柚子
	public String KeyWord="#cc80a9";//桃红乌龙
	public String Function ="#61afd8";//齐马蓝
	public String Number ="#cd9861";//枯叶黄
	public String FuHao ="#99c8ea";//淡湖蓝
	public String Object ="#d4b876";//金币
	public String zhuShi ="#585f65";//深灰
	public String Default ="#abb2bf";//灰白
	public String yes="#de6868";//枣

	wordAndColor(){
		sort();
		zhu_key_value.put("//","\n");
		zhu_key_value.put("/*","*/");
		zhu_key_value.put("<!--","-->");
	}

	public void sort(){
		Arrays.sort(fuhao);
		Arrays.sort(spilt);
		Arrays.sort(keyword);
		Arrays.sort(constword);
	}
	
	public String fromByteToColor(byte b){
		switch(b){
			case 0: return Default;
			case 1: return Str;
			case 2: return FuHao;
			case 3: return KeyWord;
			case 4: return Villber;
			case 5: return Number;
			case 6: return Villber;
			case 7: return FuHao;
			case 8: return zhuShi;
		}
		return null;
	}
	
	public void delSame(Collection<String> dst,Collection<String> src){
		//删除dst中与src中相同的元素
		for(Object o: dst.toArray()){
			if(src.contains((String)o))
				dst.remove(o);
		}
	}
	public void delSame(Collection<String> dst,String[] src){
		for(Object o: dst.toArray()){
			if(indexOf((String)o,src)!=-1)
				dst.remove(o);
		}
	}
	
	public void delNumber(Collection<String> dst){
		for(Object o: dst.toArray()){
			if(indexOfNumber((String)o)){
				dst.remove(o);
			}
		}
	}
	
//无聊的函数就让我写了吧
	
	public boolean IsAtoz(char ch){
		if(ch>='a' && ch <='z')
			return true;
		else if(ch>='A' && ch <='Z')
			return true;
		return false;
	}
	
	public boolean indexOfNumber(char ch){
		if(ch>='0'&&ch<='9')
			return true;
		return false;
	}
	
	public boolean indexOfNumber(String src){
		int i;
	    for(i=0;i<src.length();i++){
			if(!indexOfNumber(src.charAt(i)))
				return false;
		}
		return true;
	}
	
	public int calaN(String src,int index){
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
	

	public int indexOf(char ch,char[]fuhao){
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

	public int indexOf(String str,String[] keyword) {	
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
	
    public ArrayList<String> indexsOf(String str,String[] keyword) {	
	//查找数组中所有出现了str的元素
		if(str.length()==0 || keyword==null||keyword.length==0)
			return null;
	    ArrayList<String> words = new ArrayList<String>();
		for(String word:keyword){
			if(word.toLowerCase().indexOf(str.toLowerCase())==0){
				words.add(word);
			}
		}
		return words;
	}
	public ArrayList<String> indexsOf(String str,Collection<String> keyword) {	
	//查找集合中所有出现了str的元素
		if(str.length()==0 || keyword==null||keyword.size()==0)
			return null;
	    ArrayList<String> words = new ArrayList<String>();
		for(String word:keyword){
			if(word.toLowerCase().indexOf(str.toLowerCase())==0){
				words.add(word);
			}
		}
		return words;
	}
	
	public ArrayList<Integer> indexsOf(String str,String text){
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
	
//独用函数
	public String indexOfKey(String str,int nowIndex){
		for(String key: zhu_key_value.keySet()){
		    if(str.indexOf(key,nowIndex)==nowIndex)
				return key;
		}
		return null;
	}
	
	public String getNStr(String src,int n){
		StringBuffer arr= new StringBuffer();
		while(n-- !=0){
			arr.append(src);
		}
		return arr.toString();
	}
	
}

class OtherWords{
	public TreeSet<String> historyVillber;
	public TreeSet<String> beforeType;
	public TreeSet<String> lastFunc;
	public TreeSet<String> Tag;
	public TreeSet<String> Attribut;
	
	OtherWords(){
	 historyVillber = new TreeSet<>();
	 beforeType= new TreeSet<>();
	 lastFunc= new TreeSet<>();
	 Tag= new TreeSet<>();
	 Attribut= new TreeSet<>();
	
	}
}
