<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign In</title>

    <!-- Linking CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">

    <!-- Linking JS -->
    <script src="${pageContext.request.contextPath}/js/loginValidate.js" defer></script>
</head>
<body class="gradient-custom-3">

    <div class="container gradient-custom-4">

        <div id="logout-message"></div>

        <h2>Sign in</h2>
        <form id="loginForm" action="/login" method="post" onsubmit="return validateLoginForm(event);">

            <div class="form-group">
                <label for="email">Email address</label>
                <input type="email" id="email" name="email" class="form-control" required>
                <div id="error-message" style="color: red; display: none;">
                    <span id="error-text"></span> <!-- Display error message -->
                </div>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control" required>
            </div>

            <div class="form-group">
                <a href="#!" class="forgot-password">Forgot password?</a>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign in</button>

            <div class="text-center">
                <p>Not a member? <a href="${pageContext.request.contextPath}/register">Register</a></p>
            </div>
        </form>
    </div>

</body>
</html>
