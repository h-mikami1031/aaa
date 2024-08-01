package com.example.ReportManagement.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.ReportManagement.entity.UserList;
import com.example.ReportManagement.form.UserData;
import com.example.ReportManagement.repository.UserRepository;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class userCreateContriller {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// ユーザー登録画面表示
	@GetMapping("/userCreate")
	public ModelAndView userCreateView(ModelAndView mv) {
		mv.addObject("display", true);

		return mv;
	}

	// ユーザー登録処理
	@RequestMapping(value = "/userCreate", method = RequestMethod.POST)
	public ModelAndView userCreate(@ModelAttribute("UsertData") UserData form, ModelAndView mv) {
		mv.addObject("display", true);
		// パスワードとパスワード再入力が同じ値かチェック
		if (form.getPass().equals(form.getRePass())) {

			// 既に登録されている社員番号がチェック
			if (userRepository.existsById(form.getUser_id())) {
				mv.addObject("paramErrorusercreate", true);
				return mv;
			} else {
				UserList userList = form.toEntity();
				String hashedPassword = passwordEncoder.encode(userList.getPass());
				userList.setPass(hashedPassword);

				// insert文実行
				userRepository.saveAndFlush(userList);

				mv.addObject("paramCreateSuccess", true);
				return mv;
			}

		} else {
			mv.addObject("paramErrorpass", true);
			return mv;
		}

	}
}
