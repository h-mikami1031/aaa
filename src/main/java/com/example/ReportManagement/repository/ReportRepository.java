package com.example.ReportManagement.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ReportManagement.entity.AttendanceList;

@Repository
public interface ReportRepository extends JpaRepository<AttendanceList, Integer> {

	// 報告書全件取得
	@Query("SELECT a FROM AttendanceList a WHERE a.user_id >= (SELECT MIN(user_id) FROM UserList) AND a.user_id <= (SELECT MAX(user_id) FROM UserList) ORDER BY a.user_id,a.attendance")
	List<AttendanceList> findByUserIdsInRange();
}
