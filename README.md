# Java Socket Chat App
## Assigmment 01: Networking CS Bach Khoa HCM University
### Summary: Write a app Chat using java socket API
### Implement:
- Step 1: We create CLI (Command Line Interface) for basic web chat.
- Step 2: We design GUI (Graphical User Interface)for this app.
### Step 1: Create CLI for basic web chat:
#### Processing:
1. Server is create for handle client request.
2. Client view a list of client offline - online when connect to server.
	2.1 Every User has a username, and password, they can create new one for the first time, the information is save at server.
	2.2 Server handle request for connect another User from a User.
3. When a User want to connect to other User, which are online. They will sent a message for request to server.
4. Server handle this request and notify to target User.
5. If they accept, a client will create they own server and connect to other.
#### A Server:
- Has a list of users, save information of its.
#### A Client:
- Request for access server
- Create they own server
- Exchange message with server
