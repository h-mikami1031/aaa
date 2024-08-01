package com.example.ReportManagement.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.ReportManagement.entity.UserList;
import com.example.ReportManagement.form.UserData;
import com.example.ReportManagement.repository.UserRepository;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor

public class userEditController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// ユーザー編集画面表示
	@GetMapping("/userEdit")
	public ModelAndView userEditView(@ModelAttribute("UserData") UserData form, @RequestParam int user_id,
			@RequestParam String user_name, @RequestParam int adminflag, ModelAndView mv) {

		// パラメータの情報をHTML側に渡す。
		mv.addObject("user_id", user_id);
		mv.addObject("user_name", user_name);
		mv.addObject("adminflag", adminflag);

		// ユーザー情報取得
		UserList userList = userRepository.findById(user_id).get();

		mv.addObject("created", userList.getCreated());
		mv.addObject("display", true);

		return mv;
	}

	// ユーザー編集処理
	@RequestMapping(value = "/userEdit", method = RequestMethod.POST)
	public String editUser(@ModelAttribute("UserData") UserData form, RedirectAttributes redirectAttributes) {

		// パスワードチェック
		if (form.getPass().equals(form.getRePass())) {
			// 入力データをエンティティクラスに入れる
			UserList userList = form.toEntity();
			userList.setCreated(form.getCreated());// 登録日のupdateを回避
			String hashedPassword = passwordEncoder.encode(userList.getPass());
			userList.setPass(hashedPassword);

			// update文実行
			userRepository.save(userList);

			// 画面更新時に必要なデータを設定
			redirectAttributes.addFlashAttribute("paramEditSuccess", true);
			redirectAttributes.addAttribute("user_name", userList.getUser_name());
			redirectAttributes.addAttribute("adminflag", userList.getAdminflag());
		} else {
			// 画面更新時に必要なデータを設定
			redirectAttributes.addFlashAttribute("paramErrorpass", true);
			redirectAttributes.addAttribute("user_name", form.getOldname());
			redirectAttributes.addAttribute("adminflag", form.getOldadminflag());

		}
		// 画面更新時に必要なデータを設定
		redirectAttributes.addFlashAttribute("display", true);
		redirectAttributes.addAttribute("user_id", form.getUser_id());

		return "redirect:/userEdit";
	}

}
