package com.example.ReportManagement.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
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

public class userSelectController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// ユーザー検索画面表示
	@GetMapping("/userSelect")
	public ModelAndView userEditView(ModelAndView mv) {
		mv.addObject("display", true);

		return mv;
	}

	// 検索ボタン押下時処理
	@RequestMapping(value = "/userSelect", method = RequestMethod.POST)
	public String selectUser(@ModelAttribute("UserData") UserData form, RedirectAttributes redirectAttributes) {

		// 入力データをエンティティクラスに入れる。
		UserList userList = form.toEntity();

		String inputPassword = form.getPass();
		int user_id = userList.getUser_id();

		// ユーザー情報取得
		Optional<UserList> userOptional = userRepository.findById(user_id);

		// 存在する社員番号がチェック
		if (userOptional.isPresent()) {
			userList = userOptional.get();

			// パスワードチェック
			if (passwordEncoder.matches(inputPassword, userList.getPass())) {
				redirectAttributes.addAttribute("user_id", user_id);
				redirectAttributes.addAttribute("user_name", userList.getUser_name());
				redirectAttributes.addAttribute("adminflag", userList.getAdminflag());
				return "redirect:/userEdit";
			} else {
				redirectAttributes.addFlashAttribute("display", true);
				redirectAttributes.addFlashAttribute("paramErroruser", true);
				return "redirect:/userSelect";
			}
		} else {
			redirectAttributes.addFlashAttribute("display", true);
			redirectAttributes.addFlashAttribute("paramErroruser", true);
			return "redirect:/userSelect";
		}

	}

}
