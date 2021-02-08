<%@ page import="ga.gaba.EsxiStatus.ListVMVimAction" %><%--
  Created by IntelliJ IDEA.
  User: glyczak
  Date: 3/30/19
  Time: 2:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%
    ListVMVimAction action = new ListVMVimAction();
    action.execute();
%>
<c:set var="vms" value="<%= action.getVMContent() %>"/>
<html>
<head>
    <title>VM Power Panel</title>
</head>
<body>
<form action="power.do" method="post">
    <select name="vm">
        <c:forEach var="vm" items="${vms}">
            <option value="${vm.obj.value}">
                    ${vm.getProp("name")}
            </option>
        </c:forEach>
    </select>
    <input type="radio" id="suspended-state-radio-button"
           name="state" value="suspended" checked>
    <label for="suspended-state-radio-button">
        Suspend
    </label>
    <input type="radio" id="powered-off-state-radio-button"
           name="state" value="poweredOff" checked>
    <label for="powered-off-state-radio-button">
        Power Off
    </label>
    <input type="radio" id="powered-on-state-radio-button"
           name="state" value="poweredOn" checked>
    <label for="powered-on-state-radio-button">
        Power On
    </label>
    <input type="hidden" name="redirect" value="power.jsp?message=VM power state successfully changed.">
    <input type="submit" value="Change State" />
</form>
<c:if test='<%= request.getParameter("message") != null %>'>
    <script type="text/javascript">
        alert('${param["message"]}');
    </script>
</c:if>
</body>
</html>