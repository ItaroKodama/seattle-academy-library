$(function(){
	//ログイン画面でemailをセッションに保存
	$('.primary').on('click', function(){
		sessionStorage.setItem('accountName', $('#email').val());
	});
	
	//書籍を借りる際にアカウント情報（email）をセッションから取得
	$(function(){
		$('.accountName').val(sessionStorage.getItem('accountName'));
	});
});
