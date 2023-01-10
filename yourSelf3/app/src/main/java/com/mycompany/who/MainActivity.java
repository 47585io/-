package com.mycompany.who;

import android.app.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mycompany.who.Edit.*;
import com.mycompany.who.Share.*;
import java.util.*;

public class MainActivity extends Activity
{
//抱歉啊，主界面就是编辑器

	private RelativeLayout re;
	private RelativeLayout ForEdit;
	//private EditText Lines;
	private ScrollView WindowFather;
	private LinearLayout mWindow;
	
	private myLog log;
	private FileList files;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								 WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.edit);	
		initActivity();	
		ForEdit();	
	    CodeEdit.Enabled_Format=true;
		CodeEdit.Enabled_Drawer=true;
		CodeEdit.Enabled_Complete=true;
		CodeEdit.Enabled_MakeHTML=true;
    }
	
	private void initActivity()
	{
		re=findViewById(R.id.editRelativeLayout);
		ForEdit=findViewById(R.id.editLinearLayout);
		//Lines=findViewById(R.id.EditText);
		WindowFather=findViewById(R.id.WindowFather);
		mWindow=findViewById(R.id.Window);
		log=new myLog("/storage/emulated/0/Linux/share.html");	
		files=new FileList();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(1, 1, 1, "ReDraw Java");
		menu.add(2, 2, 2, "ReDraw XML");
		menu.add(3, 3, 3, "Uedo");
		menu.add(4, 4, 4, "Redo");
		menu.add(5, 5, 5, "Format");
		menu.add(6,6,6,"Setting");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		CodeEdit Edit=files.getEdit(files.getNowIndex());
		
		String html;
		try{
		switch(item.getItemId()){
		case 1:
			html= Edit.reDrawColor(Edit.getSelectionStart(), Edit.getSelectionEnd());
		    if (html != null)
			    log.e(html, true);
			break;
		
		case 2:
			html= Edit.reDrawXML(Edit.getSelectionStart(), Edit.getSelectionEnd());
		    if (html != null)
			    log.e(html, true);
			break;
			
		case 3:
		    Edit.Uedo();
			break;
			
	    case 4:
			Edit.Redo();
			break;
		
		case 5:
			Edit.Format(0,Edit.getText().toString().length());
			break;
		case 6:
			
			break;
		}
		}catch(Exception e){}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	
//Activity负责创建，配置Edit
	
	public void ForEdit(){
		CodeEdit Edit= creatAEdit();
		files.addAEdit(Edit,ForEdit);
		configAEdit(Edit);	
		files.tabAEdit(files.getNowIndex(),ForEdit);
	}
	
	public CodeEdit creatAEdit(){
		EditDate stack=getEdit.getDate();
		EditText Lines=new EditText(this);
		CodeEdit Edit=getEdit.getE (this,stack,Lines);
		Edit.setId((int)System.currentTimeMillis());
		//onCreate中分配一个id，控件会自动保存上下文
		return Edit;
	}
	
	public void configAEdit(CodeEdit Edit)
	{
		configEditSelf(Edit);
		configEditText(files.getNowIndex());
		configEditPerceive(Edit);
		configEditWallpaper(files.getNowIndex());
	}
	public void configEditSelf(CodeEdit Edit)
	{
		
		//将Edit设置得更人性化
		//当进入放大模式时，关闭窗口，禁止长按
		Edit.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					
					if (p2.getAction() == MotionEvent.ACTION_UP)
					{
						//当手指抬起，还原 
						p1.setLongClickable(true);
					}
					return false;
				}
			});	
		Edit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					WindowFather.setX(-9999);
					WindowFather.setY(-9999);
				}
			});

	}

	public void configEditText(final int index)
	{
		files.getEdit(index).addTextChangedListener(new TextWatcher(){
                /**
				 * 输入框改变前的内容
				 *  charSequence 输入前字符串
				 *  start 起始光标，即改变前的光标位置
				 *  count 删除字符串的数量（这里的count是用str.length()计算的，因为删除一个emoji表情，count打印结果是 2）
				 *  after 输入框中改变后的字符串与起始位置的偏移量（也就是输入字符串的length）
				 */
				/*
				 输入4个字符，删除一个字符，它们的值变化：
				 0, 0, 1
				 1, 0, 1
				 2, 0, 1
				 3, 0, 1 
				 3, 1, 0
				 如果只输入字符，count为0，
				 */
				/*

				 */
            	@Override
				public void beforeTextChanged(CharSequence str, int start, int count, int after)
				{
					if(files.getEdit(index).IsModify){
						return;
						//如果它是由于Uedo造成的修改，则不能装入
					}
					try
					{
						if (count == 0)
						{
							//如果没有删除字符，本次即将从start开始插入after个字符，那么上次的字符串就是：
							//删除现在start～start+after之间的字符串
							files.getEdit(index).stack.put(start, start + after, "");
						}
						else
					{
							//如果删除了字符，本次从start开始删除了count个字符，那么上次的字符串就是：
							//从现在start-count开始，插入start-count～start之间的字符串
							files.getEdit(index).stack.put(start - count+1, start - count , str.toString().substring(start - count+1, start+1));
						}
					}
					catch (Exception e)
					{}
				}

				/**
				 * 输入框改变后的内容
				 *  charSequence 字符串信息
				 *  start 起始光标
				 *  before 输入框中改变前的字符串与起始位置的偏移量（也就是删除字符串的length）
				 *  count 输入字符串的数量（输入一个emoji表情，count打印结果是2）
				 */
				@Override
				public void onTextChanged(CharSequence text, int start, int lenghtBefore, int lengthAfter)
				{
				}

				/**
				 *  editable 输入结束呈现在输入框中的信息
				 */
				@Override
				public void afterTextChanged(Editable p1)
				{
					if(files.getEdit(index).IsModify)
						return;
					try{
					    mWindow.removeAllViews();
						//如果没有单词，不作展示
					    if(files.getEdit(index).openWindow(MainActivity.this,mWindow,files.getEdit(index).getSelectionStart())>0){
						     WindowFather.setX((re.getWidth()-WindowFather. getWidth())/2);
						     WindowFather.setY((re.getHeight()-WindowFather.getHeight())/2);
						}
						else{
							//如果删除字符后没有了单词，则移走
							WindowFather.setX(-9999);
							WindowFather.setY(-9999);
						}
						
					}catch(Exception e){}
				}
			});
	}
	
	public void configEditPerceive(CodeEdit Edit)
	{
		Edit.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick( View p1)
				{
					p1.setLongClickable(false);
					((CodeEdit)p1).closeInputor(MainActivity.this, p1);
					return false;
				}
			});
	}
	
	public void configEditWallpaper(final int index){

		re.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					WindowFather.setX(-9999);
					WindowFather.setY(-9999);
					files.getEdit(index).openInputor(MainActivity.this,files.getEdit(index));		
					files.getEdit(index).setSelection(files.getEdit(index).getText().toString().length());
				}
			});
		WindowFather.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					WindowFather.setX(-9999);
					WindowFather.setY(-9999);
				}
			});
	}
	
}


//FileList负责管理Edit
class FileList
{
	public ArrayList<CodeEdit> EditList;
	private int nowIndex=-1;
	FileList (){
		EditList=new ArrayList<>();
	}
	
	public void addAEdit(CodeEdit Edit,ViewGroup ForEdit){
		EditList.add(Edit);
		ForEdit.addView(Edit);
		nowIndex=EditList.size()-1;
	}
	public void tabAEdit(int index,ViewGroup ForEdit){
	    ForEdit.removeView(EditList.get(nowIndex));
		ForEdit.addView(EditList.get(index));
		nowIndex=index;
	}
	public void delAEdit(int index,ViewGroup ForEdit){
		EditList.remove(index);
		if(index==nowIndex){
		    tabAEdit(index-1,ForEdit);
		    nowIndex-=1;
		}
	}

	
	public int getNowIndex(){
		return nowIndex;
	}
	
	public CodeEdit getEdit(int index){
		return EditList.get(index);
	}
}

