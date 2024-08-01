package com.example.ReportManagement.form;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleItem {

	// 業務予定ID
	private int task_schedule_id;

	// 優先順位
	private int priority;

	// 予定業務内容
	private String schedule;

	// 予定進歩率
	private int schedule_progress;

	// 実際の進歩率
	private int actual_progress;
	
	// 削除フラグ
	private int delete_flag;
}
