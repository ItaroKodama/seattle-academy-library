/**
 * */

$(function() {
	var vals = [];
		$(".checkbox").on('change', function() {
		//チェックボックスがチェックされたら配列に追加
		if (this.checked) {
			vals.push($(this).val());
		}
		//チェックボックスのチェックが外されたら配列から削除
		if (!this.checked) {
			vals.splice(vals.indexOf($(this).val()),1);
		}
	});

	//削除ボタンを押したら、削除する書籍のリストをinputタグのvalueに設定
	$(".btn_delete_book").on("click", function() {
		$("#delete_books").val(vals);
	});
});