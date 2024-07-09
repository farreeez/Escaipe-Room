# EscAIpe Room

## Acknowledgements

This project would not have been possible without the hard work, dedication,
and efforts of Nasser Giacaman, Valerio Terragni, and all the other lecturers
and tutors involved in running the SOFTENG 206 course at the University of Auckland in 2023.
I would also like to thank my group members, Shubh Bhargava and Mehul Killa, for their dedication,
passion and commitment to this project. This project would not have been possible without your
dedication and hard work.

## About

**EscAIpe Room** is a JavaFX application created as a project for the course SOFTENG 206.
Which is a Part 2 Software Engineering design course at the University of Auckland. It is a 3D
escape room game with an AI virtual assistant run by GPT 3.5 that guides the user through the game.
The game was made for a wide range of audiences with the aim of providing a fun and possibly educational
experience.

## Software Pre-Requisites

To run the software successfully, ensure that the following software have been
installed:

- Java Runtime Environment (JRE) 21
- JavaFX 20

## Running

## To setup OpenAI's API

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `apiproxy.config`
- put inside the credentials that you received from no-reply@digitaledu.ac.nz (put the quotes "")

  ```
  email: "upi123@aucklanduni.ac.nz"
  apiKey: "YOUR_KEY"
  ```

  these are your credentials to invoke the OpenAI GPT APIs

## To run the game

`./mvnw clean javafx:run`

## To debug the game

`./mvnw clean javafx:run@debug` then in VS Code "Run & Debug", then run "Debug JavaFX"
