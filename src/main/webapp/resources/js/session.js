$(function(){
	//ログイン画面でemailをセッションに保存
	$('.primary').on('click', function(){
		sessionStorage.setItem('account_name', $('#email').val());
	});
	
	//書籍を借りる際にアカウント情報（email）をセッションから取得
	$(function(){
		$('.account_name').val(sessionStorage.getItem('account_name'));
	});
});
