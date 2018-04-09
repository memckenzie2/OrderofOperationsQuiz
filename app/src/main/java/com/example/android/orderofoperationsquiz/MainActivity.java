package com.example.android.orderofoperationsquiz;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Activity states
    CheckBox check1Q1;
    CheckBox check2Q1;
    CheckBox check3Q1;
    CheckBox check4Q1;
    EditText editTextQ2;
    RadioGroup q3Group;
    EditText editTextQ4;
    EditText editTextQ5;
    TextView q1;
    TextView q2;
    TextView q3;
    TextView q4;
    TextView q5;
    EditText editTextName;
    int attemptCounter;

    //Variables for displaying  and markingrandomly generated questions
    private String solutionQ2;
    private int solutionQ4;
    private String questionText4;
    private int solutionQ5;
    private String questionText5;
    //Variables to store and track missed questions for later display to player.
    private String missedQuestions;
    private String message;
    private String scoreMessage;
    private Button emailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes activity calls
        editTextName = findViewById(R.id.name_edit);
        check1Q1 = findViewById(R.id.question_1_check1);
        check2Q1 = findViewById(R.id.question_1_check2);
        check3Q1 = findViewById(R.id.question_1_check3);
        check4Q1 = findViewById(R.id.question_1_check4);
        editTextQ2 = findViewById(R.id.question2_edit);
        q3Group = findViewById(R.id.question_3_radio_group);
        editTextQ4 = findViewById(R.id.question_4_edit);
        editTextQ5 = findViewById(R.id.question_5_edit);
        q1 = findViewById(R.id.question1);
        q2 = findViewById(R.id.question2);
        q3 = findViewById(R.id.question3);
        q4 = findViewById(R.id.question4);
        q5 = findViewById(R.id.question_5);

        //Generates solutions to the random problems
        solutionQ4 = orderOpProb4();
        solutionQ5 = orderOpProb5();
        solutionQ2 = randomQ2();
        attemptCounter = 0;
        emailButton = findViewById(R.id.email);
        emailButton.setVisibility(View.INVISIBLE);
    }

    /*
    This method is called when the order button is clicked.
     */
    public void onSubmit(View view) {
        int correct = 0;
        missedQuestions = " missed the following questions:\n";

        //Pulls state of all response views
        //Reduces state of checkboxes to single true/false - true if ANY checkbox checked and false if none selected.
        boolean checkBoxSelected = check1Q1.isChecked() || check2Q1.isChecked() || check3Q1.isChecked() || check4Q1.isChecked();

        //Response to Question 2
        String responseQ2 = editTextQ2.getText().toString();

        //Response Question 3
        int radioGroupQ3 = q3Group.getCheckedRadioButtonId();

        //Response to Question 4
        String responseQ4 = editTextQ4.getText().toString();

        //Response to Question 5
        String responseQ5 = editTextQ5.getText().toString();

        //Check all questions have a response. If not, highlights unanswered question in red and then displays a toast message
        if (allQuestionsAnswer(checkBoxSelected, responseQ2, radioGroupQ3, responseQ4, responseQ5)) {
            Toast toast = new Toast(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Uh-oh, you're not done! Please answer the questions highlighted in red.",
                    Toast.LENGTH_LONG).show();
        }
        //If all questions has a response, check answers, score quiz, and display results in a toast message.
        else {

            //Question 1, Check boxes 2 and 3 should be the only ones selected
            if(markQ1(check1Q1.isChecked(), check2Q1.isChecked(), check3Q1.isChecked(), check4Q1.isChecked())){
                correct += 1;
            }


            //Question 2 - returns true if entered string matches solutionQ2
            if(markQ2(responseQ2)){
                correct +=1;
            }


            //Question 3 - returns true if the 4th radio button is selected
            RadioButton radioCorrectQ3 = findViewById(R.id.question_3_radio4);
            if(markQ3(radioCorrectQ3.isChecked())){
                correct += 1;
            }

            //Question 4 - returns true if entered string matches solutionQ4
            if(markQ4(responseQ4)){
                correct +=1;
            }

            //Question 5 - returns true if entered string matches solutionQ5
            if(markQ5(responseQ5)){
                correct += 1;
            }

            if (correct == 5) {
                missedQuestions += "Amazing! No questions missed!";
            }
            //Calculate score and display as toast message
            finalScore(correct);
            emailButton.setVisibility(View.VISIBLE);
        }

    }

    /*
   This method is called when the reset button is clicked. It resets all views and generates new random questions.
    */
    public void reset(View view) {

        //Generates solutions to the random problems
        solutionQ4 = orderOpProb4();
        solutionQ5 = orderOpProb5();
        solutionQ2 = randomQ2();
        missedQuestions = " missed the following questions:\n";
        attemptCounter = 0;

        //resets all questions to default state
        check1Q1.setChecked(false);
        check2Q1.setChecked(false);
        check3Q1.setChecked(false);
        check4Q1.setChecked(false);
        editTextQ2.setText("");
        q3Group.clearCheck();
        editTextQ4.setText("");
        editTextQ5.setText("");
        editTextName.setText("");

        //Reset all to default color
        q1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        q2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        q3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        q4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        q5.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        emailButton.setVisibility(View.INVISIBLE);
    }

    /*
    This method is called when the e-mail results button is clicked.
     */
    public void emailResults(View view) {
        //Retrieves student's name
        String name = editTextName.getText().toString();
        //Build Message
        message = scoreMessage + "\n\n" + name + missedQuestions + "\n\nKnow the answers? Download the Order of Operations Quiz App and beat their score!";

        // Use an intent to launch an email app.
        // Send the score and missed questions in the email body.
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.email_subject, name));
        intent.putExtra(Intent.EXTRA_TEXT, message);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    /* Updates question 2 Textview with problem statement
     * @param questionChar is the letter from PEMDAS that is being used for the current version of the question.
     */
    private void displayProb2(String questionChar) {

        q2.setText("2. What does the " + questionChar + " in the acronym PEMDAS stand for?");
    }

    /* Updates question 4 Textview with problem statement
     * @param problem is the problem statement.
     */
    private void displayProb4(String problem) {
       q4.setText(problem);
    }

    /* Updates question 5 Textview with problem statement
     * @param problem is the problem statement..
     */
    private void displayProb5(String problem) {
        q5.setText(problem);
    }

    /*
    Chooses a random letter from PEMDAS, generates a problem statement for question 2 to be displayed, and returns the correct solution for the problem.
    */
    public String randomQ2() {
        String question = "";
        String answer = "";
        Random randNum = new Random();
        int randAns = randNum.nextInt(5);

        //Chooses which letter from PEMDAS with equal probability based on randomly generated value from 0-5.
        switch (randAns) {
            case 0:
                question = "P";
                answer = "parentheses";
                break;
            case 1:
                question = "E";
                answer = "exponents";
                break;
            case 2:
                question = "M";
                answer = "multiplication";
                break;
            case 3:
                question = "D";
                answer = "division";
                break;
            case 4:
                question = "A";
                answer = "addition";
                break;
            case 5:
                question = "S";
                answer = "subtraction";
                break;
        }

        //Updates the Question 2 Textview
        displayProb2(question);

        return answer;
    }

    /*
    Generates random numbers to populate question 4's expression: a - b * c + d
     */
    public int orderOpProb4() {

        Random rand = new Random();
        int solution;

        int a = rand.nextInt(50) + 1;
        int b = rand.nextInt(50) + 1;
        int c = rand.nextInt(50) + 1;
        int d = rand.nextInt(7) + 1;
        solution = a - b * c + d;

        questionText4 = "4. Solve: \n " + Integer.toString(a) + " - " + Integer.toString(b) + " × " + Integer.toString(c) + " + " + Integer.toString(d);
        displayProb4(questionText4);
        return solution;
    }


    /*
    Generates random numbers to populate question 5's expression: z + a ÷ (d - b * c)
    To generate a problem with a whole number solution the random numbers must be generated such that...
    a must be divisible by the correct solution to (d - b * c)
    This will be done by generating b, c, and d first and generating a to be a multiple of the result of (d - b * c).
     */
    public int orderOpProb5() {

        Random rand = new Random();
        int solution;

        int b = rand.nextInt(10) + 1;
        int c = rand.nextInt(10) + 1;
        int d = rand.nextInt(10) + 1;
        int aDiv = rand.nextInt(7) + 1;
        int a = aDiv * (d + b * c);
        int z = rand.nextInt(10) + 1;
        solution = z - aDiv;

        questionText5 = "5. Solve: \n " + Integer.toString(z) + " - " + Integer.toString(a) + " ÷ " + "(" + Integer.toString(d) + " + " + Integer.toString(b) + " × " + Integer.toString(c) + ")";
        displayProb5(questionText5);
        return solution;
    }

    /*
    Ensures that all questions have answers and if not sets the question textview to red.
    Displays a toastview to user asking them to answer questions in red.
    Return true if all questiuons have a response, false if one or more do not
    @param checkBoxSelected is true if a CheckBox for question 1 is selected
    @param editText2 the contents of the EditText field for question 2
    @param radioQ3 the id of the selected button in the radiobutton group for question 3. Will be -1 if no button is selected.
    @param editText4 the contents of the EditText field for question 4
    @param editText5 the contents of the EditText field for question 5
     */
    private boolean allQuestionsAnswer(boolean checkBoxSelected, String editText2, int radioQ3, String editText4, String editText5) {
        boolean emptyAnswer = false;

        //Check if at least one checkbox is selected,
        if (!checkBoxSelected) {

            q1.setTextColor(Color.RED);
            emptyAnswer = true;
        } else {
            q1.setTextColor(Color.parseColor("#0026ca"));
        }

        //Check if edittext for question 3 has input
        if (TextUtils.isEmpty(editText2)) {
            q2.setTextColor(Color.RED);
            emptyAnswer = true;
        } else {
            q2.setTextColor(Color.parseColor("#0026ca"));
        }

        //check if radio button is selected
        if (radioQ3 == -1) {
            q3.setTextColor(Color.RED);
            emptyAnswer = true;
        } else {
            q3.setTextColor(Color.parseColor("#0026ca"));
        }

        //Check if edittext for question 4 has input
        if (TextUtils.isEmpty(editText4)) {
            q4.setTextColor(Color.RED);
            emptyAnswer = true;
        } else {
            q4.setTextColor(Color.parseColor("#0026ca"));
        }

        //Check if edittext for question 5 has input
        if (TextUtils.isEmpty(editText5)) {
            q5.setTextColor(Color.RED);
            emptyAnswer = true;
        } else {
            q5.setTextColor(Color.parseColor("#0026ca"));
        }

        return emptyAnswer;
    }

    /*
    Checks the response to question 1 against the correct solution.
    Correct response is 2nd and 3rd checkboxes selected and none others.
    Returns true if correct.
    @param check1 is the status of the 1st checkbox option
    @param check2 is the status of the 2nd checkbox option
    @param check3 is the status of the 3rd checkbox option
    @param check4 is the status of the 4th checkbox option
     */
    private boolean markQ1(boolean check1, boolean check2, boolean check3, boolean check4) {
        if (!check1 && check2 && check3 && !check4) {
            return true;
        } else {
            missedQuestions = missedQuestions + "\nQuestion 1\n" + q1.getText() + "\n";
            return false;
        }
    }

    /*
    Checks the response to question 2 against the correct solution.
    Correct response is stored in solutionQ2 and is randomly selected from possible PEMDAS values at launch.
    Returns true if correct.
    @param responseQ2 is the contents of the edittext for question 2.
     */
    private boolean markQ2(String responseQ2) {
        if (responseQ2.toLowerCase().equals(solutionQ2)) {
            return true;
        } else {
            missedQuestions = missedQuestions + "\nQuestion 2\n" + q2.getText() + "\n";
            return false;
        }
    }

    /*
    Checks the response to question 3 by checking status of correct response (4th option)
    Returns true if correct.
    @param responseQ3 is the status of the correct radiobutton option - it is true if selected.
     */
    private boolean markQ3(boolean correctQ3) {
        if (correctQ3) {
            return true;
        } else {
            missedQuestions = missedQuestions + "\nQuestion 3\n" + q3.getText() + "\n";
            return false;
        }
    }

    /*
    Checks the response to question 4 against the correct solution.
    Correct response is stored in solutionQ4 from the resulting randomly generated values for problem that are initialized during launch..
    Returns true if correct.
    @param responseQ4 is the contents of the edittext for question 4.
     */
    private boolean markQ4(String responseQ4) {
        int responseQ4Int;
        try {
            responseQ4Int = Integer.parseInt(responseQ4);
        } catch (Exception e) {

            return false;
        }

        if (responseQ4Int == solutionQ4) {
            return true;
        } else {
            missedQuestions = missedQuestions + "\nQuestion 4\n" + q4.getText() + "\n";
            return false;
        }
    }

    /*
    Checks the response to question 5 against the correct solution.
    Correct response is stored in solutionQ4 from the resulting randomly generated values for problem that are initialized during launch..
    Returns true if correct.
    @param responseQ5 is the contents of the edittext for question 5.
     */
    private boolean markQ5(String responseQ5) {
        int responseQ5Int;
        try {
            responseQ5Int = Integer.parseInt(responseQ5);
        } catch (Exception e) {

            return false;
        }

        if (responseQ5Int == solutionQ5) {
            return true;
        } else {
            missedQuestions = missedQuestions + "\nQuestion 5\n" + q5.getText();
            return false;
        }
    }

    /*
    Calculates the final score as a percentage of correct / number of questions.
    Displays a toast message to user with resulting percentage and ratio.
    @param correct is the number of correct answers
     */
    private void finalScore(int correct) {
        double percentCorrect = (double) correct / 5 * 100;
        //Retrieves student's name
        String name = editTextName.getText().toString();
        scoreMessage = name + " earned " + Double.toString(percentCorrect) + "% which is " + Integer.toString(correct) + "/5 correct!";

        Toast toast = new Toast(getApplicationContext());
        Toast.makeText(getApplicationContext(), scoreMessage,
                Toast.LENGTH_LONG).show();
    }
}