package com.example.ReportManagement.controller;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ReportManagement.entity.UserList;
import com.example.ReportManagement.form.UserData;
import com.example.ReportManagement.repository.UserRepository;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class loginController {
	private final UserRepository userRepository;

	// ログイン画面表示
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	// ログイン処理
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("UserData") UserData form, RedirectAttributes redirectAttributes) {

		// 入力されたデータをエンティティクラスに入れる
		UserList userList = form.toEntity();
		String pass = userList.getPass();
		int user_id = userList.getUser_id();

		// ユーザー情報取得
		Optional<UserList> userOptional = userRepository.findById(user_id);

		// 存在する社員番号かチェック
		if (userOptional.isPresent()) {
			userList = userOptional.get();

			// パスワードチェック
			if (pass.equals(userList.getPass())) {
				redirectAttributes.addAttribute("user_id", user_id);
				redirectAttributes.addAttribute("user_name", userList.getUser_name());
				redirectAttributes.addAttribute("fileid", 0);
				redirectAttributes.addAttribute("adminflag", userList.getAdminflag());
				if (userList.getAdminflag() == 1) {
					return "redirect:/taskSelection";
				}
				return "redirect:/main";
			} else {
				redirectAttributes.addFlashAttribute("display", true);
				return "redirect:/login";
			}
		} else {
			redirectAttributes.addFlashAttribute("display", true);
			return "redirect:/login";
		}
	}
}
