package com.mycompany.who.Edit;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.lang.reflect.*;

public class CodeEdit extends FormatEdit
{
	public float size=20; 
	private EditText lines;
	public EditDate stack;
	private int lineCount;
	
	CodeEdit(Context cont,EditDate stack,EditText lines){
		super(cont);
		this.stack=stack;
		this.lines=lines;
		lines.setTextColor(0xff585f65);
		lines.setTypeface(Typeface.MONOSPACE);
		lines.setTextSize(20);
		lines.setLineSpacing(1.5f,1.5f);
	}
	
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
				//lines.setTextSize(size += 0.4);
			}
			else{
				setTextSize(size -= 0.4);
				//lines.setTextSize(size -= 0.4);
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
	
}
