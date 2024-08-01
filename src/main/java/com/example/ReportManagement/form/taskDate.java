package com.example.ReportManagement.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.example.ReportManagement.entity.DailyReportList;
import com.example.ReportManagement.entity.TaskReportList;
import com.example.ReportManagement.entity.TaskScheduleList;

import groovy.util.IFileNameFinder;
import lombok.Data;

//業務日報データの一時保存
@Data
public class taskDate {
	// 業務日報ID
	private int report_id;

	// 社員番号
	private int user_id;

	// 氏名
	private String user_name;

	// ログイン時氏名
	private String loginname;

	// 業務日報の日付
	private String task_date;

	// 部署名
	private String department;

	// 役職名
	private String post;

	// 本人コメント
	private String comment;

	// 部長氏名
	private String general_manager;

	// 課長氏名
	private String manager;

	private List<ScheduleItem> schedule_items;
	
	private List<TaskItem> task_items;

	

	// インサートフラグ
	private int insertflag;

	// 登録日
	private String created;
	

	// エンティティクラスのデータ作成
	public DailyReportList toDailyReportListEntity() {
		DailyReportList dailyReportList = new DailyReportList();
		dailyReportList.setReport_id(report_id);
		dailyReportList.setUser_id(user_id);
		dailyReportList.setUser_name(user_name);
		dailyReportList.setTask_date(task_date);
		dailyReportList.setDepartment(department);
		dailyReportList.setPost(post);
		dailyReportList.setComment(comment);
		dailyReportList.setGeneral_manager(general_manager);
		dailyReportList.setManager(manager);


		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDateStr = dateFormat.format(nowDate);

		dailyReportList.setCreated(nowDateStr);
		dailyReportList.setModified(nowDateStr);
		
		TaskScheduleList taskScheduleList;
		if(schedule_items != null) {
			for(ScheduleItem scheduleItem : schedule_items) {
				taskScheduleList = new TaskScheduleList(scheduleItem.getTask_schedule_id(),null,scheduleItem.getPriority(),scheduleItem.getSchedule(),scheduleItem.getSchedule_progress(),scheduleItem.getActual_progress(),0);
				dailyReportList.addSchedule(taskScheduleList);
			}
		}
		
		TaskReportList taskReportList;
		if(task_items != null) {
			for(TaskItem taskItem : task_items) {
				taskReportList = new TaskReportList(taskItem.getTask_id(),null,taskItem.getTask(),taskItem.getFree_text(),taskItem.getStart_time(),taskItem.getEnd_time(),0);
				dailyReportList.addTask(taskReportList);
			}
		}

		return dailyReportList;
	}
	
	

}
