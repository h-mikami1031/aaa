package com.example.ReportManagement.form;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskItem {

	// 業務ID
		private int task_id;

		// 業務内容
		private String task;

		// 報告・連絡内容
		private String free_text;

		// 開始時間
		private String start_time;

		// 終了時間
		private String end_time;
		
		// 削除フラグ
		private int delete_flag;
}
