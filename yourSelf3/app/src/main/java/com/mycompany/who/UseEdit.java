package com.mycompany.who;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.lang.reflect.*;
import java.util.*;
import com.mycompany.who.CodeEdit.*;

public class UseEdit extends CodeEdit
{

	public float size=20; 
	private EditText lines;
	public EditDate stack;
	private int lineCount;
	
	UseEdit(Context cont, boolean Enabled,EditDate stack,EditText lines)
	{
		super(cont, Enabled);
		this.stack=stack;
		this.lines=lines;
		
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd)
	{
		
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
		
		if(IsModify){
		    
		    return;
		
		}
		//如果正被修改，则不允许修改
		reSAll(start,start+lengthAfter,"\t","    ");	
		if(text.toString().indexOf('\n',start)!=-1&&lengthAfter<6&&lengthBefore==0){
			Format(start,start+lengthAfter);
		}
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		
	}


	public String getWord(int offset){
		//获得光标处的纯单词
		if(getText().toString().length()>getSelectionStart()){
			if(WordLib.IsAtoz( getText().toString().charAt(getSelectionStart()))){
				return null;
			}
		}
	    wordIndex node = tryWordSplit(getText().toString(),offset-1);
		if(node.end==(byte)0)
			node.end=offset;
		String want= getText().toString().substring(node.start,node.end);
		
		return want;
	}
	public int SearchWord(Context cont,ViewGroup Window, String want,int flag){
		//从单词库中搜索单词
		int hasWord=0;
		ArrayList<String> words = WordLib.indexsOf(want,WordLib.keyword);
		hasWord+= addSomeWord(cont,words,Window,1);
	    words = WordLib.indexsOf(want,WordLib.constword);
		hasWord+= addSomeWord(cont,words,Window,99);
		words = WordLib.indexsOf(want,WordLib2.historyVillber);
		hasWord+= addSomeWord(cont,words,Window,2);
		words = WordLib.indexsOf(want,WordLib2.lastFunc);
		hasWord+= addSomeWord(cont,words,Window,3);
		words = WordLib.indexsOf(want,WordLib2.beforeType);
		hasWord+= addSomeWord(cont,words,Window,4);
		words = WordLib.indexsOf(want,WordLib2.Tag);
		hasWord+= addSomeWord(cont,words,Window,5);
		words = WordLib.indexsOf(want,WordLib2.Attribut);
		hasWord+= addSomeWord(cont,words,Window,99);
		return hasWord;
	}
	public int addSomeWord(Context cont,ArrayList<String> words, ViewGroup Window, int flag){
		//排序并添加一组的单词块
		if(words==null||words.size()==0)
			return 0;
		sort(words);
		for(String word: words){
		    ViewGroup token =  creatButton(cont,word,Window, flag);
			Window.addView(token);
		}
		return 1;
	}
	ListView b;
	
	public ViewGroup creatButton(Context cont,final String word,final ViewGroup Window, final int flag){
		//创建一个单词块，
		//不到必要时刻，少用final(const)
		LinearLayout line=new LinearLayout(cont);
		ImageView pic=new ImageView(cont);
		switch(flag){
			//关键字，变量，函数，类型，标签，默认
			case 1:
				pic.setImageResource(R.drawable.image_1);	
				break;
			case 2:
				pic.setImageResource(R.drawable.image_2);
				break;
			case 3:
				pic.setImageResource(R.drawable.image_3);
				break;
			case 4:
				pic.setImageResource(R.drawable.image_4);
				break;
			case 5:
				pic.setImageResource(R.drawable.image_5);
				break;
			default:
			    pic.setImageResource(R.drawable.image_n);
				
		}
		line.addView(pic);
		pic.layout(pic.getLeft(),pic.getTop(),pic.getLeft()+50,pic.getTop()+35);
		line.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					//如果点击了就插入单词并关闭窗口
					wordIndex tmp = tryWordSplit(getText().toString(),getSelectionStart()-1);
					getText().replace(tmp.start,getSelectionStart(),word);
					if(flag==3)
						getText().insert(getSelectionStart(),"(");
					if(flag==5){
						if(getText().toString().charAt(tmp.start)!='<')
						    getText().insert(tmp.start,"<");
					}
					//函数和标签额外插入字符
					ViewGroup WindowFather=(ViewGroup) Window.getParent();
				    WindowFather.setX(-9999);
					WindowFather.setY(-9999);		
				}
			});
		
		TextView but = new TextView(cont);
		but.setText(word);
		but.setTextSize(22);
		line.addView(but);
		return line;
		
	}
	
	public void sort(ArrayList<String> words){
		//排序
		words.sort(new Comparator<String>(){
				@Override
				public int compare(String p1, String p2)
				{
					if(p1.charAt(0)==p2.charAt(0)){
						if(p1.length()>p2.length())
							return 1;
						else
							return -1;
					}
					return 0;
				}
			});
	}
	
	public wordIndex tryWordSplit(String src,int index){
		//试探纯单词
	    wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			while(WordLib.indexOf(src.charAt(index),WordLib.fuhao)==-1)
				index--;
			tmp.start=index+1;
			tmp.end=getSelectionStart();
		}catch(Exception e){
			return new wordIndex(0,0,(byte)0);
		}
		return tmp;
	}
	
	
//___________________________________________________________________________________________________________________________
	

	public static void setCursorDrawableColor(EditText editText, int color)
	{
        try
		{
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");//获取这个字段
            fCursorDrawableRes.setAccessible(true);//代表这个字段、方法等等可以被访问
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);

            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);

            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            drawables[0]=editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1]=editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);//SRC_IN 上下层都显示。下层居上显示。
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        }
		catch (Throwable ignored)
		{
        }
    }

	public static void closeInputor(Context context, View editText)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	public static void openInputor(Context context, View editText)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(editText, 0);
	}
	
	public wordIndex getCursorPos(int offset){
		//获取光标坐标
		int lines= getLayout().getLineForOffset(offset);
		Rect bounds = new Rect();
		//任何传参取值都必须new
		wordIndex pos = new wordIndex(0,0,(byte)0);
		getLineBounds(lines,bounds);
	    pos.start= (int)bounds.exactCenterX();
		pos.end= (int)bounds.exactCenterY();
	    return pos;
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent p2)
	{
		//拦截事件进行放大
		if (p2.getPointerCount() == 2 && p2.getHistorySize() != 0)
		{
			if (
			      (
			         Math.sqrt(
			         (
					    Math.pow(
			               Math.abs(p2.getX(0) - p2.getX(1)),2
						)
				       +
						Math.pow(
				           Math.abs(p2.getY(0) - p2.getY(1)),2
						)
					 )
			      )
			    >
				  (
				     Math.sqrt(
				        Math.pow(
				        Math.abs(p2.getHistoricalX(0, p2.getHistorySize() - 1) - p2.getHistoricalX(1, p2.getHistorySize() - 1)),2
					    )		
				       +
					    Math.pow( 
				        Math.abs(p2.getHistoricalY(0, p2.getHistorySize() - 1) - p2.getHistoricalY(1, p2.getHistorySize() - 1)),2)
						)
					 )
			      )
			   ){
			//如果两点之间的距离大于历史的两点间距离，就把字放大
				setTextSize(size += 0.4);
				}
			else{
				setTextSize(size -= 0.4);
				
			//如果手指向内缩，就把字缩小
			}
		}
		
        super.onTouchEvent(p2);
		return true;

	}
	
	public void Uedo()
	{
		IsModify=true;
		EditDate.Token token= stack.getLast();
		if (token != null)
		{
			if (token.src == ""){
				stack.Reput(token.start, token.start + 1, getText().subSequence(token.start, token.end).toString());
			//如果Uedo会将范围内字符串删除，则我要将其保存，待之后插入
				getText().replace(token.start, token.end, token.src);	
			}
			else{
				stack.Reput(token.start, token.start + token.src.length(), "");
			//如果Uedo会将在那里插入一个字符串，则我要将其下标保存，待之后删除
			    getText().insert(token.start,token.src);
			}
		}
		IsModify=false;
	}

	public void Redo()
	{
		IsModify=true;
		EditDate.Token token= stack.getNext();
		if (token != null)
		{
			if (token.src == ""){
				stack.put(token.start, token.start + 1, getText().subSequence(token.start, token.end).toString());
			//如果Redo会将范围内字符串删除，则我要将其保存，待之后插入
				getText().replace(token.start, token.end, token.src);
			}
			else{
				stack.put(token.start, token.start + token.src.length(), "");
			//如果Redo会将在那里插入一个字符串，则我要将其下标保存，待之后删除
			    getText().insert(token.start,token.src);
		    }
		}
		IsModify=false;
	}
	
	
//	___________________________________________________________________________________________________________________________
	
	public void startFormat(int start,int end,ArrayList<DoAnyThings> totalList){
		Stack<Integer> bindow;
		IsModify=true;
		for(DoAnyThings total:totalList){
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
		ArrayList<DoAnyThings> totalList=new ArrayList<DoAnyThings>();
		totalList.add(new AnyThingFormat().getFormat_N());	
		try{
		    startFormat(start,end,totalList);
		}catch(Exception e){}
	}

	abstract class DoAnyThings extends DoAnyThing{
		abstract int dothing_Run(Editable editor,int nowIndex,Stack<Integer> arr);
		//开始做事
		abstract int dothing_Start(Editable editor,int nowIndex);
		//为了避免繁琐的判断，一开始就调用start方法，将事情初始化为你想要的样子
		abstract int dothing_End(Editable editor,int beforeIndex,Stack<Integer> arr);
		//收尾工作
	}
	class AnyThingFormat{
		public DoAnyThings getFormat_N(){
			return new DoAnyThings(){

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
				    nowCount=WordLib.calaN(src,nowIndex+1);
					nextCount=WordLib.calaN(src,nextIndex+1);
					//统计\n之后的分隔符数量
					
					if(start_bindow<nextIndex&&start_bindow!=-1)
					    bindow.push(start_bindow);
						
					if(end_bindow<nextIndex&&end_bindow!=-1){
						//如果当前的nextindex出了代码块，将}设为前面的代码块中与{相同位置
						int tail_bindow=nowIndex;
						if(bindow.size()>0){
						    tail_bindow=bindow.pop();
						    int head_bindow=src.lastIndexOf('\n',tail_bindow-1);
						    int Count = WordLib.calaN(src,head_bindow+1);

						    if(end_bindow-nowIndex-1==Count+(nowCount-Count)){
							//如果括号当前位置是在减去上次附加的4个空格后，缩进刚好满足预期的，则还原位置
							    editor.delete(nowIndex+1,nowIndex+(nowCount-Count)+1);
							return nextIndex-(nowCount-Count);
						    }
						}
						//否则括号之前一定不是直接挨着\n和一些空格
						//又因为每次会对齐下行
						//所以如果不满足预期的空格，则{前面没有对齐
						if(nowCount-4>nextCount){
							editor.insert(nextIndex+1,WordLib.getNStr(" ",nowCount-nextCount-4));
						}
						return nextIndex;
					}
					
					if(close_tag!=-1){
						//不对xml进行格式化
						int nextnext=src.indexOf('\n',nextIndex+1);
						if(nextnext!=-1){
						    nextnext= src.indexOf('\n', nextnext+1);
						    if(close_tag<nextnext&&nextnext!=-1){
								if(nowCount>nextCount){
									editor.insert(nextIndex+1,WordLib.getNStr(" ",nowCount-nextCount));
									return nextnext+(nowCount-nextCount);
								}
							    return nextnext;
						    }
						}
					}
					
					//如果下个的分隔符数量小于当前的，缩进至与当前的相同的位置
					if(nowCount>nextCount){
						if(start_bindow<nextIndex&&start_bindow!=-1){
							//如果它是{之内的，并且缩进位置小于{，则将其缩进至{内
							editor.insert(nextIndex+1,WordLib.getNStr(" ",nowCount-nextCount+4));
							return nextIndex;
						}
						editor.insert(nextIndex+1,WordLib.getNStr(" ",nowCount-nextCount));
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
				int dothing(String src, StringBuffer nowWord, int nowIndex, ArrayList<CodeEdit.wordIndex> nodes)
				{
					return -1;
				}


			};
		}
	}
}


//	___________________________________________________________________________________________________________________________


class EditDate
{
	private Stack<Token> UedoList;
	private Stack<Token> RedoList;
	EditDate()
	{
		UedoList=new Stack<>();
		RedoList=new Stack<>();
	}

	public void put(int start, int end, String src)
	{
		UedoList.push(new Token(start, end, src));
	}

	public void Reput(int start, int end, String src)
	{
		RedoList.push(new Token(start, end, src));
	}

	public Token getLast()
	{	
	    if (UedoList.size() == 0)
			return null;
		return UedoList.pop();
	}
	public Token getNext()
	{
		if (RedoList.size() == 0)
			return null;
		return RedoList.pop();
	}

	public class Token
	{
		Token(int start, int end, String src)
		{
			this.start=start;
			this.end=end;
			this.src=src;
		}
		int start;
		int end;
		String src;
	}
}


