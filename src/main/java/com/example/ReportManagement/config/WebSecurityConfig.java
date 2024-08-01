package com.example.ReportManagement.config;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.events.SessionCreatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration

public class WebSecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Spring Securityの設定
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// 静的リソースへのリクエスト設定(CSS、Javascript、画像など)
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 認可を必要としないアクセスの許可

				// 管理者のみ閲覧可能な画面
				.requestMatchers("/userCreate").hasAuthority("ADMIN").requestMatchers("/userSelect")
				.hasAuthority("ADMIN").requestMatchers("/userEdit").hasAuthority("ADMIN")

				.anyRequest().authenticated()// 他全てのリクエストは認証が必要
		)

				.formLogin(login -> login.loginPage("/login").successHandler(successHandler()).permitAll())

				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.deleteCookies("JSESSIONIDS").permitAll())

				.sessionManagement(session -> session.maximumSessions(1).maxSessionsPreventsLogin(true)
						.sessionRegistry(sessionRegistry()));

		return http.build();
	}

	// 権限によってログイン後の遷移先を変更
	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

}
