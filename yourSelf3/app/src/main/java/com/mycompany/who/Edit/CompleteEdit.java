package com.mycompany.who.Edit;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.mycompany.who.*;
import com.mycompany.who.Share.*;
import java.lang.reflect.*;
import java.util.*;


public class CompleteEdit extends DrawerEdit
{
	
	public static boolean Enabled_Complete=false;
	public static int Search_Bit=0xffffffff;
	
	CompleteEdit(Context cont){
		super(cont);	
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd)
	{}

	public int openWindow(Context cont,ViewGroup Window,int index){
		if(!Enabled_Complete)
			return 0;
		int hasWord=0;
		String wantBefore= getWord(index);
		String wantAfter = getAfterWord(index);
		//获得光标前后的单词，并开始查找
	    hasWord= SearchWord(cont,Window,wantBefore,wantAfter,0,wantBefore.length(),index);
		return hasWord;
	}


	public int SearchWord(Context cont,ViewGroup Window, String wantBefore,String wantAfter,int before,int after,int index){
		//从单词库中搜索单词
		int hasWord=0;

		ArrayList<String> words;
		if(Share.getbit(Search_Bit,1)==1){
	        words = SearchOnce(wantBefore,wantAfter,WordLib.keyword,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,1);
		}
		if(Share.getbit(Search_Bit,2)==1){		
	        words = SearchOnce(wantBefore,wantAfter,WordLib.constword,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,99);
		}
		if(Share.getbit(Search_Bit,4)==1){	
		    words =SearchOnce(wantBefore,wantAfter,WordLib2.historyVillber,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,2);
		}
		if(Share.getbit(Search_Bit,3)==1){
		    words = SearchOnce(wantBefore,wantAfter,WordLib2.lastFunc,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,3);
		}
		if(Share.getbit(Search_Bit,5)==1){
	   
		    words = SearchOnce(wantBefore,wantAfter,WordLib2.beforeType,before,after);
	    	hasWord+= addSomeWord(cont,words,Window,index,4);
		}
		if(Share.getbit(Search_Bit,6)==1){	
		    words = SearchOnce(wantBefore,wantAfter,WordLib2.Tag,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,5);
		}
		if(Share.getbit(Search_Bit,7)==1){
		    words = SearchOnce(wantBefore,wantAfter,WordLib2.Attribut,before,after);
		    hasWord+= addSomeWord(cont,words,Window,index,99);
		}
		return hasWord;
	}

	public ArrayList<String> SearchOnce(String wantBefore,String wantAfter,String[] target,int before,int after){
		ArrayList<String> words=null;
		Idea ino = Array_Splitor.getNo();
		Idea iyes = Array_Splitor.getyes();
		if(!wantBefore.equals(""))
		//如果前字符串不为空，则搜索
		    words=Array_Splitor.indexsOf(wantBefore,target,before,ino);
		if(!wantAfter.equals("")&&words!=null)
		//如果前字符串搜索结果不为空并且后字符串不为空，就从之前的搜索结果中再次搜索
		    words=Array_Splitor.indexsOf(wantAfter,words,after,iyes);
		else if(!wantAfter.equals("")&&words==null){
			//如果前字符串为空，但后字符串不为空，则只从后字符串开始搜索
			words=Array_Splitor.indexsOf(wantAfter,target,after,iyes);
		}
		return words;
	}

	public ArrayList<String> SearchOnce(String wantBefore,String wantAfter,Collection<String> target,int before,int after){
		//同上
		ArrayList<String> words=null;
		Idea ino = Array_Splitor.getNo();
		Idea iyes = Array_Splitor.getyes();
		if(!wantBefore.equals(""))
		    words=Array_Splitor.indexsOf(wantBefore,target,before,ino);
		if(!wantAfter.equals("")&&words!=null)
		    words=Array_Splitor.indexsOf(wantAfter,words,after,iyes);
		else if(!wantAfter.equals("")&&words==null){
			words=Array_Splitor.indexsOf(wantAfter,target,after,iyes);
		}
		return words;
	}

	public int addSomeWord(Context cont,ArrayList<String> words, ViewGroup Window,int index, int flag){
		//排序并添加一组的单词块
		if(words==null||words.size()==0)
			return 0;
		Array_Splitor.sort(words);
		Array_Splitor.sort2(words);
		for(String word: words){
		    ViewGroup token =  creatButton(cont,word,Window,index, flag);
			Window.addView(token);
		}
		return 1;
	}


	public ViewGroup creatButton(Context cont,final String word,final ViewGroup Window,final int index, final int flag){
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
					try{
						wordIndex tmp = tryWordSplit(getText().toString(),index);
						wordIndex tmp2 = tryWordSplitAfter(getText().toString(),index);
						getText().replace(tmp.start,tmp2.end,word);
						setSelection(tmp.start+word.length());
						//把光标移动到最后
						if(flag==3)
							getText().insert(getSelectionStart(),"(");
						if(flag==5){
							if(getText().toString().charAt(tmp.start)!='<')
								getText().insert(tmp.start,"<");
						}//函数和标签额外插入字符
					}catch(Exception e){}

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



	public wordIndex tryWordSplit(String src,int nowIndex){
		//试探纯单词
		int index=nowIndex-1;
	    wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			while(index>-1&&Array_Splitor.indexOf(src.charAt(index),WordLib.fuhao)==-1)
				index--;
			tmp.start=index+1;
			tmp.end=nowIndex;
		}catch(Exception e){
			return new wordIndex(0,0,(byte)0);
		}
		return tmp;
	}
	public wordIndex tryWordSplitAfter(String src,int index){
		//试探纯单词
	    wordIndex tmp = new wordIndex(0,0,(byte)0);
		try{
			tmp.start=index;
			while(index<src.length()&&Array_Splitor.indexOf(src.charAt(index),WordLib.fuhao)==-1)
				index++;
			tmp.end=index;
		}catch(Exception e){
			return new wordIndex(0,0,(byte)0);
		}
		return tmp;
	}

	public String getWord(int offset){
		//获得光标前的纯单词
	    wordIndex node = tryWordSplit(getText().toString(),offset);
		if(node.end==0)
			node.end=offset;
		String want= getText().toString().substring(node.start,node.end);

		return want;
	}
	public String getAfterWord(int offset){
		//获得光标后面的纯单词
		wordIndex node = tryWordSplitAfter(getText().toString(),offset);
		if(node.end==0)
			node.end=getText().toString().length();
		String want= getText().toString().substring(node.start,node.end);

		return want;
	}
	
}





