$(function(){
	const borrowingStatus = $('.borrowing_status').text();
	if(borrowingStatus === '貸し出し中'){
		$('.btn_rentBook').prop('disabled', true);
		$('.btn_deleteBook').prop('disabled', true);
		if($('.borrowing_user').val() != sessionStorage.getItem('account_name')){
			$('.btn_returnBook').prop('disabled', true);
		}
	}
	if(borrowingStatus === '貸し出し可'){
		$('.btn_returnBook').prop('disabled', true);
	}	
});