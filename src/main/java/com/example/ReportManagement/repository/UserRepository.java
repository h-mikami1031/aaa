package com.example.ReportManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ReportManagement.entity.UserList;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserList, Integer> {

}
