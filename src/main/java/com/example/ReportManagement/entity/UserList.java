package com.example.ReportManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

//ユーザー一覧のエンティティクラス
@Entity
@Table(name = "user_list")
@Data
public class UserList {
	// 社員番号(主キー)
	@Id
	@Column
	private int user_id;

	// 氏名
	@Column
	private String user_name;

	// パスワード
	@Column
	private String pass;

	// 管理者フラグ
	@Column
	private int adminflag;

	// 登録日
	@Column
	private String created;

	// 更新日
	@Column
	private String modified;

}
