//検索ボタン押下時の処理
$(function() {
	$('#button').bind("click", function() {
		var userid = new RegExp($('#userid').val());
		var username = new RegExp($('#username').val());
		var subject = new RegExp($('#subject').val());

		var fromattendance = new Date($('#fromattendance').val());
		var fromattendancestr = ($('#fromattendance').val());
		var toattendance = new Date($('#toattendance').val());
		var toattendancestr = ($('#toattendance').val());

		var fromcheck_day = new Date($('#fromcheck_day').val());
		var fromcheck_daystr = ($('#fromcheck_day').val());
		var tocheck_day = new Date($('#tocheck_day').val());
		var tocheck_daystr = ($('#tocheck_day').val());

		var adminflag = document.querySelector("#adminflag").value;

		$('#data-table tbody tr').each(function() {
			var useridtxt = "";
			var usernametxt = "";
			var attendancetxt = "";
			var attendancedate = "";
			var subjecttxt = "";
			var check_daytxt = "";
			var check_daydate = "";

			//管理者権限による検索条件分岐
			if (adminflag == "[ADMIN]") {
				useridtxt = $(this).find("td:eq(0)").html();
				usernametxt = $(this).find("td:eq(1)").html();
				attendancetxt = $(this).find("td:eq(2)").html();
				attendancedate = new Date(attendancetxt);
				subjecttxt = $(this).find("td:eq(3)").html();
				check_daytxt = $(this).find("td:eq(4)").html();
				check_daydate = new Date(check_daytxt);
			} else {
				attendancetxt = $(this).find("td:eq(0)").html();
				attendancedate = new Date(attendancetxt);
				subjecttxt = $(this).find("td:eq(1)").html();
				check_daytxt = $(this).find("td:eq(2)").html();
				check_daydate = new Date(check_daytxt);
			}

			//検索項目チェック
			if (useridtxt.match(userid) != null) {
				if (usernametxt.match(username) != null) {
					if (subjecttxt.match(subject) != null) {

						//研修参加日絞り込み	
						if (fromattendance <= toattendance || (!fromattendancestr || !toattendancestr)) {
							if (fromattendance <= attendancedate || !fromattendancestr) {
								if (attendancedate <= toattendance || !toattendancestr) {

									//チェック予定日絞り込み
									if (fromcheck_day <= tocheck_day || (!fromcheck_daystr || !tocheck_daystr)) {
										if (fromcheck_day <= check_daydate || !fromcheck_daystr) {
											if (check_daydate <= tocheck_day || !tocheck_daystr) {
												$(this).show();
											} else {
												$(this).hide();
											}
										} else {
											$(this).hide();
										}
									} else {
										$(this).hide();
									}

								} else {
									$(this).hide();
								}

							} else {
								$(this).hide();
							}

						} else {
							$(this).hide();
						}

					} else {
						$(this).hide();
					}
				} else {
					$(this).hide();
				}
			} else {
				$(this).hide();
			}
		});
	});

	//すべて表示ボタン押下時の処理
	$('#button2').bind("click", function() {
		$('#userid').val('');
		$('#username').val('');
		$('#subject').val('');
		$('#fromattendance').val('');
		$('#toattendance').val('');
		$('#fromcheck_day').val('');
		$('#tocheck_day').val('');
		$('#data-table tr').show();
	});
});


//報告書一覧のソート機能
$(document).ready(function() {
	$("#data-table").tablesorter();
});

window.addEventListener('DOMContentLoaded', function() {
	const tableHeaders = document.querySelectorAll('#data-table th');
	tableHeaders.forEach(header => {
		header.addEventListener('click', function() {
			const currentSort = this.getAttribute('data-sort');
			const arrowDown = '▼';
			const arrowUp = '▲';

			tableHeaders.forEach(header => {
				header.removeAttribute('data-sort');
				const headerInner = header.querySelector('.tablesorter-header-inner');
				headerInner.textContent = headerInner.textContent.replace(arrowUp, '').replace(arrowDown, '');
			});

			if (currentSort === 'asc') {
				this.setAttribute('data-sort', 'desc');
				const headerInner = this.querySelector('.tablesorter-header-inner');
				headerInner.textContent = headerInner.textContent + arrowDown;
				headerInner.textContent;
			} else {
				this.setAttribute('data-sort', 'asc');
				const headerInner = this.querySelector('.tablesorter-header-inner');
				headerInner.textContent = headerInner.textContent + arrowUp;
				headerInner.textContent;
			}
		});
	});
});

//エラーメッセージ表示
document.addEventListener("DOMContentLoaded", function() {
	//保存ボタン押下時
	document.getElementById("save").addEventListener("click", function(event) {
		var attendance = ($('#attendance').val());;
		var subject = ($('#subjectName').val());;
		var clickedButton = event.target;
		if (clickedButton.id == "save") {
			if (!attendance || !subject) {
				alert("研修参加日と研修名を入力してください");
				event.preventDefault();
				return false;
			}

		}
	});
	//Excel出力ボタン押下時	
	document.getElementById("createExcel").addEventListener("click", function(event) {
		var attendance = ($('#attendance').val());;
		var subject = ($('#subjectName').val());;
		var clickedButton = event.target;
		if (clickedButton.id == "createExcel") {
			if (!attendance || !subject) {
				alert("研修参加日と研修名を入力してください");
				event.preventDefault();
				return false;
			}

		}
	});
});

//テキストボックスのエンターキーを無効化
function disableEnterKey(event) {
	if (event.keyCode === 13) {
		event.preventDefault();
	}
}
