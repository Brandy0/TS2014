/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.erhuoapp.erhuo.im.domain;


public class UserHeadAndName {
	
	public String userid = "";
	public String nick = "";
	public String head = "";

	public void setUserId(String userid){
		this.userid = userid;
	}
	public String getUserId(){
		if(userid == null){
			userid = "";
		}
		return userid;
	}
	
	public void setNick(String nick){
		this.nick = nick;
	}
	public String getNick(){
		if(nick == null){
			nick = "";
		}
		return nick;
	}
	
	public void setHead(String head){
		this.head = head;
	}
	public String getHead(){
		if(head == null){
			head = "";
		}
		return head;
	}
	
}
