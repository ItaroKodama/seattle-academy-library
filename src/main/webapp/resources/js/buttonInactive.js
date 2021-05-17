/**
 * 
 */

$(function(){
	let str = $('.borrowing_status').text();
	if($('.borrowing_status').text() === '貸し出し中'){
		$('.btn_rentBook').prop('disabled', true);
		$('.btn_deleteBook').prop('disabled', true);
	}
	if($('.borrowing_status').text() === '貸し出し可'){
		$('.btn_returnBook').prop('disabled', true);
	}	
});