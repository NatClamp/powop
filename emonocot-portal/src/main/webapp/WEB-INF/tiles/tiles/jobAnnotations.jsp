<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:em="http://e-monocot.org/portal/functions"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags"
	xmlns:spring="http://www.springframework.org/tags" version="2.0">

	<div class="content">
		<div class="page-header">
			<h2 id="page-title">${source.identifier}</h2>
		</div>
		<div class="row">
		 <div id="pages" class="span8">				
				<tags:results pager="${result}"/>
		  </div>
		  <div class="pagination">
				<tags:pagination pager="${result}" url="messages"/>
		  </div>
		</div>
		<div class="row">
			<h5>
				<spring:message code="job.number" arguments="${job.jobId}" />
			</h5>
			<table class="zebra-striped">
				<thead>
					<tr>
						<th><spring:message code="jobExecution.startTime" /></th>
						<th><spring:message code="jobExecution.duration" /></th>
						<th><spring:message code="jobExecution.status" /></th>
						<th><spring:message code="jobExecution.exitStatus.exitCode" />
						</th>
						<th><spring:message
								code="jobExecution.exitStatus.exitDescription" /></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${em:formatDate(job.startTime)}</td>
						<td>${em:formatPeriod(job.startTime,job.endTime)}</td>
						<td>${job.status}</td>
						<td>${job.exitStatus.exitCode}</td>
						<td>${job.exitStatus.exitDescription}</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="row">
		    <h5><spring:message code="issues"/></h5>
			<table class="zebra-striped">
				<thead>
					<tr>
						<th><spring:message code="category" />
						</th>
						<th><spring:message code="issue.code" />
						</th>
						<th><spring:message code="issue.message" />
						</th>
						<th><spring:message code="related.object" />
						</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="annotation" items="${result.records}">
						<tr>
							<td>${annotation.type}</td>
							<td>${annotation.code}</td>
							<td>${annotation.text}</td>
							<c:set var="annotatedObj"
								value="${em:deproxy(annotation.annotatedObj)}" />
							<td><c:choose>
									<c:when test="${annotatedObj.class.simpleName eq 'Taxon'}">
										<jsp:element name="a">
											<jsp:attribute name="href">
												<c:url value="/taxon/${annotatedObj.identifier}" />
											</jsp:attribute>
				              ${annotatedObj.name}
				            </jsp:element>
									</c:when>
									<c:when test="${annotatedObj.class.simpleName eq 'Image'}">
										<jsp:element name="a">
											<jsp:attribute name="href">
												<c:url value="/image/${annotatedObj.identifier}" />
											</jsp:attribute>
				              ${annotatedObj.caption}
				            </jsp:element>
									</c:when>
									<c:when test="${not empty annotatedObj}">
				            ${annotatedObj.class.name} : ${annotatedObj.identifier}
				          </c:when>
									<c:otherwise />
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</jsp:root>