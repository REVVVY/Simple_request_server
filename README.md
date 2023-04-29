# Simple request server

Simple request server that support HTTP GET.

# Guide
In order to run the program you could open the project in intellj or similar IDEs or just grab the 2 java files `Main` and `Server`. 
Run the Main class located under `src/main/java/org/example/` in the repository.
You can also inside of the Main class specify a desired port and root directory of the server. You can use my predefined files if you want, these are located in the root of the project (`404.html` and `test.html`). Those 2 files include 1 html file for the 404 not found error and another one that just displays plain text. Make sure that when you run the `Main` class those 2 files are located in the root of the server which you define by the second parameter in the main function.

When you run the `Main` class the server is up and running. Now you can open a web browser and enter `localhost:1032/test.html` in the url with the selected port and test file and hit enter.
