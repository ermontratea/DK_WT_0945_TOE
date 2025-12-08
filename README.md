
# Clinic App



## Backend Access
Run `ClinicApplication.java` to access the SpringBoot app at http://localhost:8080.

- Add doctor (Anna Nowak, PESEL: 00000000000, specialization: CARDIOLOGY, address: A 1) with:
```http
POST http://localhost:8080/doctors
Content-Type: application/json

{
  "firstName": "Anna",
  "lastName": "Nowak",
  "pesel": "00000000000",
  "specialization": "CARDIOLOGY",
  "address": "A 1"
}
 ```
- To see doctors list go to http://localhost:8080/doctors.
- See doctor with ID 1 details at http://localhost:8080/1 etc.
- Delete doctor with ID 1 with
```http
 DELETE http://localhost:8080/1
 ```


## Frontend Access
Run the backend first by running `ClinicApplication.java`. Then run `ClinicApplicationFX.java`.

If it doesn’t work, you can also run the app using Gradle from the IDE:
- Open the Gradle panel
- Go to `clinic -> Tasks -> application`
- Run `run`

![Run ClinicApplicationFX via Gradle](runClinicAppFx.png)