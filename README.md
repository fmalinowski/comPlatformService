# Com Platform Service Provider

This application provides a HTTP service running on port 8000 by default that accepts POST requests with JSON data to the '/email' endpoint.
The JSON object has to include the following parameters:
```
‘to:’ The email address to send to.
‘to_name:’ The name to accompany the email
‘from:’ The email address in the from and reply-to fields 
‘from_name:’ the name to accompany the from/reply emails 
‘subject:’ the subject line of the email.
‘body:’ the HTML body of the email.
```

<hr />

## How to install the application

This application has been built with the Apache Maven software project management tool. It can be found here: https://maven.apache.org

Please modify the API keys and API urls for the MailGun and Mandrill Providers in the constants `API_URL` and `API_KEY` placed at the top of the classes `MailGunProvider` and `MandrillProvider`.

To compile the project, run the following maven command: `mvn package`.
This will run the test suite and compile the application into a JAR file in the target folder.
To avoid running the tests when compiling the project, type the command: `mvn package -DskipTests=true`

To run the service, type the following command in your terminal: `java -cp ./target/com-platform-app-1.0-SNAPSHOT-jar-with-dependencies.jar com.malinowski.App`

## Language used and microframeworks

This service has been written in Java and no particular framework is used in this application except for tests purposes. 

However a few libraries are present such as the HttpComponents HttpClient library from Apache to send Post requests to the Email providers, Apache Commons IO (IOUtils class) to convert an InputStream into a string, Jsoup library to parse an HTML string and render the corresponding text.

As far as the testing frameworks, PowerMock is used in order to mock objects especially static methods and constructors.


## What could be done with additional time?

The assignment did not mention any specification regarding the response that is sent back to the client.
In order to inform the client that everything went well, we send back a 200 Http status code (everything went well up to the email providers MailGun and Mandrill).
If something goes wrong such as an invalid JSON object (for instance a JSON object that does not comply with the validations) or an error reported by the remote email providers, an error Http status code is returned to the client.

It would be great to add more information to response for the client such as the nature of the error. In order to do this, we could send back a JSON object with 2 fields: an "error" field indicating the nature of the error (ValidationError if the error occured at the validation level or another type of Error), a "message" field containing the error message and in the case of a validation error, we could add a nested JSON Object under the key "details" that contains the error for each field. Basically the keys in that JSON object would be the field names and the values would be the nature of the validation error.
To sum up, the JSON object could look like as follows:

```
{
	error: ‘ValidationError’
	message: 'Some input fields are invalid'
	details: {
		email: ‘Email is not formatted correctly’,
		from_name: ‘The sender name is empty’
	}
}
```

There is an interface `EmailServiceProvider` which is implemented by `MailGunProvider` and `MandrillProvider` in order to abstract the provider in this http service. In order to change the provider, we just need to change the object provided to the `EmailRequestsHandler` instance in the main function of the App.
What could be nice is to have a config file (for instance XML config file) that specifies the namespace of the class used for the email provider (for instance `com.malinowski.providers.MailGunProvider`) in order to make this change.
This config file would be read in the main function of the App and we would load the corresponding class specified in the config file.
Nothing would be changed in the logic of the current application.
