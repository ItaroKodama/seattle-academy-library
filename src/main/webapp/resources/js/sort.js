$(function() {
	$(".sort_books").on("click", function() {
		let books = [];
		$('.books').each(function(i) {
			books[i] = {
				title: $(this).find(".book_title").text(),
				author: $(this).find(".book_author .wordOverflow").text(),
				publisher: $(this).find(".book_publisher .wordOverflow").text(),
				publish_date: $(this).find(".book_publish_date").text(),
				source: $(this).html() //.booksクラスのhtmlコード
				};
		});

		const sortByWhat = $(".sortByWhat").val();
		const isAsc = $(".howSort:eq(0)").prop("checked");

		if (sortByWhat == "title") {
			if (isAsc) {
				books.sort(
					function(a, b) {
						if (a.title < b.title) return -1;
						if (a.title > b.title) return 1;
						return 0;
					});
			} else {
				books.sort(
					function(a, b) {
						if (a.title > b.title) return -1;
						if (a.title < b.title) return 1;
						return 0;
					});
			}
		}

		if (sortByWhat == "author") {
			if (isAsc) {
				books.sort(
					function(a, b) {
						if (a.author < b.author) return -1;
						if (a.author > b.author) return 1;
						return 0;
					});
			} else {
				books.sort(
					function(a, b) {
						if (a.author > b.author) return -1;
						if (a.author < b.author) return 1;
						return 0;
					});
			}
		}

		if (sortByWhat == "publisher") {
			if (isAsc) {
				books.sort(
					function(a, b) {
						if (a.publisher < b.publisher) return -1;
						if (a.publisher > b.publisher) return 1;
						return 0;
					});
			} else {
				books.sort(
					function(a, b) {
						if (a.publisher > b.publisher) return -1;
						if (a.publisher < b.publisher) return 1;
						return 0;
					});
			}
		}

		if (sortByWhat == "publishDate") {
			if (isAsc) {
				books.sort(
					function(a, b) {
						if (a.publish_date < b.publish_date) return -1;
						if (a.publish_date > b.publish_date) return 1;
						return 0;
					});
			} else {
				books.sort(
					function(a, b) {
						if (a.publish_date > b.publish_date) return -1;
						if (a.publish_date < b.publish_date) return 1;
						return 0;
					});
			}
		}

		//配列を出力する
		for (var j = 0; j < books.length; j++) {
			$(".books").eq(j).html(books[j].source);
		}
	})
});