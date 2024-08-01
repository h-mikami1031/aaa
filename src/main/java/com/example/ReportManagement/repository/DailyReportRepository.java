package com.example.ReportManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ReportManagement.entity.AttendanceList;
import com.example.ReportManagement.entity.DailyReportList;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReportList, Integer>{

	@Query("SELECT a FROM DailyReportList a WHERE a.user_id >= (SELECT MIN(user_id) FROM UserList) AND a.user_id <= (SELECT MAX(user_id) FROM UserList) ORDER BY a.user_id,a.task_date")
	List<DailyReportList> findByUserIdsInRange();
}
