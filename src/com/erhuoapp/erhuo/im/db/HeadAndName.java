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
package com.erhuoapp.erhuo.im.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.erhuoapp.erhuo.im.domain.User;
import com.erhuoapp.erhuo.im.domain.UserHeadAndName;

public class HeadAndName {
	public static final String TABLE_NAME = "head_and_name";
	public static final String COLUMN_NAME_ID = "userid";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_HEAD = "head";

	private DbOpenHelper dbHelper;

	public HeadAndName(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<UserHeadAndName> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (UserHeadAndName user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUserId());
				if(user.getNick() != null)
					values.put(COLUMN_NAME_NICK, user.getNick());
				if(user.getHead() != null)
					values.put(COLUMN_HEAD, user.getHead());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public List<UserHeadAndName> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<UserHeadAndName> contactList = new ArrayList<UserHeadAndName>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String userid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String head = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD));
				UserHeadAndName user = new UserHeadAndName();
				user.setUserId(userid);
				user.setNick(nick);
				user.setHead(head);
				contactList.add(user);
			}
			cursor.close();
		}
		return contactList;
	}
	
	public UserHeadAndName selectOne(String userid){
		UserHeadAndName mUserHeadAndName = new UserHeadAndName();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query(TABLE_NAME, 
					null, 
					COLUMN_NAME_ID + " = \"" + userid + "\"",
					null, null, null, null);
			while (cursor.moveToNext()) {
				String myuserid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String mynick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String myhead = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD));
				mUserHeadAndName.setUserId(myuserid);
				mUserHeadAndName.setNick(mynick);
				mUserHeadAndName.setHead(myhead);
			}
			cursor.close();
		}
		return mUserHeadAndName;
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String userid){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{userid});
		}
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(UserHeadAndName user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getUserId());
		if(user.getNick() != null)
			values.put(COLUMN_NAME_NICK, user.getNick());
		if(user.getHead() != null)
			values.put(COLUMN_HEAD, user.getHead());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
