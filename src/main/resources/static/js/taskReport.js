var table = document.getElementById('tableBody');
var table2 = document.getElementById('tableBody2');

// テーブルの行数を取得して表示
let tableBodyClicCount = table.rows.length;
let tableBody2ClicCount = table2.rows.length;
function addRow(tBody) {
	const tableBody = document.getElementById(tBody);
	const rows = tableBody.getElementsByTagName('tr');
	var rowCount = tableBody.getElementsByTagName('tr').length;
	const newRow = document.createElement('tr');
	if (tBody == 'tableBody') {
		newRow.innerHTML = `
							<td><span>${rowCount + 1}</span><input type="hidden" name="${'schedule_items[' + tableBodyClicCount + '].priority'}" value="${rows.length + 1}"></td>
							<td><input type="text" name="${'schedule_items[' + tableBodyClicCount + '].schedule'}" class="form-control input-cell" onkeydown="disableEnterKey(event)"></td>
							<td><input type="number" name="${'schedule_items[' + tableBodyClicCount + '].schedule_progress'}" class="form-control" min="0" max="100" value="100" onkeydown="disableEnterKey(event)"></td>
							<td><input type="number" name="${'schedule_items[' + tableBodyClicCount + '].actual_progress'}" class="form-control" min="0" max="100" value="0" onkeydown="disableEnterKey(event)"></td>
							<td><button class="btn btn-danger" onclick="removeRow(this,'tableBody')">削除</button></td>
						`;
		tableBody.appendChild(newRow);
		updateRowNumbers();
		tableBodyClicCount++;
	}

	if (tBody == 'tableBody2') {
		newRow.innerHTML = `
						<td><input type="time" class="form-control input-cell time-input" name="${'task_items[' + tableBody2ClicCount + '].start_time'}" onkeydown="disableEnterKey(event)"></td>
						<td><input type="time" class="form-control input-cell time-input" name="${'task_items[' + tableBody2ClicCount + '].end_time'}" onkeydown="disableEnterKey(event)"></td>
						<td><textarea name="${'task_items[' + tableBody2ClicCount + '].task'}" cols="80" rows="4"
											spellcheck="false"></textarea>
						</td>
						<td><textarea name="${'task_items[' + tableBody2ClicCount + '].free_text'}" cols="80" rows="4"
											spellcheck="false"></textarea>
						</td>
						<td><button class="btn btn-danger"
											onclick="removeRow(this,'tableBody2')">削除</button></td>
						`;
		tableBody.appendChild(newRow);
		tableBody2ClicCount++;

	}

}

function removeRow(button, tBody) {
	const row = button.parentNode.parentNode;
	row.parentNode.removeChild(row);
	if (tBody == 'tableBody') {
		updateRowNumbers();
	}




}

function updateRowNumbers() {
	var tableRows = document.querySelectorAll('#tableBody tr');
	tableRows.forEach((row, index) => {
		var spanElement = row.querySelector('span');
		if (spanElement) {
			spanElement.textContent = index + 1;

			// 部分一致でname属性に "priority" を含む要素を取得
			var priorityInput = row.querySelector('input[name*="priority"]');
			if (priorityInput) {
				priorityInput.value = index + 1;
			}
		}
	});
}


//検索ボタン押下時の処理
$(function() {
	$('#button').bind("click", function() {
		var userid = new RegExp($('#userid').val());
		var username = new RegExp($('#username').val());

		var fromdate = new Date($('#fromdate').val());
		var fromdatestr = ($('#fromdate').val());
		var todate = new Date($('#todate').val());
		var todatestr = ($('#todate').val());


		var adminflag = document.querySelector("#adminflag").value;

		$('#data-table tbody tr').each(function() {
			var useridtxt = "";
			var usernametxt = "";
			var datetxt = "";
			var date = "";


			//管理者権限による検索条件分岐
			if (adminflag == "[ADMIN]") {
				useridtxt = $(this).find("td:eq(0)").html();
				usernametxt = $(this).find("td:eq(1)").html();
				datetxt = $(this).find("td:eq(2)").html();
				date = new Date(datetxt);

			} else {
				datetxt = $(this).find("td:eq(0)").html();
				date = new Date(datetxt);
			}

			//検索項目チェック
			if (useridtxt.match(userid) != null) {
				if (usernametxt.match(username) != null) {

					//日付絞り込み	
					if (fromdate <= todate || (!fromdatestr || !todatestr)) {
						if (fromdate <= date || !fromdatestr) {
							if (date <= todate || !todatestr) {
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
		});
	});

	//すべて表示ボタン押下時の処理
	$('#button2').bind("click", function() {
		$('#userid').val('');
		$('#username').val('');
		$('#fromdate').val('');
		$('#todate').val('');
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
		var task_date = ($('#task_date').val());;
		var clickedButton = event.target;
		let alertShown = false;
		if (clickedButton.id == "save") {
			if (!task_date) {
				alert("日付を入力してください");
				event.preventDefault();
				return false;
			}

			const timeInputs = document.querySelectorAll('.start-time');

			timeInputs.forEach(timeInput => {

				if (alertShown) {
					return; // すでにアラートが表示された場合は処理を終了
				}
				const selectedTime = timeInput.value;
				const startTime = '08:00';
				const endTime = '19:59';

				if (selectedTime != '') {
					if (selectedTime < startTime || selectedTime > endTime) {
						alert('選択できる時間は08:00から19:59までです。');
						event.preventDefault();
						alertShown = true;
						return false;
					}
				} else {
					alert('開始時間を入力してください。');
					event.preventDefault();
					alertShown = true;
					return false;
				}
			});

			const tableBodyRows = document.querySelectorAll('#tableBody tr');
			const tableBody2Rows = document.querySelectorAll('#tableBody2 tr');
			if (tableBodyRows.length == 0) {
				alert('業務予定を入力してください。');
				event.preventDefault();
				return false;
			}




			if (tableBody2Rows.length == 0) {
				alert('業務報告を入力してください。');
				event.preventDefault();
				return false;
			}

			tableBody2Rows.forEach(row => {
				if (alertShown) {
					return; // すでにアラートが表示された場合は処理を終了
				}
				const startTimeInput = row.querySelector(".start-time");
				const endTimeInput = row.querySelector(".end-time");

				const startTimeValue = startTimeInput.value;
				const endTimeValue = endTimeInput.value;

				if (endTimeValue != '') {
					if (startTimeValue > endTimeValue) {
						alert("開始時間が終了時間より遅い時間になっています。");
						event.preventDefault();
						alertShown = true;
						return false;
					}
				}
			});
		}
	});

	//Excel出力ボタン押下時	
	document.getElementById("createExcel").addEventListener("click", function(event) {
		var task_date = ($('#task_date').val());;
		var clickedButton = event.target;
		let alertShown = false;
		if (clickedButton.id == "createExcel") {
			if (!task_date) {
				alert("日付を入力してください");
				event.preventDefault();
				return false;
			}

			const timeInputs = document.querySelectorAll('.start-time');

			timeInputs.forEach(timeInput => {
				if (alertShown) {
					return; // すでにアラートが表示された場合は処理を終了
				}
				const selectedTime = timeInput.value;
				const startTime = '08:00';
				const endTime = '19:59';

				if (selectedTime != '') {
					if (selectedTime < startTime || selectedTime > endTime) {
						alert('選択できる時間は08:00から19:59までです。');
						event.preventDefault();
						alertShown = true;
						return false;
					}
				} else {
					alert('開始時間を入力してください。');
					event.preventDefault();
					alertShown = true;
					return false;
				}
			});


			const tableBodyRows = document.querySelectorAll('#tableBody tr');
			const tableBody2Rows = document.querySelectorAll('#tableBody2 tr');
			if (tableBodyRows.length == 0) {
				alert('業務予定を入力してください。');
				event.preventDefault();
				return false;
			}


			if (tableBody2Rows.length == 0) {
				alert('業務報告を入力してください。');
				event.preventDefault();
				return false;
			}
			tableBody2Rows.forEach(row => {
				if (alertShown) {
					return; // すでにアラートが表示された場合は処理を終了
				}
				const startTimeInput = row.querySelector(".start-time");
				const endTimeInput = row.querySelector(".end-time");

				const startTimeValue = startTimeInput.value;
				const endTimeValue = endTimeInput.value;

				if (endTimeValue != '') {
					if (startTimeValue > endTimeValue) {
						alert("開始時間が終了時間より遅い時間になっています。");
						event.preventDefault();
						alertShown = true;
						return false;
					}
				}
			});

		}
	});
});

//テキストボックスのエンターキーを無効化
function disableEnterKey(event) {
	if (event.keyCode === 13) {
		event.preventDefault();
	}
}
