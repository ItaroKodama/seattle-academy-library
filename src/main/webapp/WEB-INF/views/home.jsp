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
<script src="resources/js/list.js"></script>
</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li><a href=" <%=request.getContextPath()%>/home" class="menu">Home</a></li>
                <li><a href=" <%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <h1>Home</h1>
        <a href="<%= request.getContextPath()%>/addBook" class="btn_add_book">書籍の追加</a> 
        <a href="<%= request.getContextPath()%>/addBooksBulk" class="btn_bulk_book">一括登録</a>
        <form action="<%=request.getContextPath()%>/deleteBooksBulk" method="post" enctype="multipart/form-data" id="data_upload_form">
            <button type="submit" class="btn_delete_book">書籍の削除</button>
            <input type="hidden" id="delete_books" name="deleteBookList">
        </form>
        <c:if test="${!empty cannotDelete}">
            <div class="error_msg">${cannotDelete}</div>
        </c:if>
        <c:if test="${!empty deleteMessage}">
            <div class="error_msg">${deleteMessage}</div>
        </c:if>
        <div class="content_body">
            <c:if test="${!empty noBook}">
                <div class="error_msg">${noBook}</div>
            </c:if>
            <div>
                <div class="booklist">
                    <c:forEach var="bookInfo" items="${bookList}">
                        <div class="books">
                            <input type="checkbox" value="${bookInfo.bookId}" class="checkbox">
                            <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                                <a href="javascript:void(0)" onclick="this.parentNode.submit();">
                                    <c:if test="${bookInfo.thumbnail == 'null'}">
                                        <img class="book_noimg" src="resources/img/noImg.png">
                                    </c:if> <c:if test="${bookInfo.thumbnail != 'null'}">
                                        <img class="book_noimg" src="${bookInfo.thumbnail}" alt="NO IMAGE">
                                    </c:if>
                                </a> 
                                <input type="hidden" name="bookId" value="${bookInfo.bookId}">
                            </form>
                            <ul>
                                <li class="book_title">${bookInfo.title}</li>
                                <li class="book_author">${bookInfo.author}（著）</li>
                                <li class="book_publisher">出版社：${bookInfo.publisher}</li>
                                <li class="book_publish_date">出版日：${bookInfo.publishDate}</li>
                            </ul>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
