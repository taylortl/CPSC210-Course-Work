package ui.exceptions;

/******************************************
 * Since this exception only check for user inputs,
 * I cannot test the exception in the tests for my models
 * I talked to another TA about my situation,
 * and he suggested me to put it in the UI package so that it won't affect my code coverage test
 ******************************************/
// Exception when user entered an invalid input
public class OutOfBoundInput extends Exception {
}
