# MESSAGES

This is an explanation of all the messages sent to the server.
Assuming correct use, the messages are in chronological order.

1. The user signs in, making a POST to /tokensignin with the user's UID.
2. The user clicks on "Go to my page!", making a GET to /usr{their UID}_self.
3. (Suppose) The user votes for a cycle they see. The cycle ID is kept consistent with the server through a JavaScript global variable - db - the server generates while making the user's page. This is a POST to /vote with JSON data with {id: the cycle ID, uid: the user's UID}.
4. (Suppose) The user adds a bunch of requests. Each request is encoded into the JSON pair encoding. An array of encoded pairs, in JSON is sent to /swap in a HTTP POST.
5. (Suppose) The user signs out. A /tokensignout POST contains the UID and the server notes that the user has signed out.

There are other GET requests initiated by the browser in order to load the required files - usually a css file and two js files after the main html file.
