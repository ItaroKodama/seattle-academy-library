/** 
 * 一括削除時に選択したファイルを配列に格納 
*/

$(function() {
	let vals = [];
	$(".checkbox").on('change', function() {
		//チェックボックスがチェックされたら配列に追加
		if (this.checked) {
			vals.push($(this).val());
		}
		//チェックボックスのチェックが外されたら配列から削除
		if (!this.checked) {
			vals.splice(vals.indexOf($(this).val()), 1);
		}
	});

	//削除ボタンを押したら、削除する書籍のリストをinputタグのvalueに設定
	$(".btn_bulkDelete").on("click", function() {
		if (!confirm('本当に削除しますか？\n書籍ID：' + vals)) {
        return false;
      }else{
		$("#delete_books").val(vals);
	}
	});
}); 