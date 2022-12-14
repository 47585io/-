package com.mycompany.who.Edit;

import java.util.*;

public class EditDate
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


