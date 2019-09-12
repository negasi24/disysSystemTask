# disysSystemTask
System task for interview

Login:
------
1. User have to login first. For easy understanding, display the sample username and password in the login screen.
2. User sample login credentials maintained in the credential interface with hasmap datatype because there is no api for login purpose.
3. Used RXJava for form validation and realtime authentication.
4. Once user logged successfully, the user data saved in shared preference after encrypt the data.
5. After that, it will redirect to Request whome it may concern screen.

Request whome it may concern screen:
------------------------------------
1. User fill all the mandatroy details
2. Used RXJava for form validation
3. Once user fill the details and then press submit for save the data to server database using Post method API.
4. handle the success and failur case of response.
5. Once successfully saved, it will redirect to news list screen.
6. user can skip the above step and redirect to news list screen.

News List Screen:
-----------------
1. Load the news data from get method API.
2. Used Glide libery for display the image and it will keep the image in catch memory for offline purpose.
3. Implemented swipe refresh layout for refresh the news list.

