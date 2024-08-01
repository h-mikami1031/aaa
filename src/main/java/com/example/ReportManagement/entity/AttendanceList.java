package com.example.ReportManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "attendance_list")
@Data
//報告書一覧のエンティティクラス
public class AttendanceList {
	// ファイル番号(主キー)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int fileid;

	// 社員番号
	@Column
	private int user_id;

	// 氏名
	@Column
	private String user_name;

	// 研修参加日
	@Column
	private String attendance;

	// 研修名
	@Column
	private String subject;

	// 評価
	@Column
	private String evaluation;

	// フリーコメント
	@Column
	private String comment;

	// やること1
	@Column
	private String todo1;

	// やること2
	@Column
	private String todo2;

	// やること3
	@Column
	private String todo3;

	// 本人評価1
	@Column
	private String todid1;

	// 本人評価2
	@Column
	private String todid2;

	// 本人評価3
	@Column
	private String todid3;

	// チェック予定日
	@Column
	private String check_day;

	// 登録日
	@Column
	private String created;

	// 更新日
	@Column
	private String modified;

}
