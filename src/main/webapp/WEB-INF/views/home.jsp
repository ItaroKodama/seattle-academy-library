<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=utf8"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>ホーム｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="resources/js/popup.js"></script>
</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li><a href="<%=request.getContextPath()%>/home" class="menu">Home</a></li>
                <li><a href="<%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <h1>Home</h1>
        <div class=btn_list>
            <a href="<%=request.getContextPath()%>/addBook" class="btn_add_book">書籍の追加</a>
            <!--Creates the popup body-->
            <div class="popup_overlay">
                <!--Creates the popup content-->
                <div class="popup_content">
                    <h2>書籍の検索</h2>
                    <form action="<%=request.getContextPath()%>/searchBooks" method="post" enctype="multipart/form-data" id="data_upload_form">
                        <div class="search_item">
                            <span>書籍名：</span> <input type="radio" name="titleMatching" value="true" checked>部分一致 <input type="radio" name="titleMatching" value="false">完全一致 <input type="text" name="searchTitle" class="search_word">
                        </div>
                        <div class="search_item">
                            <span>著者名：</span> <input type="radio" name="authorMatching" value="true" checked>部分一致 <input type="radio" name="authorMatching" value="false">完全一致 <input type="text" name="searchAuthor" class="search_word">
                        </div>
                        <div class="search_item">
                            <span>出版社：</span> <input type="radio" name="publisherMatching" value="true" checked>部分一致 <input type="radio" name="publisherMatching" value="false">完全一致 <input type="text" name="searchPublisher" class="search_word">
                        </div>
                        <div class="search_item">
                            <span>出版日：</span> <input type="radio" name="publishDateMatching" value="true" checked>部分一致 <input type="radio" name="publishDateMatching" value="false">完全一致 <input type="text" name="searchPublishDate" class="search_word">
                        </div>
                        <div class="addBookBtn_box">
                            <button type="submit" id="add-btn" class="btn_addBook">検索</button>
                        </div>
                    </form>
                    <!--popup's close button-->
                    <button class="close">Close</button>
                </div>
            </div>
            <!--Content shown when popup is not displayed-->
            <button class="open btn_search_book">書籍の検索</button>
        </div>
        <div class="content_body">
            <div class="result_message">
                <c:if test="${!empty noBook}">
                    <div class="error_msg">${noBook}</div>
                </c:if>
                <c:if test="${!empty search_word}">
                    ${search_word}&emsp;&emsp;&emsp;
                    <a href="<%=request.getContextPath()%>/home">検索結果のクリア</a>
                </c:if>
            </div>
            <div class="booklist">
                <c:forEach var="bookInfo" items="${bookList}">
                    <div class="books">
                        <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                            <a href="javascript:void(0)" onclick="this.parentNode.submit();"> <c:if test="${empty bookInfo.thumbnail}">
                                    <img class="book_noimg" src="resources/img/noImg.png">
                                </c:if> <c:if test="${!empty bookInfo.thumbnail}">
                                    <img class="book_noimg" src="${bookInfo.thumbnail}" alt="NO IMAGE">
                                </c:if>
                            </a> <input type="hidden" name="bookId" value="${bookInfo.bookId}">
                        </form>
                        <ul>
                            <li class="book_title">${bookInfo.title}</li>
                            <li class="book_author">${bookInfo.author}</li>
                            <li class="book_publisher">出版社：${bookInfo.publisher}</li>
                            <li class="book_publish_date">出版日：${bookInfo.publishDate}</li>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>
</body>
</html>
