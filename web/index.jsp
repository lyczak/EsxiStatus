<%@ page import="ga.gaba.EsxiStatus.ListVMVimAction" %>
<%--
  Created by IntelliJ IDEA.
  User: glyczak
  Date: 12/8/18
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%
    ListVMVimAction action = new ListVMVimAction();
    action.execute();
%>
<c:set var="vmNames" value="<%= action.getPoweredVMNames() %>"/>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <ul>
      <c:forEach var="vmName" items="${vmNames}">
          <li>
              ${vmName}
          </li>
      </c:forEach>
  </ul>
  </body>
</html>
