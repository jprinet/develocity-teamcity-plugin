<%@ include file="/include.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${buildScans.size()>0}">
        <ul>
            <c:forEach items="${buildScans.all()}" var="buildScan">
                <li style="list-style: none"><a href="${buildScan.url}" target="_blank"><img
                    src="${buildScan.buildScanBadge}" alt="${buildScan.urlWithoutProtocol}"></a></li>
            </c:forEach>
        </ul>
    </c:when>
</c:choose>
