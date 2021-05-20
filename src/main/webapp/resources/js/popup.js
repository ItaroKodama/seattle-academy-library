//appends an "active" class to .popup and .popup-content when the "Open" button is clicked
$(function() {
	$(".open").on("click", function() {
		$(".popup_overlay, .popup_content").addClass("active");
	});

	//ポップアップのオーバーレイをクリックした時にポップアップを閉じる 
	$(".popup_overlay").on("click", function(e) {
		if(!$(e.target).closest(".popup_content").length){
		$(".popup_overlay, .popup_content").removeClass("active");
		}
	});

	//closeボタンをクリックした時にポップアップを閉じる 
	$(".close").on("click", function(){
		$(".popup_overlay, .popup_content").removeClass("active");
	});
});