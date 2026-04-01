document.addEventListener("DOMContentLoaded", async function () {
    const profileForm = document.getElementById("profileForm");
    const editButton = document.getElementById("editButton");
    const saveButton = document.getElementById("saveButton");
    const profilePicture = document.getElementById("profilePicture");
    const profileUpload = document.getElementById("profileUpload");
    const inputs = document.querySelectorAll("input, select, textarea");
    const userEmail = sessionStorage.getItem("email");


    const homeResponse = await fetch("http://localhost:8080/recentApp", {
                    method: "GET",
                    headers: {
                        "Authorization": "Bearer " + storedToken, // Use the token from sessionStorage
                    }
                });

                if (homeResponse.ok) {
                    console.log("Home page access granted");
                    window.location.href = "/recentApp";
                } else {
                    throw new Error("Failed to access Home (Token Issue)");
                }

    if (!userEmail) {
        alert("User not logged in. Redirecting to login page.");
        window.location.href = "/login"; // Redirect if no email in session
        return;
    }
    document.getElementById("emailId").value = userEmail;

    // Load profile data on page load
    async function loadProfile() {
        try {
            const response = await fetch(`/api/v1/userProfile/get-profile?email=${userEmail}`);
            if (!response.ok) {
                throw new Error("Profile not found");
            }
            const profileData = await response.json();

            if (profileData) {
                profileForm.firstName.value = profileData.firstName || "";
                profileForm.lastName.value = profileData.lastName || "";
                profileForm.email.value = userEmail; // Email remains unchanged
                profileForm.gender.value = profileData.gender || "";
                profileForm.dob.value = profileData.dateOfBirth || "";
                profileForm.address.value = profileData.address || "";
                profileForm.phone.value = profileData.phoneNumber || "";
                profileForm.emergencyContact.value = profileData.alternateNumber || "";
                profilePicture.src = profileData.profileUrl || "/image/profile.jpg";
            }
        } catch (error) {
            console.error("Error loading profile:", error);
        }
    }

    loadProfile();

    // Profile Picture Upload
    profileUpload.addEventListener("change", function (e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function () {
                profilePicture.src = reader.result;
            };
            reader.readAsDataURL(file);
        }
    });

    // Edit Profile
    editButton.addEventListener("click", () => {
        inputs.forEach(input => {
            if (input.name !== "email") {
                input.readOnly = false;
                if (input.tagName === "SELECT") input.disabled = false;
            }
        });

        saveButton.hidden = false;
        editButton.hidden = true;
    });

    // Save Profile
    saveButton.addEventListener("click", async function (e) {
        e.preventDefault();

        const formData = {
            firstName: profileForm.firstName.value,
            lastName: profileForm.lastName.value,
            gender: profileForm.gender.value,
            dateOfBirth: profileForm.dob.value,
            address: profileForm.address.value,
            phoneNumber: profileForm.phone.value,
            alternateNumber: profileForm.emergencyContact.value,
            profileUrl: profilePicture.src, // Base64 or image URL
        };

        try {
            const response = await fetch(`/api/v1/userProfile/add?email=${userEmail}`, {  // ✅ Include email in URL
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            if (response.ok) {
                console.log("Update successful. Reloading...");
                alert("Profile updated successfully!");

                inputs.forEach(input => {
                    input.readOnly = true;
                    if (input.tagName === "SELECT") input.disabled = true;
                });
                saveButton.hidden = true;
                editButton.hidden = false;

                setTimeout(() => {
                    window.location.reload(true); // Force reload
                }, 100);

            } else {
                const errorMessage = await response.text();
                alert("Error updating profile: " + errorMessage);
            }
        } catch (error) {
            console.error("Error saving profile:", error);
        }
    });

});
