package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	// 주석 테스트, comment test, コメントのテスト（検討）
	@GetMapping("/")
	public String index()
	{
		return "index"; 
	}
}
