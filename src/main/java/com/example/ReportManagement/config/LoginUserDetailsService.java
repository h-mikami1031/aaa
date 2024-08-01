package com.example.ReportManagement.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ReportManagement.entity.UserList;
import com.example.ReportManagement.repository.UserRepository;

@Service
public class LoginUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public LoginUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		int userId = Integer.parseInt(username); // Stringからintに変換

		// リポジトリを使用してDBからユーザー情報を検索する
		Optional<UserList> user = userRepository.findById(userId);
		if (user == null) {
			throw new UsernameNotFoundException(String.valueOf(userId));
		}
		UserList userList = user.get();

		// ユーザーの権限情報を管理者か普通のユーザーかで判定して設定する
		String authorities;
		if (userList.getAdminflag() == 1) {
			authorities = "ADMIN";
		} else {
			authorities = "USER";
		}

		return new LoginUser(userId, userList.getUser_name(), userList.getPass(), authorities);
	}
}
