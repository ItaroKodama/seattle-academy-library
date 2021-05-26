//appends an "active" class to .popup and .popup-content when the "Open" button is clicked
$(function() {
	//検索ポップアップの表示
	$(".btn_search_book").on("click", function() {
		$(".popup_overlay, .popup_search").addClass("active");
	});
	
	//並び替えポップアップの表示
	$(".btn_sort_book").on("click", function(){
		$(".popup_overlay, .popup_sort").addClass("active");
	});

	//ポップアップのオーバーレイをクリックした時にポップアップを閉じる 
	$(".popup_overlay").on("click", function(e) {
		if(!$(e.target).closest(".popup_search").length
		 && !$(e.target).closest(".popup_sort").length){
			$(".popup_overlay, .popup_search, .popup_sort").removeClass("active");
		}
	});

	//closeボタンをクリックした時にポップアップを閉じる 
	$(".close").on("click", function(){
		$(".popup_overlay, .popup_search, .popup_sort").removeClass("active");
	});
});