package com.example.ReportManagement.controller;

import java.io.Console;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.poi.examples.hssf.usermodel.CellComments;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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
import com.example.ReportManagement.entity.DailyReportList;
import com.example.ReportManagement.entity.TaskReportList;
import com.example.ReportManagement.entity.TaskScheduleList;
import com.example.ReportManagement.form.ReportData;
import com.example.ReportManagement.form.ScheduleItem;
import com.example.ReportManagement.form.TaskItem;
import com.example.ReportManagement.form.taskDate;
import com.example.ReportManagement.repository.DailyReportRepository;
import com.example.ReportManagement.repository.ReportRepository;
import com.example.ReportManagement.repository.TaskReportListRepository;
import com.example.ReportManagement.repository.TaskScheduleListRepository;
import com.example.ReportManagement.repository.UserRepository;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import groovyjarjarasm.asm.tree.IntInsnNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class taskReportController {
	private final DailyReportRepository dailyReportRepository;
	private final TaskScheduleListRepository taskScheduleListRepository;
	private final TaskReportListRepository taskReportListRepository;

	// 勤務報告書画面表示
	@GetMapping("/taskReport")
	public Model taskReportView(Model mv, Authentication authentication,
			@ModelAttribute("taskDate") taskDate taskDate) {

		LoginUser loginUser = (LoginUser) authentication.getPrincipal();

		List<GrantedAuthority> authorities = (List<GrantedAuthority>) loginUser.getAuthorities();

		List<DailyReportList> dailyReportListFindList = dailyReportRepository.findByUserIdsInRange();
		Stream<DailyReportList> dailyReportListStream = dailyReportListFindList.stream();

		boolean isAdmin = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
		// 管理者権限チェック
		if (isAdmin) {
			mv.addAttribute("display", true);
		} else {
			mv.addAttribute("display", false);
			dailyReportListStream = dailyReportListStream.filter(p -> p.getUser_id() == loginUser.getUserId());
		}

		// 画面に表示させる報告書データ
		// report_id＝0なら、新規作成
		if (taskDate.getReport_id() == 0) {
			mv.addAttribute("user_name", loginUser.getUsername());
			mv.addAttribute("insertflag", 1);
			mv.addAttribute("report_id", 0);
			mv.addAttribute("user_id", loginUser.getUserId());
		} else {

			DailyReportList dailyReportList = dailyReportRepository.findById(taskDate.getReport_id()).get();

			List<TaskScheduleList> taskScheduleList = dailyReportList.getTaskScheduleList();
			taskScheduleList.removeIf(item -> item.getDelete_flag() == 1);

			List<TaskReportList> taskReportList = dailyReportList.getTaskReportList();
			taskReportList.removeIf(item -> item.getDelete_flag() == 1);

			mv.addAttribute("taskDate", dailyReportList);
			mv.addAttribute("taskScheduleList", taskScheduleList);
			mv.addAttribute("taskReportList", taskReportList);
			mv.addAttribute("insertflag", 0);
			mv.addAttribute("user_name", dailyReportList.getUser_name());
			mv.addAttribute("created", dailyReportList.getCreated());
			mv.addAttribute("user_id", dailyReportList.getUser_id());
			mv.addAttribute("report_id", dailyReportList.getReport_id());
		}

		mv.addAttribute("dailyReportList", dailyReportListStream);

		mv.addAttribute("adminflag", loginUser.getAuthorities());

		return mv;
	}

	// 保存処理
	@RequestMapping(value = "/taskReport", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute("taskDate") taskDate taskDate, RedirectAttributes redirectAttributes,
			@RequestParam String action) {

		if (action.equals("保存")) {
			// 業務予定の不要行削除、ソート
			List<ScheduleItem> scheduleItemList = taskDate.getSchedule_items();
			scheduleItemList.removeIf(item -> item.getPriority() == 0);
			scheduleItemList.sort(Comparator.comparing(ScheduleItem::getPriority));
			taskDate.setSchedule_items(scheduleItemList);

			// 業務報告の不要行削除、ソート
			List<TaskItem> taskItemList = taskDate.getTask_items();
			taskItemList.removeIf(item -> item.getStart_time() == null);
			taskItemList.sort(Comparator.comparing(TaskItem::getStart_time));
			taskDate.setTask_items(taskItemList);

			// entityクラスへの代入
			DailyReportList dailyReportList = taskDate.toDailyReportListEntity();

			// 上書き保存時、既存データのdelete_flagを全て立てる
			if (taskDate.getInsertflag() == 0) {
				taskScheduleListRepository.updateDeleteFlag(taskDate.getReport_id());
				taskReportListRepository.updateDeleteFlag(taskDate.getReport_id());
			}

			// 保存処理
			dailyReportList = dailyReportRepository.save(dailyReportList);

			taskDate.setInsertflag(1); // insertflagを1に設定して画面のテキストボックスはそのまま保持
			redirectAttributes.addFlashAttribute("taskDate", taskDate);
			redirectAttributes.addAttribute("report_id", dailyReportList.getReport_id());
		} else if (action.equals("新規作成")) {
			redirectAttributes.addAttribute("report_id", 0);
		}

		return "redirect:/taskReport";
	}

	// Excelダウンロード処理
	@PostMapping("/testDownload")
	public void downloadExcel(@ModelAttribute("taskDate") taskDate taskDate, HttpServletResponse response)
			throws IOException {

		// 追加された行数カウント
		int addRowCount = 0;

		// プロパティファイルからファイルパスを取得
		Properties properties = new Properties();
		String strpass = "./config.properties";

		String templatePath = "";
		String outputPath = "";

		try {
			InputStream istream = new FileInputStream(strpass);
			properties.load(istream);
			templatePath = properties.getProperty("taskReportTemplateFilePath");
			outputPath = properties.getProperty("taskReportOutputPath");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// テンプレートファイルの一時的なコピーを作成
		Path tempCopyPath = Files.createTempFile("template_copy", ".xlsx");
		Files.copy(Paths.get(templatePath), tempCopyPath, StandardCopyOption.REPLACE_EXISTING);
		Workbook templateWorkbook = WorkbookFactory.create(new FileInputStream(tempCopyPath.toString()));

		// Excelにデータを入力
		Sheet sheet = templateWorkbook.getSheet("業務日報");

		// 日付データのセット
		Row outputRow = sheet.getRow(0);
		Cell outputCell_date = outputRow.getCell(27);
		outputCell_date.setCellValue(taskDate.getTask_date());

		// 所属データのセット
		outputRow = sheet.getRow(1);
		Cell outputCell_department = outputRow.getCell(27);
		outputCell_department.setCellValue(taskDate.getDepartment());

		// 役職データのセット
		outputRow = sheet.getRow(2);
		Cell outputCell_post = outputRow.getCell(27);
		outputCell_post.setCellValue(taskDate.getPost());

		// 氏名データのセット
		outputRow = sheet.getRow(3);
		Cell outputCell_name = outputRow.getCell(27);
		outputCell_name.setCellValue(taskDate.getUser_name());

		List<ScheduleItem> scheduleItemList = taskDate.getSchedule_items();
		scheduleItemList.removeIf(item -> item.getPriority() == 0);
		scheduleItemList.sort(Comparator.comparing(ScheduleItem::getPriority));

		// 追加する業務予定の行数
		int scheduleItemListOverSize = scheduleItemList.size() - 8;
		int add_count = 0;
		int remainder = 0;
		if (scheduleItemListOverSize > 0) {
			add_count = scheduleItemListOverSize / 2;
			remainder = scheduleItemListOverSize % 2;

			if (remainder == 1) {
				add_count++;
			}
		}

		if (add_count > 0) {
			for (int i = 0; i < add_count; i++) {
				Row sourceRow = sheet.getRow(8);
				sheet.shiftRows(9, sheet.getLastRowNum(), 1, true, false);
				Row newRow = sheet.createRow(9);

				// コピー元のセルを取得して新しい行にコピー
				for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
					Cell sourceCell = sourceRow.getCell(j);
					Cell newCell = newRow.createCell(j);

					if (sourceCell != null) {
						// セルのスタイルをコピー
						CellStyle newCellStyle = templateWorkbook.createCellStyle();
						newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
						newCell.setCellStyle(newCellStyle);

					}

				}

				// セル結合のコピー
				for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
					CellRangeAddress cellRangeAddress = sheet.getMergedRegion(j);
					if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
						CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
								(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
								cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
						sheet.addMergedRegion(newCellRangeAddress);
					}

				}

			}
		}

		int row_count = 0;
		int row = 0;
		int cell_count = 0;

		// 業務予定のデータセット
		for (ScheduleItem scheduleItem : scheduleItemList) {

			row = row_count + 7;

			outputRow = sheet.getRow(row);
			Cell outputCell_priority = outputRow.getCell(cell_count);
			outputCell_priority.setCellValue(scheduleItem.getPriority());

			Cell outputCell_schedule = outputRow.getCell(cell_count + 2);
			outputCell_schedule.setCellValue(scheduleItem.getSchedule());

			Cell outputCell_schedule_progress = outputRow.getCell(cell_count + 14);
			outputCell_schedule_progress.setCellValue(scheduleItem.getSchedule_progress());

			Cell outputCell_actual_progress = outputRow.getCell(cell_count + 16);
			outputCell_actual_progress.setCellValue(scheduleItem.getActual_progress());

			row_count++;

			if (row_count >= add_count + 4) {
				row_count = 0;
				cell_count = cell_count + 18;
			}
		}

		addRowCount = addRowCount + add_count;

		// 業務報告の不要行削除、ソート
		List<TaskItem> taskItemList = taskDate.getTask_items();
		taskItemList.removeIf(item -> item.getStart_time() == null);
		taskItemList.sort(Comparator.comparing(TaskItem::getStart_time));

		for (int i = 0; i < 11; i++) {
			int task_row = 13 + addRowCount;// テンプレートの業務報告の最初の行番号
			int add_task_row_count = 0;// 時間帯ごとで追加した行数カウント

			int hour = 8 + i;// 作業する時間帯
			String selected_hour = null;// stream検索用

			// 12時台の入力は行わない
			if (hour == 12) {
				continue;
			}

			final int final_task_row = task_row + (i * 2);// ラムダ式用にfinal変数として宣言

			if (hour < 10) {
				selected_hour = "0" + hour + ":";
			} else {
				selected_hour = hour + ":";
			}

			final String final_selected_hour = selected_hour;// ラムダ式用にfinal変数として宣言

			Stream<TaskItem> filterTaskItemList = filterTaskList(taskItemList, hour, final_selected_hour);

			// カウント用stream
			Stream<TaskItem> streamCount = filterTaskList(taskItemList, hour, final_selected_hour);

			// 各時間帯の行数
			long timeCount = streamCount.count();

			if (timeCount >= 3) {
				// セル結合を解除
				int mergedRegionIndex = getMergedRegion(sheet, final_task_row, 0);
				if (mergedRegionIndex != -1) {
					sheet.removeMergedRegion(mergedRegionIndex);
				}
				for (int j = 0; j < timeCount - 2; j++) {

					Row sourceRow = sheet.getRow(final_task_row + 1);
					sheet.shiftRows(final_task_row + 1, sheet.getLastRowNum(), 1, true, false);
					Row newRow = sheet.createRow(final_task_row + 1);

					// コピー元のセルを取得して新しい行にコピー
					for (int k = 0; k < sourceRow.getLastCellNum(); k++) {
						Cell sourceCell = sourceRow.getCell(k);
						Cell newCell = newRow.createCell(k);
						if (sourceCell != null) {
							// セルのスタイルをコピー
							CellStyle newCellStyle = templateWorkbook.createCellStyle();
							newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
							newCell.setCellStyle(newCellStyle);

							// 11時台は行追加で罫線がつくため、不要な罫線の削除
							if (hour == 11 || hour == 19) {
								// セルスタイルの取得
								CellStyle cellStyle = newCell.getCellStyle();
								// 上部の罫線を削除
								cellStyle.setBorderBottom(BorderStyle.NONE);

								// セルにスタイルを設定
								newCell.setCellStyle(cellStyle);
							}
						}
					}

					add_task_row_count++;
					addRowCount++;
				}

				// セル結合
				add_task_row_count++;
				CellRangeAddress mergedRegion = new CellRangeAddress(final_task_row,
						final_task_row + add_task_row_count, 0, 1);
				sheet.addMergedRegion(mergedRegion);
				add_task_row_count = 0;
			}

			AtomicInteger columnIndex = new AtomicInteger(0); // 列インデックスの初期値

			// 業務報告のデータセット
			filterTaskItemList.forEach(item -> {
				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH時mm分");

				LocalTime start_time = LocalTime.parse(item.getStart_time());
				String start_time_str = start_time.format(outputFormatter);

				String end_time_str = "";
				if (item.getEnd_time() != "") {
					LocalTime end_time = LocalTime.parse(item.getEnd_time());
					end_time_str = end_time.format(outputFormatter);
				}

				String time_str = start_time_str + "～" + end_time_str;

				Row output_task_row = sheet.getRow(final_task_row + columnIndex.get());

				Cell outputCell_time = output_task_row.getCell(2);
				outputCell_time.setCellValue(time_str);

				Cell outputCell_task = output_task_row.getCell(10);
				outputCell_task.setCellValue(item.getTask());

				Cell outputCell_free_text = output_task_row.getCell(22);
				outputCell_free_text.setCellValue(item.getFree_text());

				columnIndex.incrementAndGet();
			});
		}

		outputRow = sheet.getRow(38 + addRowCount);
		Cell outputCell_comment = outputRow.getCell(8);
		outputCell_comment.setCellValue(taskDate.getComment());

		outputRow = sheet.getRow(39 + addRowCount);
		Cell outputCell_general_manager = outputRow.getCell(0);
		outputCell_general_manager.setCellValue(taskDate.getGeneral_manager());

		Cell outputCell_manager = outputRow.getCell(4);
		outputCell_manager.setCellValue(taskDate.getManager());

		// Excelの保存
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		templateWorkbook.write(outputStream);
		templateWorkbook.close();
		outputStream.close();

		// ファイル名設定
		String fileName = "02_【業務報告】%s_%s_%s_v1.2.xlsx";
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyMMdd");

		Date task_date = null;
		try {
			task_date = inputFormat.parse(taskDate.getTask_date());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String task_date_str = outputFormat.format(task_date);
		fileName = String.format(fileName, taskDate.getUser_id(), taskDate.getUser_name(), task_date_str);
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

	// セル結合された範囲のインデックス取得
	private static int getMergedRegion(Sheet sheet, int row, int column) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
			if (mergedRegion.isInRange(row, column)) {
				return i;
			}
		}
		return -1;
	}

	private static Stream<TaskItem> filterTaskList(List<TaskItem> taskItemList, int hour, String final_selected_hour) {
		if (hour == 13) {
			Stream<TaskItem> filterTaskItemList = taskItemList.stream()
					.filter(task -> task.getStart_time().startsWith("12:") || task.getStart_time().startsWith("13:"));
			return filterTaskItemList;
		} else {
			Stream<TaskItem> filterTaskItemList = taskItemList.stream()
					.filter(task -> task.getStart_time().startsWith(final_selected_hour));
			return filterTaskItemList;
		}
	}

}
