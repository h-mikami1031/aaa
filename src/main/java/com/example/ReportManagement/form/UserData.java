package com.example.ReportManagement.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ReportManagement.entity.UserList;
import lombok.Data;

//ユーザーデータの一時保存
@Data
public class UserData {
	
	//社員番号
	private int user_id;
	
	//氏名
	private String user_name;
	
	//変更前の氏名
	private String oldname;
	
	//パスワード
	private String pass;
	
	//再入力したパスワード
	private String rePass;
	
	//管理者権限
	private int adminflag;
	
	//変更前の管理者権限
	private int oldadminflag;
	
	//登録日
	private String created;
	
	//エンティティクラスのデータを生成
	public UserList toEntity() {
		
		
		UserList userList = new UserList();
		userList.setUser_id(user_id);
		userList.setUser_name(user_name);
		userList.setPass(pass);
		userList.setAdminflag(adminflag);
			
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDateStr = dateFormat.format(nowDate);
		
		userList.setCreated(nowDateStr);
		userList.setModified(nowDateStr);
	
		return userList;
	}
}
