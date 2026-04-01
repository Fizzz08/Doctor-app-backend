document.addEventListener('DOMContentLoaded', function () {
    console.log("🏠 Home Page Loaded");

    // Fetch Username if Email Exists
    const userEmail = sessionStorage.getItem("email");
    if (userEmail) {
        fetch(`/username?email=${encodeURIComponent(userEmail)}`)
            .then(response => response.text())
            .then(username => {
                const elements = document.getElementsByClassName('welcome-message');
                for (let element of elements) {
                    element.textContent = username !== 'User not found' ? username : 'Welcome, Guest';
                }
            })
            .catch(error => console.error('Error fetching the username:', error));
    }

    // Profile Dropdown Toggle
    document.getElementById('profileImg')?.addEventListener('click', function () {
        const dropdown = document.getElementById('dropdownMenu');
        dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
    });

    // Close Dropdown when Clicking Outside
    document.addEventListener("click", function (event) {
        const dropdown = document.getElementById("dropdownMenu");
        if (dropdown && event.target.id !== "profileImg" && !dropdown.contains(event.target)) {
            dropdown.style.display = "none";
        }
    });

    // Logout
    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", function (event) {
            event.preventDefault();

            fetch("/logout", {
                method: "DELETE",  // Change to DELETE if POST doesn't work
                credentials: "same-origin",
                headers: {
                    "Authorization": "Bearer " + token, // Include token
                    "Content-Type": "application/json",
                },
            })
            .then(response => {
                if (!response.ok) throw new Error("Logout request failed");
                return response.text();
            })
            .then(() => {
                sessionStorage.clear();  // Clear session storage on logout
                window.location.href = "/login"; // Redirect to login
            })
            .catch(error => {
                console.error("Logout failed:", error);
                alert("Logout failed. Please try again.");
            });
        });
    }


    // Get the form element
    const form = document.getElementById('appointmentForm');

    if (form) {
        console.log("Form found! Adding event listener.");
        form.addEventListener('submit', function (event) {
            event.preventDefault(); // Prevent default form submission

            // Get input values
            const location = document.getElementById('location').value;
            const specialization = document.getElementById('specialization').value;

            // Validate inputs
            if (location && specialization) {
                // Fetch data from the backend
                fetch(`/api/v1/doctor/search?location=${encodeURIComponent(location)}&specialization=${encodeURIComponent(specialization)}`)
                    .then(response => response.json())
                    .then(data => {
                        if (data.length === 0) {
                            sessionStorage.removeItem('doctorData');
                            window.location.href = '/book?noResult=true';
                        } else {
                            sessionStorage.setItem('doctorData', JSON.stringify(data));
                            window.location.href = `/book?location=${encodeURIComponent(location)}&specialization=${encodeURIComponent(specialization)}`;
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('An error occurred while searching for doctors. Please try again.');
                    });
            } else {
                alert('Please fill in both location and specialization fields.');
            }
        });
    } else {
        console.error('Form with ID "appointmentForm" not found.');
    }
});

