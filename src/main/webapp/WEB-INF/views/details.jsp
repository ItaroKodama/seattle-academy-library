<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta charset="UTF-8">
<title>書籍の詳細｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="resources/css/lightbox.css">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="resources/js/lightbox.js"></script>
<script src="resources/js/buttonInactive.js"></script>
<script src="resources/js/session.js"></script>
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
        <h1>書籍の詳細</h1>
        <div class="content_body detail_book_content">
            <div class="content_left">
                <span>書籍の画像</span>
                <div class="book_thumnail">
                    <c:if test="${bookDetailsInfo.thumbnailUrl != 'null'}">
                        <a href="${bookDetailsInfo.thumbnailUrl}" data-lightbox="image-1">
                            <img class="book_noimg" src="${bookDetailsInfo.thumbnailUrl}" alt="NO IMAFE">
                        </a>
                    </c:if>
                    <c:if test="${bookDetailsInfo.thumbnailUrl == 'null'}">
                        <a href="resources/img/noImg.png" data-lightbox="image-1"> 
                            <img class="book_noimg" src="resources/img/noImg.png">
                        </a>
                    </c:if>
                </div>
                <c:choose>
                    <c:when test="${bookDetailsInfo.borrowing}">
                        <p class="borrowing_status">貸し出し中</p>
                    </c:when>
                    <c:otherwise>
                        <p class="borrowing_status">貸し出し可</p>
                    </c:otherwise>
                </c:choose>
                <input type="hidden" class="borrowing_user" value="${bookDetailsInfo.borrowingUserName}">
            </div>
            <div class="content_right">
                <div>
                    <span>書籍名</span>
                    <p>${bookDetailsInfo.title}</p>
                </div>
                <div>
                    <span>著者名</span>
                    <p>${bookDetailsInfo.author}</p>
                </div>
                <div>
                    <span>出版社</span>
                    <p>${bookDetailsInfo.publisher}</p>
                </div>
                <div>
                    <span>出版日</span>
                    <p>${bookDetailsInfo.publish_date}</p>
                </div>
                <div>
                    <span>ISBN</span>
                    <p>${bookDetailsInfo.isbn}</p>
                </div>
                <div>
                    <span>説明文</span>
                    <p>${bookDetailsInfo.description}</p>
                </div>
            </div>
        </div>
        <c:if test="${!empty borrowingHistory}">
            <ul class="borrowingHistory">
                <li class="column_name">アカウント名</li>
                <li class="column_name">貸出日時</li>
                <li class="column_name">返却日時</li>
                <c:forEach var="borrowingHistory" items="${borrowingHistory}">
                    <li>${borrowingHistory.accountName}</li>
                    <li>${borrowingHistory.borrowingDate}</li>
                    <li>${borrowingHistory.returnDate}</li>
                </c:forEach>
            </ul>
        </c:if>
        <div class="edtDelBookBtn_box">
            <form method="post" action="rentBook">
                <button type="submit" value="${bookDetailsInfo.bookId}" name="bookId" class="btn_rentBook">借りる</button>
                <input type="hidden" class="account_name" name="account_name">
            </form>
            <form method="post" action="returnBook">
                <button type="submit" value="${bookDetailsInfo.bookId}" name="bookId" class="btn_returnBook">返す</button>
            </form>
            <form method="post" action="editBook">
                <button type="submit" value="${bookDetailsInfo.bookId}" name="bookId" class="btn_editBook">編集</button>
            </form>
            <form method="post" action="deleteBook">
                <button type="submit" value="${bookDetailsInfo.bookId}" name="bookId" class="btn_deleteBook">削除</button>
            </form>
            <form method="post" action="borrowingHistory">
                <button type="submit" value="${bookDetailsInfo.bookId}" name="bookId" class="btn_borrowingHistory">貸出履歴</button>
            </form>
        </div>
    </main>
</body>
</html>
