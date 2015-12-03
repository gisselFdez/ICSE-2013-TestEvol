<%@ page session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>

<%@include file="includes/header.jsp"%>

</head>

<body>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="row-fluid">
				<div class="span12 center login-header">
					<h2>Welcome to TestEvol</h2>
				</div>
				<!--/span-->
			</div>
			<!--/row-->

			<div class="row-fluid">
				<div class="well span5 center login-box">
						<div id="up" class="alert alert-info">Please login with your
							Username and Password.
						</div>
						<div id="err" class="alert alert-error" style="display:none">
							Your login attempt was not successful, try again.
						</div>

						<div id="request" class="alert alert-success" style="display:none">
							Your request has been accepted. You will soon receive an email with your credentials.
						</div>
						<div id="tool" class="alert alert-success" style="display:none">
							Your request has been accepted. You will soon receive an email with a link to the tool.
						</div>
					<form class="form-horizontal"
						 action="<c:url value="j_spring_security_check"/>" method="post">
						<fieldset>
							<div class="input-prepend" title="Username" data-rel="tooltip">
								<span class="add-on"><i class="icon-user"></i></span><input
									autofocus class="input-large span10" name="j_username"
									id="username" type="text" placeholder="username" />
							</div>
							<div class="clearfix"></div>

							<div class="input-prepend" title="Password" data-rel="tooltip">
								<span class="add-on"><i class="icon-lock"></i></span><input
									class="input-large span10" name="j_password" id="password"
									type="password" placeholder="password" />
							</div>
							<div class="clearfix"></div>

							<!-- 
							<div class="input-prepend">
								<label class="remember"><a href="projects/list">Guest
										login</a></label>
							</div> -->
							<div class="clearfix"></div>

							<p class="center span5">
								<input type="submit" class="btn btn-primary" value="Login">
								<input type="button" class="btn btn-setting" value="Request Account">
								<input type="button" class="btn" onclick="$('#toolModal').modal('show')" value="Request Tool">
							</p>
						</fieldset>
					</form>
				</div>
				<!--/span-->
			</div>
			<!--/row-->
		</div>
		<!--/fluid-row-->
	</div>
	<!--/.fluid-container-->
		<div class="modal hide fade" id="myModal" style="width: 360px;">
			<form action='<c:url value="/account"/>'>
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><i class="icon-remove"></i></button>
				<h3>Request Account</h3>
			</div>
			<div class="modal-body center">
				<div class="input-prepend" title="Enter you e-mail address" data-rel="tooltip">
					<span class="add-on"><i class="icon-user"></i></span><input autofocus class="input-large" name="username" id="username" type="text" placeholder="Enter your e-mail address" />
				</div>
			</div>
			<div class="modal-footer">
				<a href="#" class="btn" data-dismiss="modal" id="close_login">Close</a>
				<input type="submit" class="btn btn-info" value="Request"/>
			</div>
			</form>
		</div>
<div class="modal hide fade" id="toolModal" style="width: 360px;">
			<form action='<c:url value="/account/tool"/>'>
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><i class="icon-remove"></i></button>
				<h3>Request Tool</h3>
			</div>
			<div class="modal-body center">
				<div class="input-prepend" title="Enter you e-mail address" data-rel="tooltip">
					<span class="add-on"><i class="icon-user"></i></span><input autofocus class="input-large" name="username" id="username" type="text" placeholder="Enter your e-mail address" />
				</div>
			</div>
			<div class="modal-footer">
				<a href="#" class="btn" data-dismiss="modal" id="close_login">Close</a>
				<input type="submit" class="btn btn-info" value="Request"/>
			</div>
			</form>
		</div>
	<!-- 	<script type="text/javascript" src="<c:url value="/projects/version/script"/>"></script>
 -->
	<script>
		var load_projects = false;
		if(window.location.search.indexOf('accessRequested=true') >= 0){
			document.getElementById("up").style.display='none';
			document.getElementById("request").style.display='block';
		}
		if(window.location.search.indexOf('toolRequested=true') >= 0){
			document.getElementById("up").style.display='none';
			document.getElementById("tool").style.display='block';
		}
	</script>
	<%@include file="includes/scripts.jsp"%>
	<script type="text/javascript">
	if(mustShowFeedbackMessage() && !successExecution()) {
		$("#up").hide();
		$("#err").show();
	}

	
	</script>
</body>
</html>