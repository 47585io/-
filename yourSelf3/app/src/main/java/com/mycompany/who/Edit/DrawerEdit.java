package com.mycompany.who.Edit;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.lang.reflect.*;
import java.util.*;
import com.mycompany.who.Share.*;


public class DrawerEdit extends EditText
{
	
	//一百行代码实现代码染色
	public static wordAndColor WordLib = new wordAndColor();
	public OtherWords WordLib2;
	public boolean IsModify=false;
	//你应该在所有会修改文本的函数添加设置IsModify，并在ontextChange中适当判断，避免死循环
	public static boolean Enabled_Drawer=false;
	public static boolean Enabled_MakeHTML=false;
	public static int Drawer_Bit=0xffffffff;
	public static int Finder_Bit=0xffffffff;
	
	public int tryLines=3;
	
	DrawerEdit(Context cont){
		super(cont);
		WordLib2=new OtherWords();
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
		    editor.replace(nodes.get(i).start+start,nodes.get(i).end+start,String_Splitor. colorText(text.substring(nodes.get(i).start,nodes.get(i).end),WordLib.fromByteToColor(nodes.get(i).b)));
		IsModify=false;
	}
	public void DrawS(int start,int end,ArrayList<wordIndexS> nodes){
		//兼容wordIndexS的重载
		IsModify=true;
		int i;
		Editable editor=getText();
		for(i=nodes.size()-1;i>-1;i--)
		    editor.replace(nodes.get(i).start+start,nodes.get(i).end+start,String_Splitor.  colorText(nodes.get(i).to,WordLib.fromByteToColor(nodes.get(i).b)));
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
				arr.append(String_Splitor.  textColor(text.substring(index,node.start),WordLib.Default));
			arr.append(String_Splitor.  textColor(text.substring(node.start,node.end),WordLib.fromByteToColor(node.b)));
			index=node.end;
		}
		if(index<text.length())
			arr.append(String_Splitor.  textColor(text.substring(index,text.length()),WordLib.Default));
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
	    String HTML = null;
		if(text==null)
			return null;
			
		try{
			olny_FindJava(text);
			HTML= olny_DrawJava(start,end,text);
		}catch(Exception e){}
		
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
		if(Share.getbit(Drawer_Bit,0)==1)
		    totalList.add(AllThings.getGoTo_zhuShi());	
		if(Share.getbit(Drawer_Bit,1)==1)
		    totalList.add(AllThings.getGoTo_Str());
		if(Share.getbit(Drawer_Bit,6)==1)
		    totalList.add(new AnyThingForXML().getDraw_Tag());
		if(Share.getbit(Drawer_Bit,7)==1)
		    totalList.add(new AnyThingForXML().getDraw_Attribute());
		totalList.add(AllThings.getNoSans_Char());
		
		nodes = startFind(text,totalList);
		clearRepeatNode(nodes);	
		//用已有信息查找一次，并清理重复的node
		Draw(start,end,nodes);
		
		if(Enabled_MakeHTML){
			HTML= getHTML(nodes,text);
		}
		}catch(Exception e){}
		
		totalList.clear();
		//清除上次的任务
		return HTML;
	}
	
	public void olny_FindJava(String text){
		ArrayList<DoAnyThing> totalList = new ArrayList<DoAnyThing>();
		AnyThingForJava AllThings = new AnyThingForJava();
		
		if(Share.getbit(Finder_Bit,3)==1)
		    totalList.add(AllThings.getSans_TryFunc());	
		if(Share.getbit(Finder_Bit,4)==1)
		    totalList.add(AllThings.getSans_TryVillber());
		if(Share.getbit(Finder_Bit,5)==1)
		    totalList.add(AllThings.getSans_TryType());
		totalList.add(AllThings.getNoSans_Char());
		//请您在任何时候都加入getChar，因为它可以适时切割单词
		
		startFind(text,totalList);
		//试探一次，得到信息

		Array_Splitor. delSame(WordLib2.lastFunc,WordLib.keyword);
		//函数名不可是关键字，但可以和变量或类型重名	
		Array_Splitor.delSame(WordLib2.beforeType,WordLib.keyword);
		//类型不可是关键字
		Array_Splitor.delSame(WordLib2.beforeType,WordLib2.historyVillber);
		//类型不可是变量，类型可以和函数重名
		Array_Splitor.delSame(WordLib2.beforeType,WordLib.constword);
		//类型不可是保留字
		Array_Splitor. delSame(WordLib2.historyVillber,WordLib.keyword);
		//变量不可是关键字
		Array_Splitor.delSame(WordLib2.historyVillber,WordLib.constword);
		//变量不可是保留字
		Array_Splitor.delNumber(WordLib2.beforeType);
		Array_Splitor.delNumber(WordLib2.historyVillber);
		Array_Splitor.delNumber(WordLib2.lastFunc);
		//去掉数字
	}
	
	public String olny_DrawJava(int start,int end,String text){
		ArrayList<DoAnyThing> totalList = new ArrayList<DoAnyThing>();
		AnyThingForJava AllThings = new AnyThingForJava();
		ArrayList<wordIndex> nodes = null;
		String HTML=null;
		
		if(Share.getbit(Drawer_Bit,0)==1)
		    totalList.add(AllThings.getGoTo_zhuShi());
		if(Share.getbit(Drawer_Bit,1)==1)
		    totalList.add(AllThings.getGoTo_Str());
		if(Share.getbit(Drawer_Bit,2)==1)	
		    totalList.add(AllThings.getNoSans_Keyword());
		if(Share.getbit(Drawer_Bit,3)==1)	
		    totalList.add(AllThings.getNoSans_Func());
		if(Share.getbit(Drawer_Bit,4)==1)
		    totalList.add(AllThings.getNoSans_Villber());
		if(Share.getbit(Drawer_Bit,5)==1)
		    totalList.add(AllThings.getNoSans_Type());
		totalList.add(AllThings.getNoSans_Char());
		//请您在任何时候都加入getChar，因为它可以适时切割单词
		
		nodes = startFind(text,totalList);
		clearRepeatNode(nodes);	
		//用已有信息查找一次，并清理重复的node
		Draw(start,end,nodes);

		if(Enabled_MakeHTML){
			HTML= getHTML(nodes,text);
		}
		
		return HTML;
		
	}

	
	public wordIndex tryWord(String src,int index){
		//试探前面的单词
		wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			while(Array_Splitor. indexOf(src.charAt(index),WordLib.fuhao)!=-1)
				index--;
			tmp.end=index+1;
			while(Array_Splitor.indexOf(src.charAt(index),WordLib.fuhao)==-1)
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
			while(Array_Splitor.indexOf(src.charAt(index),WordLib.fuhao)!=-1)
				index++;
			tmp.start=index;
			while(Array_Splitor.indexOf(src.charAt(index),WordLib.fuhao)==-1)
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
			&&Array_Splitor.indexOf(src.charAt(index),WordLib.spilt)!=-1){
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
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

					if(Array_Splitor.indexOf(nowWord.toString(),WordLib.keyword)!=-1&&!String_Splitor. IsAtoz(src.charAt(nowIndex+1))){
						//如果当前累计的字符串是一个关键字并且后面没有a～z这些字符，就把它加进nodes
						nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)3));
						nowWord.replace(0,nowWord.length(),"");
					    return nowIndex;
					}
					else if(Array_Splitor.indexOf(nowWord.toString(),WordLib.constword)!=-1&&!String_Splitor. IsAtoz(src.charAt(nowIndex+1))){
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
					if(String_Splitor. indexOfNumber(src.charAt(nowIndex))&& !String_Splitor. IsAtoz(src.charAt(nowIndex-1))){
						//否则如果当前的字符是一个数字，就把它加进nodes
						//由于关键字和保留字一定没有数字，所以可以清空之前的字符串
						nodes.add(new wordIndex(nowIndex,nowIndex+1,(byte)4));
						nowWord.replace(0,nowWord.length(),"");
						return nowIndex;
					}	
					else if(Array_Splitor.indexOf(src.charAt(nowIndex),WordLib.fuhao)!=-1){	
						//否则如果它是一个特殊字符，就更不可能了，清空之前累计的字符串
						if(Array_Splitor.indexOf(src.charAt(nowIndex),WordLib.spilt)==-1)
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
					if(WordLib2.historyVillber.contains(nowWord.toString())&&!String_Splitor. IsAtoz(src.charAt(nowIndex+1))){
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
					if(WordLib2.beforeType.contains(nowWord.toString())&&!String_Splitor. IsAtoz(src.charAt(nowIndex+1))){
						int afterIndex=tryAfterIndex(src,nowIndex+1);
						if(src.charAt(afterIndex)!='('){
						//否则如果当前累计的字符串是一个类型并且后面没有a～z和（ 这些字符，就把它加进nodes
						    wordIndex tmp=tryWord(src,nowIndex-nowWord.length());
							String is=src.substring(tmp.start,tmp.end);
							if(!(is.equals("class")||is.equals("extends")||is.equals("implement")||is.equals("interface"))){
						    nodes.add(new wordIndex(nowIndex-nowWord.length()+1,nowIndex+1,(byte)3));
						    nowWord.replace(0,nowWord.length(),"");
						    return afterIndex-1;
							}
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
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
						   &&Array_Splitor.indexOf(src.charAt(nowIndex-1),arr)==-1
						   &&Array_Splitor.indexOf(src.charAt(nowIndex+1),arr)==-1){
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
				    int index = Array_Splitor.indexOf(nowWord.toString(),WordLib.keyword);
					if(index!=-1&&!String_Splitor.IsAtoz(src.charAt(nowIndex+1))){
					    if(WordLib.keyword[index]=="class"
						||WordLib.keyword[index]=="new"
					    ||WordLib.keyword[index]=="extends"
						||WordLib.keyword[index]=="implements"
						||WordLib.keyword[index]=="interface"){
								wordIndex tmp=tryWordAfter(src,nowIndex+1);
								WordLib2.beforeType.add(src.substring(tmp.start,tmp.end));
								return nowIndex;
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
	
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
			
		if (lengthAfter != 0)
		{	//如果正被修改，不允许再次修改
		    if(IsModify){
			    return;
			}
			//是否启用自动染色
			if(!Enabled_Drawer)
				return;
				
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
		"byte","char","boolean","short","int","float","long","double","void",
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
	
	
//无聊的函数就让我写了吧

//独用函数
	public String indexOfKey(String str,int nowIndex){
		for(String key: zhu_key_value.keySet()){
		    if(str.indexOf(key,nowIndex)==nowIndex)
				return key;
		}
		return null;
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


