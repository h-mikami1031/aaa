package com.example.ReportManagement.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ReportManagement.entity.AttendanceList;

import lombok.Data;

//報告書データの一時保存
@Data
public class ReportData {

	// ファイル番号
	private int fileid;

	// 社員番号
	private int user_id;

	// 氏名
	private String user_name;

	// ログイン時氏名
	private String loginname;

	// 研修参加日
	private String attendance;

	// 研修名
	private String subject;

	// 評価
	private String evaluation;

	// フリーコメント
	private String comment;

	// やること1
	private String todo1;

	// やること2
	private String todo2;

	// やること3
	private String todo3;

	// 本人評価1
	private String todid1;

	// 本人評価2
	private String todid2;

	// 本人評価3
	private String todid3;

	// チェック予定日
	private String check_day;

	// インサートフラグ
	private int insertflag;

	// 登録日
	private String created;

	// エンティティクラスのデータ作成
	public AttendanceList toEntity() {
		AttendanceList attendanceList = new AttendanceList();
		attendanceList.setFileid(fileid);
		attendanceList.setUser_id(user_id);
		attendanceList.setUser_name(user_name);
		attendanceList.setAttendance(attendance);
		attendanceList.setSubject(subject);
		attendanceList.setEvaluation(evaluation);
		attendanceList.setComment(comment);
		attendanceList.setTodid1(todid1);
		attendanceList.setTodid2(todid2);
		attendanceList.setTodid3(todid3);
		attendanceList.setTodo1(todo1);
		attendanceList.setTodo2(todo2);
		attendanceList.setTodo3(todo3);
		attendanceList.setCheck_day(check_day);

		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDateStr = dateFormat.format(nowDate);

		attendanceList.setCreated(nowDateStr);
		attendanceList.setModified(nowDateStr);

		return attendanceList;
	}

}
