package com.example.ReportManagement.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ReportManagement.config.LoginUser;
import com.example.ReportManagement.entity.AttendanceList;
import com.example.ReportManagement.form.ReportData;
import com.example.ReportManagement.repository.ReportRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MainController {
	private final ReportRepository reportRepository;

	// メイン画面表示処理
	@GetMapping("/main")
	public String mainView(Authentication authentication, @ModelAttribute("ReportData") ReportData form, Model mv) {

		LoginUser loginUser = (LoginUser) authentication.getPrincipal();

		List<GrantedAuthority> authorities = (List<GrantedAuthority>) loginUser.getAuthorities();

		// アコーディオンパネルに表示させるデータ一覧取得
		List<AttendanceList> attendanceListFindList = reportRepository.findByUserIdsInRange();
		Stream<AttendanceList> attendanceListStream = attendanceListFindList.stream();

		boolean isAdmin = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
		// 管理者権限チェック
		if (isAdmin) {
			mv.addAttribute("display", true);
			mv.addAttribute("attendanceList", attendanceListFindList);
		} else {
			mv.addAttribute("display", false);
			attendanceListStream = attendanceListStream.filter(p -> p.getUser_id() == loginUser.getUserId());
			mv.addAttribute("attendanceList", attendanceListStream);
		}

		// 画面に表示させる報告書データ
		// fileid＝0なら、新規作成
		if (form.getFileid() == 0) {
			mv.addAttribute("user_name", loginUser.getUsername());
			mv.addAttribute("insertflag", 1);
			mv.addAttribute("fileid", 0);
			mv.addAttribute("user_id", loginUser.getUserId());
		} else {

			AttendanceList attendanceList = reportRepository.findById(form.getFileid()).get();
			mv.addAttribute("ReportData", attendanceList);
			mv.addAttribute("insertflag", 0);
			mv.addAttribute("user_name", attendanceList.getUser_name());
			mv.addAttribute("created", attendanceList.getCreated());
			mv.addAttribute("user_id", attendanceList.getUser_id());
			mv.addAttribute("fileid", attendanceList.getFileid());
		}

		mv.addAttribute("adminflag", loginUser.getAuthorities());
		return "/main";
	}

	// 保存処理
	@RequestMapping(value = "/main", method = RequestMethod.POST)
	public String createReport(Authentication authentication, @ModelAttribute("ReportData") ReportData form,
			@RequestParam String action, RedirectAttributes redirectAttributes) {
		
		if (action.equals("保存")) {
			AttendanceList attendanceList = saveReport(form);
			
			form.setInsertflag(1); // insertflagを1に設定して画面のテキストボックスはそのまま保持

			redirectAttributes.addFlashAttribute("ReportData", form);
			redirectAttributes.addAttribute("fileid", attendanceList.getFileid());
		} else if (action.equals("新規作成")) {
			redirectAttributes.addAttribute("fileid", 0);
		}

		return "redirect:/main";
	}

	// レポートの保存処理
	private AttendanceList saveReport(ReportData form) {
		AttendanceList attendanceList = form.toEntity();
		if (form.getInsertflag() == 1) {
			attendanceList = reportRepository.saveAndFlush(attendanceList);
		} else {
			attendanceList.setCreated(form.getCreated());
			attendanceList = reportRepository.save(attendanceList);
		}
		
		return attendanceList;
	}

	// Excelダウンロード処理
	@PostMapping("/Download")
	public void downloadExcel(@ModelAttribute("ReportData") ReportData form, HttpServletResponse response)
			throws IOException {

		// プロパティファイルからファイルパスを取得
		Properties properties = new Properties();
		String strpass = "./config.properties";

		String templatePath = "";
		String outputPath = "";

		try {
			InputStream istream = new FileInputStream(strpass);
			properties.load(istream);
			templatePath = properties.getProperty("templatefilepath");
			outputPath = properties.getProperty("outputPath");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// テンプレートファイルの一時的なコピーを作成
		Path tempCopyPath = Files.createTempFile("template_copy", ".xlsx");
		Files.copy(Paths.get(templatePath), tempCopyPath, StandardCopyOption.REPLACE_EXISTING);
		Workbook templateWorkbook = WorkbookFactory.create(new FileInputStream(tempCopyPath.toString()));

		// Excelにデータを入力
		Sheet sheet = templateWorkbook.getSheet("受講報告書兼実行宣言");
		Row outputRow = sheet.getRow(2);
		Cell outputCell_attendance = outputRow.getCell(3);
		Cell outputCell_subject = outputRow.getCell(5);
		Cell outputCell_name = outputRow.getCell(10);
		outputCell_attendance.setCellValue(form.getAttendance());
		outputCell_subject.setCellValue(form.getSubject());
		outputCell_name.setCellValue(form.getUser_name());

		if (Objects.nonNull(form.getEvaluation())) {
			outputRow = sheet.getRow(5);
			Cell outputCell_evaluation = outputRow.getCell(2);
			String evaluation = outputCell_evaluation.getStringCellValue();
			int targetIndex = evaluation.indexOf(form.getEvaluation());
			if (targetIndex != -1) {
				int previousIndex = evaluation.lastIndexOf("□", targetIndex);
				if (previousIndex != -1) {
					String replacedString = evaluation.substring(0, previousIndex) + "☑"
							+ evaluation.substring(previousIndex + 1);
					outputCell_evaluation.setCellValue(replacedString);
				}
			}
		}

		outputRow = sheet.getRow(6);
		Cell outputCell_commetCell = outputRow.getCell(2);
		outputCell_commetCell.setCellValue(form.getComment());

		outputRow = sheet.getRow(9);
		Cell outputCell_todo1 = outputRow.getCell(2);
		Cell outputCell_todid1 = outputRow.getCell(13);
		outputCell_todo1.setCellValue(form.getTodo1());
		outputCell_todid1.setCellValue(form.getTodid1());

		outputRow = sheet.getRow(10);
		Cell outputCell_todo2 = outputRow.getCell(2);
		Cell outputCell_todid2 = outputRow.getCell(13);
		outputCell_todo2.setCellValue(form.getTodo2());
		outputCell_todid2.setCellValue(form.getTodid2());

		outputRow = sheet.getRow(11);
		Cell outputCell_todo3 = outputRow.getCell(2);
		Cell outputCell_todid3 = outputRow.getCell(13);
		outputCell_todo3.setCellValue(form.getTodo3());
		outputCell_todid3.setCellValue(form.getTodid3());

		if (!form.getCheck_day().isEmpty()) {
			outputRow = sheet.getRow(13);
			Cell outputCell_Chack_day = outputRow.getCell(10);
			LocalDate date = LocalDate.parse(form.getCheck_day());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
			String chack_day = date.format(formatter);
			outputCell_Chack_day.setCellValue("(チェック予定日：" + chack_day + "）");
		}

		// Excelの保存
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		templateWorkbook.write(outputStream);
		templateWorkbook.close();
		outputStream.close();

		// ファイル名設定
		String fileName = "【Biz受講報告書】%s_%s_%s_v2.0.xlsx";
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyMMdd");

		Date attendance_date = null;
		try {
			attendance_date = inputFormat.parse(form.getAttendance());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String attendance_day_str = outputFormat.format(attendance_date);
		fileName = String.format(fileName, form.getUser_id(), form.getUser_name(), attendance_day_str);
		;
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

		// ダウンロード処理
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
		InputStream fileInputStream = new FileInputStream(outputPath);
		OutputStream responseOutputStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = fileInputStream.read(buffer)) != -1) {
			responseOutputStream.write(buffer, 0, bytesRead);
		}
		fileInputStream.close();
		responseOutputStream.close();
	}
}
