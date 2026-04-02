<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign-up Form</title>

    <!-- Linking CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Linking JS -->
    <script src="${pageContext.request.contextPath}/js/validate.js" defer></script>
</head>
<body class="gradient-custom-3">

<!-- Success message box -->
<div id="success-message" class="successMessageBox"></div>

<div class="container gradient-custom-4">
    <h2>Create an account</h2>
    <form id="myForm" action="/register" method="post">

        <!-- Name Field -->
        <div class="form-group">
            <label for="name">Your Name</label>
            <input type="text" id="name" name="name" value="${user.name}" required>
        </div>

        <!-- Email Field -->
        <div class="form-group">
            <label for="email">Your Email</label>
            <input type="email" id="email" name="email" value="${user.email}" required>
            <span id="email-error" style="color: red; display: none;"></span>
        </div>

        <!-- Password Field -->
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" value="${user.password}" required>
            <span id="password-error" style="color: red; display: none;"></span>
        </div>

        <!-- Confirm Password Field -->
        <div class="form-group">
            <label for="confirmPassword">Repeat your password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" value="${user.confirmPassword}" required>
            <span id="confirm-password-error" style="color: red; display: none;"></span>
        </div>

        <!-- Terms Agreement -->
        <div class="form-group1">
            <input type="checkbox" id="terms" required>
            <label for="terms">I agree to the <a href="#">Terms of Service</a></label>
        </div>

        <!-- Register Button -->
        <button type="submit" id="registerButton" disabled>Register</button>

        <p>Already have an account? <a href="${pageContext.request.contextPath}/">Login here</a></p>
    </form>
</div>

</body>
</html>
