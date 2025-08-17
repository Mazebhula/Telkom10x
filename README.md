User Management and PDF Form Filling Application Documentation

Overview

This Spring Boot application provides a web-based platform for user authentication, managing user data, filling PDF forms with user information, and sharing geolocation. It uses Spring Security for authentication, Thymeleaf for templating, and iText for PDF manipulation. The application supports a "whoami" dashboard, a "Find a Taxi" form, and user data viewing functionality.

Architecture

Framework: Spring Boot

Security: Spring Security for form-based authentication

Templating: Thymeleaf for rendering HTML templates

PDF Processing: iText 7 for filling PDF form fields

Data Storage: Assumes a database with a UserDataRepository (implementation not provided)

Frontend: HTML with CSS (/css/login.css and /css/styles.css) and JavaScript for geolocation

Key Components

Model: UserData (fields: id, email, firstName, lastName, address, phone)

Service: FormService for saving/retrieving user data and filling PDFs

Controller: FormController for handling HTTP requests

Templates: login.html, form.html, dashboard.html, view.html

Static Resources: CSS files in src/main/resources/static/css/

Security Configuration: SecurityConfig for authentication and authorization

Endpoints

1. GET /login

Description: Renders the login page.

Template: login.html

Parameters:

error (optional): Displays "Invalid username or password" if present

Access: Public

Response: Renders login.html with an optional error message

2. GET /

Description: Displays the "Find a Taxi" form for authenticated users, redirects to /login otherwise.

Template: form.html

Model Attributes:

userData: New UserData instance

username: Authenticated user’s email

Access: Authenticated users only

Response: Renders form.html

3. GET /home

Description: Displays the main dashboard with user data input, PDF filling, and location-sharing options.

Template: dashboard.html

Model Attributes:

userData: New UserData instance

username: Authenticated user’s email

Access: Authenticated users only

Response: Renders dashboard.html

4. GET /dashboard

Description: Displays the "whoami" page with the authenticated user’s data.

Template: view.html

Model Attributes:

username: Authenticated user’s email

userData: User’s data from FormService.getUserData(email)

Access: Authenticated users only

Response: Renders view.html, redirects to /login if unauthenticated

5. POST /save

Description: Saves user data to the database.

Template: form.html

Request Body: UserData (form data: email, firstName, lastName, address, phone)

Model Attributes:

message: Success or error message

Access: Authenticated users only

Response: Renders form.html with a success/error message

6. GET /view?email=<email>

Description: Displays user data for the provided email.

Template: view.html

Parameters:

email: User’s email to fetch data

Model Attributes:

userData: User’s data from FormService.getUserData(email)

username: Provided email

Access: Authenticated users only

Response: Renders view.html

7. POST /fill-pdf

Description: Fills a PDF form with user data and returns the filled PDF.

Request Parameters:

email: User’s email to fetch data

file: Uploaded PDF file

Response:

Success: Filled PDF as a downloadable file (filled_form.pdf)

Error: Text response with error message

Access: Authenticated users only

Content-Type: application/pdf (success), text/plain (error)

8. POST /location

Description: Handles geolocation data (not implemented in controller, assumed to exist).

Request Body: username, latitude, longitude (form-urlencoded)

Response: JSON with success or error (assumed)

Access: Authenticated users only

9. GET /logout

Description: Logs out the user and redirects to /login?logout=true.

Access: Authenticated users only

Response: Redirects to /login

Templates

1. login.html

Purpose: Login page for user authentication

CSS: /css/login.css

Features:

Form submitting to /login with username (email) and password

Displays error message if error parameter is present

Location: src/main/resources/templates/login.html

2. form.html

Purpose: "Find a Taxi" form for entering user data

CSS: /css/login.css

Features:

Form to save user data (/save)

Navigation links: Home (/home), Find a Taxi (/view?email=${username}), Chat (/chat), Logout (/logout)

Location: src/main/resources/templates/form.html

3. dashboard.html

Purpose: Main dashboard with user data input, PDF filling, and location sharing

CSS: /css/login.css

Features:

Displays authenticated user’s email

Form to save user data (/save)

Form to fill PDF (/fill-pdf)

Button for sharing geolocation (/location)

Navigation links: Home (/home), Find a Taxi (/view?email=${username}), Chat (/chat), Logout (/logout)

Location: src/main/resources/templates/dashboard.html

4. view.html

Purpose: Displays user data for "whoami" or viewing by email

CSS: /css/styles.css

Features:

Shows user data (email, firstName, lastName, address, phone) or "No data found"

Navigation links: Home (/home), Find a Taxi (/view?email=${username}), Chat (/chat), Logout (/logout)

Location: src/main/resources/templates/view.html

Security Configuration

Class: SecurityConfig

File: src/main/java/com/telkom/config/SecurityConfig.java

Features:

Form-based authentication with custom login page (/login)

Redirects to /dashboard after successful login

Public access to /login and /css/**

All other endpoints require authentication

Logout via /logout, redirecting to /login?logout=true

Dependencies

Ensure the following are in pom.xml:

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <version>7.2.5</version>
        <type>pom</type>
    </dependency>
</dependencies>

Setup Instructions

Clone the Project:

Place the code in a Spring Boot project structure.

Configure Database:

Set up a database (e.g., MySQL, PostgreSQL) and configure application.properties for spring.datasource.

Implement UserDataRepository (e.g., using Spring Data JPA).

Place Templates:

Save login.html, form.html, dashboard.html, and view.html in src/main/resources/templates/.

Static Resources:

Place login.css and styles.css in src/main/resources/static/css/.

PDF Form:

Compile form.tex (provided earlier) using pdflatex to generate form.pdf with form fields (FirstName, LastName, EmailAddress, StreetAddress, PhoneNumber).

Run the Application:

Use mvn spring-boot:run or your IDE to start the application.