package com.erhuoapp.erhuo.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;




public class ErUse {
	
	
	//public static int numOfComment = 0;
    //public static int numOfCollect = 0;
	
	
	// toggle输入法 E t
	public static void toggleInputMethod(final EditText Text,int t) {
    	//Text.setFocusable(true);  
    	//Text.setFocusableInTouchMode(true);  
    	//Text.requestFocus();  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
                   
        	public void run()  {  
        		InputMethodManager inputManager =  
        				(InputMethodManager)Text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
        		boolean isOpen = inputManager.isActive();
                if (isOpen) {
                	inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//显示了就关闭
                }
            }   
        	
       },  
       t);
    }
	
	//show输入法 E t
    public static void showInputMethod(final EditText Text,int t) {
    	//Text.setFocusable(true);  
    	//Text.setFocusableInTouchMode(true);  
    	//Text.requestFocus();  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
                   
        	public void run()  {  
        		InputMethodManager inputManager =  
        				(InputMethodManager)Text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
        		boolean isOpen = inputManager.isActive();
                if (isOpen) {
                	inputManager.showSoftInput(Text, InputMethodManager.SHOW_FORCED);
                }
            }   
        	
       },  
       t);
    }
    
    // hide输入法 E t
    public static void hideInputMethod(final EditText Text,int t) {
    	//Text.setFocusable(true);  
    	//Text.setFocusableInTouchMode(true);  
    	//Text.requestFocus();  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
                   
        	public void run()  {  
        		InputMethodManager inputManager = 
        				(InputMethodManager)Text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        		inputManager.hideSoftInputFromWindow(Text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);   
            }   
        	
       },  
       t);
    }

}
