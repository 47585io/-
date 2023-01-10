package com.mycompany.who.Share;

public class Share
{
	public static int setbitTo_1(int src,byte index){
		int a=1<<(index-1);
		return src|a;
	}
	public static int setbitTo_0(int src,byte index){
		int a=0xffffffff,b=0xffffffff,c;
		//将a左移index位，此时index之前都是1
		//将b右移32-index+1位，此时index之后都是1
		a=a<<(index);
		b=b>>(32-index+1);
		//将它们相或，空出一个index位
		c=a|b;
		//最后让src相与c，则index位必然为0
		return src&c;
	}
	public static int getbit(int src,int index){
		int a=src>>(index-1);
		return a&1;
	}
}
