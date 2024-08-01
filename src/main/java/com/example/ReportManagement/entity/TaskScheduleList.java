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
@Table(name = "task_schedule_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskScheduleList {

	// 業務予定ID(主キー)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int task_schedule_id;

	// 業務日報ID
	 @ManyToOne
	@JoinColumn(name = "report_id")
	private DailyReportList dailyReportList;

	// 優先順位
	@Column
	private int priority;

	// 予定業務内容
	@Column
	private String schedule;

	// 予定進歩率
	@Column
	private int schedule_progress;

	// 実際の進歩率
	@Column
	private int actual_progress;
	
	// 削除フラグ
	@Column
	private int delete_flag;
}
