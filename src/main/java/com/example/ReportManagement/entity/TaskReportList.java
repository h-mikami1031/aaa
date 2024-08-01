package com.example.ReportManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_report_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReportList {

	// 業務ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int task_id;

	// 業務日報ID
	@ManyToOne
	@JoinColumn(name = "report_id")
	private DailyReportList dailyReportList;

	// 業務内容
	@Column
	private String task;

	// 報告や連絡内容等
	@Column
	private String free_text;

	// 開始時間
	@Column
	private String start_time;

	// 終了時間
	@Column
	private String end_time;

	// 削除フラグ
	@Column
	private int delete_flag;
}
