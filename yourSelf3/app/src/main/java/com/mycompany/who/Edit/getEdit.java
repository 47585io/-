package com.mycompany.who.Edit;

import android.content.*;
import android.widget.*;

public class getEdit
{
	public static CodeEdit getE(Context cont, EditDate stack,EditText lines){
		return new CodeEdit(cont,stack,lines);
	}
	public static EditDate getDate(){
		return new EditDate();
	}
}
