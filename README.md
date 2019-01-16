# Teaching Assistant

Both apps have similar architecture and flow.

## Application flow

The entry point is the ```MainActivity```. Here, users can chose a login name which will be
further
used as an identification token throughout the runtime. After this, the next screen is the
```DisplayCoursesActivity```. On this screen, users can chose the course for which they
want to participate. This is the first time where the Firebase Database is accessed.
After choosing this, the ```MenuActivity``` is displayed. Users can decide wether they want
to go to the Question or to the Quiz part of the application.

From here on, there will be lots of messages stored in the database, like questions or
quizzes. To separate messages from one day to another I have used the system date to
generate a key so as to store them in the database.

## Question
After all the questions are loaded from the database, if there are any, the spinner
dissapears and they appear under the form of a Question and number of votes button.
Pressing the second button triggers an update in the database, where the previously
chosen username is associated with a vote and the total number of votes for a question
is updated.

From empirical results, I have chosen to make a question votable, without the possibility
of removing the vote. It is not the end of the world if you vote and the users tend to
spam the vote button and, as a result, other users cannot input in the application due to
the refresh.

In the upper part of the screen, users can press to add another question to the course,
launching the ```SubmitQuestionActivity```. From here, users can submit their question or
discard everything and return to the previous screen.

The application for teachers doesn`t have the possibility to add questions or vote them.
However, they can view who posted a question by pressing the question, triggering a
short message to appear on the screen.

## Quiz
From the here students and teachers have different roles. Teachers use the app to add a
quizz to the database. They another activity called ```AddQuizActivity``` where they have a
field to add the actual question and can add multiple answers. To submit the quiz, it is
necessary to select the correct answer and have at least one question added.

After submitting a quiz, teachers can view the answers which students have given to the
quiz under the form of a pie chart which has chunks representing the given answers and,
in the center, the total number of answers. The pie chart was drawn using a library found
on the internet.

Students can acces the quiz section and see the quizzes posted by the teacher. Then,
they can answer the quiz and that's it.
