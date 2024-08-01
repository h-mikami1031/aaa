package com.example.ReportManagement.controller;




import java.util.List;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ReportManagement.config.LoginUser;
import com.example.ReportManagement.entity.AttendanceList;



@Controller
public class startPageController {

	// 初期画面表示処理
		@GetMapping("/startPage")
		public String startPageView(Authentication authentication, Model mv) {
			LoginUser loginUser = (LoginUser) authentication.getPrincipal();

			List<GrantedAuthority> authorities = (List<GrantedAuthority>) loginUser.getAuthorities();


			boolean isAdmin = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
			// 管理者権限チェック
			if (isAdmin) {
				mv.addAttribute("display", true);
			} else {
				mv.addAttribute("display", false);
			}
			
			
			return "/startPage";
		}
}
