<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:c="http://java.sun.com/jsp/jstl/core" version="2.0">
	<c:if test="${not empty info}">
		<div class="alert-message info" data-dismiss="alert">
			<a class="close" href="#">×</a>
			<p>
				<strong><spring:message code="${info.code}"	arguments="${info.arguments}" />
				</strong>
			</p>
    		<jsp:scriptlet>session.removeAttribute("info");</jsp:scriptlet>
		</div>
	</c:if>
</jsp:root>
