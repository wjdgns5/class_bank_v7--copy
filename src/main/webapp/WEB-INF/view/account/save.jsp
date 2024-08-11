<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- header.jsp  -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<!-- start of content.jsp(xxx.jsp)   -->
<div class="col-sm-8">
	<h2>계좌생성 (인증)</h2>
	<h5>Bank App에 오신걸 환영합니다</h5>
	<!-- 예외적으로 로그인은 보안 때문에 post로 던지자 -->
	<form action="/account/save" method="post"> 
		<div class="form-group">
			<label for="number">계좌번호:</label>
			<input type="text" class="form-control" placeholder="Enter number" id="number" name="number" value="1002-1234"  >
		</div>
		
		<div class="form-group">
			<label for="pwd">비밀번호:</label>
			<input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="1234">
		</div>
		
		<div class="form-group">
			<label for="balance">잔액:</label>
			<input type="password" class="form-control" placeholder="Enter balance" id="balance" name="balance" value="10000">
		</div>
		
		<button type="submit" class="btn btn-primary">계좌 생성</button>
	</form>
</div>
<!-- end of col-sm-8  -->
</div>
</div>
<!-- end of content.jsp(xxx.jsp)   -->

<!-- footer.jsp  -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>