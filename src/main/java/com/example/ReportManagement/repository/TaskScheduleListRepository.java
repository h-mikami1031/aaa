package com.example.ReportManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReportManagement.entity.DailyReportList;
import com.example.ReportManagement.entity.TaskScheduleList;

@Repository
public interface TaskScheduleListRepository extends JpaRepository<TaskScheduleList, Integer>{

	@Transactional
    @Modifying
	@Query("UPDATE TaskScheduleList t SET t.delete_flag = 1 WHERE t.dailyReportList.report_id = :report_id")
	int updateDeleteFlag(@Param("report_id") int report_id);
}
