package com.example.ReportManagement.entity;

import java.util.ArrayList;
import java.util.List;

import groovy.transform.ToString;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "daily_report_list")
@Data
@ToString(excludes = "TaskScheduleList")
public class DailyReportList {

	// 業務日報ID(主キー)
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column
		private int report_id;

		// 社員番号
		@Column
		private int user_id;

		// 氏名
		@Column
		private String user_name;

		// 業務日報の日付
		@Column
		private String task_date;

		// 部署名
		@Column
		private String department;

		// 役職名
		@Column
		private String post;

		// 本人コメント
		@Column
		private String comment;

		// 部長氏名
		@Column
		private String general_manager;

		// 課長氏名
		@Column
		private String manager;

		// 登録日
		@Column
		private String created;

		// 更新日
		@Column
		private String modified;
		
		@OneToMany(mappedBy="dailyReportList",cascade = CascadeType.ALL)
		private List<TaskScheduleList> taskScheduleList = new ArrayList<>();
		
		public void addSchedule(TaskScheduleList taskSchedule) {
			taskSchedule.setDailyReportList(this);
			taskScheduleList.add(taskSchedule);
		}
		
		@OneToMany(mappedBy="dailyReportList",cascade = CascadeType.ALL)
		private List<TaskReportList> taskReportList = new ArrayList<>();
		
		public void addTask(TaskReportList taskReport) {
			taskReport.setDailyReportList(this);
			taskReportList.add(taskReport);
		}

}
