Download the library & author(for testing feign). 
Database configuration in application.properties.
Run the library & author to check the end-points.
To open swagger use link: http://localhost:8081/swagger-ui/swagger-ui/index.html; (Port was changed to have an ability to run the library & author at one time).
To test feign, please, add new book with such parameters:
{
           "title": "Java Programming",
           "author": "John Doe",
           "publicationYear": 2023,
           "availableCopies": 5,
}.
Use the link: http://localhost:8081/h2-console/login.jsp?jsessionid=dbf307014689b9661f543c77fcf15169
to see changes in db (login: "sa" ; password : "password")
If something went wrong, please, contact me.
