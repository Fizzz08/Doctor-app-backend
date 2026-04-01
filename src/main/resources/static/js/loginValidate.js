async function validateLoginForm(event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorMessage = document.getElementById("error-message");
    errorMessage.style.display = "none";

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.
            stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            console.error("Login failed:", data);
            errorMessage.style.display = "block";
            errorMessage.textContent = data.error || "Login failed";
            return;
        }

        if (data.token) {
            // Store token securely
            localStorage.setItem("token", data.token);
            console.log("Token stored:", data.token);

            // Retrieve token from sessionStorage
            const storedToken = localStorage.getItem("token");
            console.log("Retrieved token:", storedToken);

            if (!storedToken) {
                console.error("No token found in sessionStorage");
                return; // Stop execution if no token is found
            }

            const homeResponse = await fetch("http://localhost:8080/Home", {  // Use await here
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + storedToken
                }
            });
            console.log("home resonse"+homeResponse);
            const homeData = await homeResponse.json();
            console.log("home data is"+homeData);

            if (homeResponse.ok) {
                console.log("Home page access granted");
                window.location.href = "/Home";  // Ensure this is the correct path for redirection
            } else {
                throw new Error("Failed to access Home (Token Issue)");
            }
        }
    } catch (error) {
        console.error("Error:", error);
    }
}


// Log-out message handling
document.addEventListener("DOMContentLoaded", function () {
    const logoutMessage = sessionStorage.getItem("logoutMessage");

    if (logoutMessage) {
        const messageDiv = document.getElementById("logout-message");

        if (messageDiv) { // Ensure element exists
            messageDiv.textContent = logoutMessage;
            messageDiv.style.display = "block";

            // Function to remove the message
            function removeLogoutMessage() {
                messageDiv.remove(); // Remove the message
                sessionStorage.removeItem("logoutMessage"); // Clear session storage
                document.removeEventListener("click", removeLogoutMessage); // Prevent duplicate listeners
            }
            setTimeout(removeLogoutMessage, 3000);
            document.addEventListener("click", removeLogoutMessage, { once: true });
        }
    }
});
