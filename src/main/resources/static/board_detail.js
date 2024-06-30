const delete_elements = document.getElementsByClassName("delete");
Array.from(delete_elements).forEach(function(e) {
	e.addEventListener('click', function() {
		if (confirm("정말로 삭제하시겠습니까?")) {
			location.href = this.dataset.uri;
		}
	});
});
const recommend_elements = document.getElementsByClassName("recommend");
Array.from(recommend_elements).forEach(function(e) {
	e.addEventListener('click', function() {
		if (confirm("정말로 추천하시겠습니까?")) {
			location.href = this.dataset.uri;
		}
	})
});
const answer_elements = document.getElementsByClassName("page-link");
Array.from(answer_elements).forEach(function(e) {
	e.addEventListener('click', function() {
		document.getElementById('page').value = this.dataset.page;
		document.getElementById('searchForm').submit();
	});
});

$("[role='comment_board_list'],[role='comment_answer_list']").off().on('click', function() {
	var _cnt = 0;
	var _area = "";
	var _tmpArr = this.dataset.uri.split("/");
	var _page = _tmpArr.pop();
	if (this.role == "comment_board_list") {
		var _wrtieArea = $("[class='comment_board_write']");
		if (_wrtieArea.css("display") == "none") {
			_wrtieArea.show();
		} else {
			_wrtieArea.hide();
		}
		_area = $("[class='comment_board_list']");
		_cnt = $("[role='board_commant_cnt']").text();
	} else {
		var _wrtieArea = $(this).parent().next();
		if (_wrtieArea.css("display") == "none") {
			_wrtieArea.show();
		} else {
			_wrtieArea.hide();
		}
		_area = $(this).parent().parent().find(".comment_answer_list");
		_cnt = this.childNodes[1].textContent;
	}

	if (_area.css("display") != "none" || _cnt == "0") {
		_area.hide();
		return;
	}

	var str = this.dataset.uri;
	var lastSlashIndex = str.lastIndexOf('/');

	if (lastSlashIndex !== -1) {
		// 마지막 슬래시 뒤의 부분을 제거
		var prevUri = str.substring(0, lastSlashIndex);
	}

	$.ajax({
		type: "GET",
		url: this.dataset.uri,
		dataType: "json",
		success: function(data, status) {
			var _html = '';
			_html += drawComment(data);
			_html += pagination(_cnt, parseInt(_page), prevUri);

			_area.show();
			_area.html(_html);

			pageButtons();

		},
		error: function(xhr, status, error) {
			console.log(error);
		}
	});
});


function parseDateTime(datetimeString) {
	var _tmp = new Date(datetimeString);
	var _year = _tmp.getFullYear();
	var _month = _tmp.getMonth() < 10 ? "0" + _tmp.getMonth() : _tmp.getMonth();
	var _date = _tmp.getDate() < 10 ? "0" + _tmp.getDate() : _tmp.getDate();
	var _hours = _tmp.getHours() < 10 ? "0" + _tmp.getHours() : _tmp.getHours();
	var _minutes = _tmp.getMinutes() < 10 ? "0" + _tmp.getMinutes() : _tmp.getMinutes();
	var _result = _year + "-" + _month + "-" + _date + " " + _hours + ":" + _minutes;
	return _result;
}

function movePage(uri, page, cnt, area) {

	var str = uri;
	var lastSlashIndex = str.lastIndexOf('/');

	if (lastSlashIndex !== -1) {
		// 마지막 슬래시 뒤의 부분을 제거
		var prevUri = str.substring(0, lastSlashIndex);
	}
	$.ajax({
		type: "GET",
		url: uri,
		dataType: "json",
		success: function(data, status) {
			var _html = '';
			_html += drawComment(data);
			_html += pagination(cnt, parseInt(page), prevUri);

			area.show();
			area.html(_html);

			pageButtons();
		},
		error: function(xhr, status, error) {

		}
	});
}

function drawComment(data) {
	var _html = "";
	for (var i = 0; i < data.length; i++) {
		var _date = parseDateTime(data[i].createDate);
		_html += '<div class=card my-3>';
		_html += '<a th:id="|comment_' + data[i].id + '|">';
		_html += '<div class="card-body">';
		_html += '<div class="card-text">' + data[i].content + '</div>';
		_html += '<div class="d-flex justify-content-end">';
		_html += '<div class="badge bg-light text-dark p-2 text-start">';
		_html += '<div class="mb-2">';
		_html += '<span>' + data[i].userName + '</span>';
		_html += '</div>';
		_html += '<div>' + _date + '</div>';
		_html += '</div>';
		_html += '</div>';
		_html += '</div>';
		_html += '</div>';
	}
	return _html;
}

function pagination(cnt, page, uri) {
	// 네비게이션에 표시되는 페이지 수
	var _pkg = 5;
	// 리스트에 표시되는 수
	var _listcnt = 3;
	// 네비게이션에 표시할 맨처음페이지
	var _start = (Math.floor(page / _pkg) * _pkg) + 1
	var _tmp = Math.ceil(cnt / _listcnt);
	var _end = 0;

	if (_start + (_pkg - 1) >= _tmp) {
		_end = _tmp;
	} else {
		_end = _start + (_pkg - 1);
	}
	var _html = '<div role="pagination" data-uri="' + uri + '" data-page="' + page + '" data-cnt="' + cnt + '">';
	_html += '<ul class="pagination justify-content-center">';
	if (page + 1 == _start) {
		_html += '<li class="page-item disabled" style="cursor:pointer">';
	} else {
		_html += '<li class="page-item" style="cursor:pointer">';
	}
	_html += '<a class="page-link" role="comment-prev-page"><span>이전</span></a>';
	_html += '</li>';

	for (var i = _start; i <= _end; i++) {
		if (page + 1 == i) {
			_html += '<li class="page-item active" style="cursor:pointer">';
		} else {
			_html += '<li class="page-item" style="cursor:pointer">';
		}
		_html += '<a class="page-link" role="comment-page-link">' + i + '</a>';
		_html += '</li>';
	}

	if (page + 1 == _end) {
		_html += '<li class="page-item disabled" style="cursor:pointer">';
	} else {
		_html += '<li class="page-item" style="cursor:pointer">';
	}
	_html += '<a class="page-link" role="comment-next-page"><span>다음</span></a>';
	_html += '</li>';
	_html += '</ul>';
	_html += '</div>';
	return _html;
}
function moveButton(uri, page, cnt) {
	uri = uri + "/" + page;
	var _area = "";
	if (uri.split("/")[3] == "q") {
		_area = $("[class='comment_board_list']");
	} else {
		_area = $(this).closest(".comment_answer_list");
	}
	movePage(uri, page, cnt, _area);
}


function pageButtons() {
	$("[role='comment-prev-page']").off().on("click", function() {
		var _data = $($(this).closest("div")).data();
		var _uri = _data.uri;
		var _prevPage = _data.page;
		var _cnt = _data.cnt;
		var _page = parseInt(_prevPage) - 1;
		moveButton(_uri, _page, _cnt);
	});

	$("[role='comment-next-page']").off().on("click", function() {
		var _data = $($(this).closest("div")).data();
		var _uri = _data.uri;
		var _prevPage = _data.page;
		var _cnt = _data.cnt;
		var _page = parseInt(_prevPage) + 1;
		moveButton(_uri, _page, _cnt);
	});

	$("[role='comment-page-link']").off().on("click", function() {
		debugger;
		var _data = $($(this).closest("div")).data();
		var _uri = _data.uri;
		var _cnt = _data.cnt;
		var _page = parseInt($(this).text())-1;
		moveButton(_uri, _page, _cnt);
	});
}
