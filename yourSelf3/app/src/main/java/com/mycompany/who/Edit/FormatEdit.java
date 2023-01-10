package com.mycompany.who.Edit;
import android.content.*;
import android.text.*;
import android.widget.*;
import com.mycompany.who.Share.*;
import java.util.*;
import com.mycompany.who.Edit.DrawerEdit.*;

public class FormatEdit extends CompleteEdit
{
	public static boolean Enabled_Format=false;
	
	FormatEdit(Context cont){
		super(cont);
	}
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
		/*
		 if(lengthAfter!=0){		
		 String src=text.toString().substring(start,start+lengthAfter);
		 int nowIndex=src.indexOf('\n');
		 while(nowIndex!=-1){
		 lines.getText().append("\n"+lineCount);
		 lineCount++;
		 nowIndex=src.indexOf('\n',nowIndex);
		 }
		 }
		 */
		if(lengthAfter==0)
            return;
		if(IsModify){
		    return;
		}
		//如果正被修改，则不允许修改
		reSAll(start,start+lengthAfter,"\t","    ");	
		if(text.toString().indexOf('\n',start)!=-1&&lengthAfter<6&&lengthBefore==0){
			if(Enabled_Format){
				Insert(start);
			    Format(start,start+lengthAfter);	
			}
		}
		super.onTextChanged(text, start, lengthBefore, lengthAfter);

	}
	
	public void startFormat(int start,int end,ArrayList<Formator> totalList){
		Stack<Integer> bindow;
		IsModify=true;
		for(Formator total:totalList){
			bindow=new Stack<Integer>();
			int beforeIndex = 0;
			int nowIndex=start;
			try{
				nowIndex= total.dothing_Start(getText(),nowIndex);
	            for(;nowIndex<end&&nowIndex!=-1;){
					beforeIndex=nowIndex;
			        nowIndex= total.dothing_Run(getText(),nowIndex,bindow);
		        }
				nowIndex= total.dothing_End(getText(),beforeIndex,bindow);
		  	}catch(Exception e){}
		}
		IsModify=false;
	}

	public void Format(int start,int end){
		//立即进行一次默认的Format
		ArrayList<Formator> totalList=new ArrayList<Formator>();
		totalList.add(new AnyThingFormat().getFormat_N());	
		try{
		    startFormat(start,end,totalList);
		}catch(Exception e){}
	}

	abstract class Formator extends DoAnyThing{
		abstract int dothing_Run(Editable editor,int nowIndex,Stack<Integer> arr);
		//开始做事
		abstract int dothing_Start(Editable editor,int nowIndex);
		//为了避免繁琐的判断，一开始就调用start方法，将事情初始化为你想要的样子
		abstract int dothing_End(Editable editor,int beforeIndex,Stack<Integer> arr);
		//收尾工作
	}
	class AnyThingFormat{
		public Formator getFormat_N(){
			return new Formator(){

				@Override
				int dothing_Run(Editable editor, int nowIndex,Stack<Integer> bindow)
				{
					String src= editor.toString();
					int nextIndex= src.indexOf('\n',nowIndex+1);
					//从上次的\n接着往后找一个\n

					//如果到了另一个代码块，不直接缩进
					int start_bindow = src.indexOf('{',nowIndex+1);
					int end_bindow=src.indexOf('}',nowIndex+1);
					int close_tag=src.indexOf("</",nextIndex+1);

					if(nowIndex==-1||nextIndex==-1)
						return -1;

					int nowCount,nextCount;
				    nowCount=String_Splitor. calaN(src,nowIndex+1);
					nextCount=String_Splitor. calaN(src,nextIndex+1);
					//统计\n之后的分隔符数量

					if(start_bindow<nextIndex&&start_bindow!=-1)
					    bindow.push(start_bindow);

					if(end_bindow<nextIndex&&end_bindow!=-1){
						//如果当前的nextindex出了代码块，将}设为前面的代码块中与{相同位置
						int tail_bindow=nowIndex;
						int head_bindow;
						int Count;
						//有{的情况下
						if(bindow.size()>0){
						    tail_bindow=bindow.pop();
						    head_bindow =src.lastIndexOf('\n',tail_bindow-1);
						    Count= String_Splitor. calaN(src,head_bindow+1);

						    if(end_bindow-nowIndex-1==Count+(nowCount-Count)){
								//如果括号当前位置是在减去上次附加的4个空格后，缩进刚好满足预期的，则还原位置
								//因为在上次换行时}暂时默认与上行缩进保持一致，所以}一定 >= 上行缩进		
							    editor.delete(nowIndex+1,nowIndex+(nowCount-Count)+1);
								return nextIndex-(nowCount-Count);
						    }

						}
						//又因为每次会对齐下行
						//所以如果不满足预期的空格，则}前面没有对齐

						else if(nowCount-4>=nextCount){
							//如果无法决定，丢弃}，直接开下一行
							editor.insert(nextIndex+1,String_Splitor. getNStr(" ",nowCount-nextCount-4));
							return nextIndex;
						}	
					}

					if(close_tag!=-1){
						//不对xml进行格式化
						int nextnext=src.indexOf('\n',nextIndex+1);
						if(nextnext!=-1){
						    nextnext= src.indexOf('\n', nextnext+1);
						    if(close_tag<nextnext&&nextnext!=-1){
								if(nowCount>nextCount){
									editor.insert(nextIndex+1,String_Splitor. getNStr(" ",nowCount-nextCount));
									return nextnext+(nowCount-nextCount);
								}
							    return nextnext;
						    }
						}
					}

					//如果下个的分隔符数量小于当前的，缩进至与当前的相同的位置
					if(nowCount>=nextCount){
						if(start_bindow<nextIndex&&start_bindow!=-1){
							//如果它是{之内的，并且缩进位置小于{，则将其缩进至{内
							editor.insert(nextIndex+1,String_Splitor. getNStr(" ",nowCount-nextCount+4));
							return nextIndex;
						}
						editor.insert(nextIndex+1,String_Splitor. getNStr(" ",nowCount-nextCount));
						return nextIndex;
					}

					return nextIndex;
					//下次从这个\n开始

				}

				@Override
				int dothing_Start(Editable editor, int nowIndex)
				{
					String src= editor.toString();
					nowIndex=src.lastIndexOf('\n',nowIndex-1);
					if(nowIndex == -1)
						nowIndex=src.indexOf('\n');
					return nowIndex;
					//返回now之前的\n
				}

				@Override
				int dothing_End(Editable editor, int beforeIndex,Stack<Integer> arr)
				{/*
					 String src= editor.toString();
					 editor.insert(src.length(),"\n");
					 dothing_Run(getText(),beforeIndex,arr);*/
					return -1;

				}

				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<wordIndex> nodes)
				{
					return -1;
				}

			};
		}
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
	
	
	abstract class Insertor extends DoAnyThing{
		abstract public int dothing_insert(Editable editor,int nowIndex);
	}
	public class AnyThingForInsert{
		public char insertarr[];
		public AnyThingForInsert(){
			insertarr=new char[]{'{','(','[','\'','"'};
			Arrays.sort(insertarr);
		}
		public Insertor When_hasChar(){
			return new Insertor(){

				@Override
				public int dothing_insert(Editable editor,int nowIndex)
				{
					String src=editor.toString();
					int charIndex=Array_Splitor.indexOf(src.charAt(nowIndex),insertarr);
					if(charIndex!=-1){
						switch(charIndex){
							case 0:
								editor.insert(nowIndex+1,"}");
								break;
							case 1:
								editor.insert(nowIndex+1,")");
								break;
							case 2:
								editor.insert(nowIndex+1,"]");
								break;
							case 3:
								editor.insert(nowIndex+1,"'");
								break;
							case 4:
								editor.insert(nowIndex+1,"\"");
								break;
						}
					}
						
					return nowIndex;
				}

				@Override
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<DrawerEdit.wordIndex> nodes)
				{
					return 0;
				}
			};
		}
	}
	
	public void startInsert(int index,ArrayList<Insertor> totalList){
		IsModify=true;
		for(Insertor total:totalList){
			total.dothing_insert(getText(),index);
		}
		IsModify=false;
	}	
	public void Insert(int index){
		ArrayList<Insertor> totalList=new ArrayList<Insertor>();
		totalList.add(new AnyThingForInsert().When_hasChar());
		startInsert(index,totalList);
	}
	
}
