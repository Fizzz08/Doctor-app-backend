document.addEventListener('DOMContentLoaded', function() {
    const appointmentsList = document.getElementById('appointments-list');

    // Get user email from session storage (Make sure you store the email in sessionStorage when user logs in)
    const userEmail = sessionStorage.getItem("email");

    if (!userEmail) {
        appointmentsList.innerHTML = `<div class="appointment-card error">User email not found. Please log in.</div>`;
        return;
    }

    // API Endpoint
    const apiUrl = `http://localhost:8080/api/bookAppointment/recent-appointments?email=${encodeURIComponent(userEmail)}`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                appointmentsList.innerHTML = `<div class="appointment-card no-appointments">No recent appointments found</div>`;
            } else {
                let appointmentsHTML = "";
                data.forEach(appointment => {
                    appointmentsHTML += `
                        <div class="Text-Container">
                            <div class="appointment-card">
                                <h3>${appointment.doctorName}</h3>
                                <p><strong>Date:</strong> ${appointment.appointmentDate} (${appointment.day_of_appointment})</p>
                                <p><strong>Time:</strong> ${appointment.timeOfAppointment}</p>
                                <p><strong>Token:</strong> ${appointment.patient_token}</p>
                            </div>
                            <div class="Status">
                                <p><strong>Status:</strong> ${appointment.status}</p>
                            </div>
                        </div>
                    `;
                });
                appointmentsList.innerHTML = appointmentsHTML;
            }
        })
        .catch(error => {
            console.error("Error fetching appointments:", error);
            appointmentsList.innerHTML = `<div class="appointment-card error">Error fetching appointments</div>`;
        });

    //Log-out
        const logoutButton = document.getElementById("logoutButton");

        if (logoutButton) {
            logoutButton.addEventListener("click", function (event) {
                event.preventDefault(); // Prevent default link behavior

                fetch("/logout", {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Logout request failed");
                    }
                    return response.text(); // Use text() instead of json() to prevent errors
                })
                .then(() => {
                    sessionStorage.setItem("logoutMessage", "Successfully logged out!"); // Store message
                    sessionStorage.removeItem("userSessionData"); // Clear specific session data (if needed)
                    window.location.href = "/login"; // Redirect to login page
                })
                .catch(error => {
                    console.error("Logout failed:", error);
                    alert("Logout failed. Please try again.");
                });
            });
        }
});
