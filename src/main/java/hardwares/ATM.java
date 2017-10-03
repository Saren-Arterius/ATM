package hardwares;// Represents an automated teller machine

import actions.BalanceInquiry;
import actions.Transaction;
import actions.Transfer;
import actions.Withdrawal;
import data.BankDatabase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ATM {
    // constants corresponding to main menu options
    private static final int BALANCE_INQUIRY = 1;
    private static final int WITHDRAWAL = 2;
    private static final int TRANSFER = 3;
    private static final int EXIT = 4;
    protected boolean userAuthenticated; // whether user is authenticated
    protected int currentAccountNumber; // current user's account number
    protected Screen screen; // hardwares.ATM's screen
    protected CashDispenser cashDispenser; // hardwares.ATM's cash dispenser
    protected BankDatabase bankDatabase; // account information database
    protected Keypad keypad; // hardwares.ATM's keypad (which can be mocked)

    // no-argument hardwares.ATM constructor initializes instance variables
    public ATM() {
        JFrame frame = new JFrame("ATM");
        ATMPanel panel = new ATMPanel();
        frame.setContentPane(panel.getMainPanel());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        userAuthenticated = false; // user is not authenticated to start
        currentAccountNumber = 0; // no current account number to start
        screen = new Screen(panel); // create screen
        keypad = new Keypad(panel); // create keypad
        cashDispenser = new CashDispenser(); // create cash dispenser
        bankDatabase = new BankDatabase(); // create acct info database
    } // end no-argument hardwares.ATM constructor


    // start hardwares.ATM
    public void run() {
        // welcome and authenticate user; perform transactions
        while (true) {
            // loop while user is not yet authenticated
            while (!userAuthenticated) {
                screen.displayMessageLine("Welcome!");
                authenticateUser(); // authenticate user
            } // end while

            performTransactions(); // user is now authenticated
            userAuthenticated = false; // reset before next hardwares.ATM session
            currentAccountNumber = 0; // reset before next hardwares.ATM session
            screen.displayMessage("Thank you! Goodbye!");
            screen.renderToScreen();
            screen.sleepThenClear(2000);
        } // end while
    } // end method run

    // attempts to authenticate user against database
    protected void authenticateUser() {
        screen.displayMessage("Please enter your account number.");
        screen.renderToScreen();
        int accountNumber = keypad.getInput(); // input account number
        screen.clearMessage();
        screen.displayMessage("Enter your PIN."); // prompt for PIN
        screen.renderToScreen();
        keypad.setCancelButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                keypad.setInputBuffer(String.valueOf(Withdrawal.CANCELED));
                synchronized (keypad.getInputLock()) {
                    keypad.getInputLock().notifyAll();
                }
            }
        });
        keypad.setMaskInput(true);
        int pin = keypad.getInput(); // input PIN
        keypad.setCancelButtonListener(null);
        if (pin == Withdrawal.CANCELED) {
            screen.clearMessage();
            return;
        }
        keypad.setEnableDigitInput(false);
        screen.clearMessage();
        screen.renderToScreen();
        keypad.setMaskInput(false);

        screen.displayDelayedMessage("Authenticating", 3, 500);
        screen.sleepThenClear(1000);
        // set userAuthenticated to boolean value returned by database
        userAuthenticated =
                bankDatabase.authenticateUser(accountNumber, pin);

        // check whether authentication succeeded
        if (userAuthenticated) {
            currentAccountNumber = accountNumber; // save user's account #
        } else {
            screen.displayMessageLine("Invalid account number or PIN. \nPlease try again.");
            screen.renderToScreen();
            screen.sleepThenClear(2000);
        }
    } // end method authenticateUser

    // display the main menu and perform transactions
    protected void performTransactions() {
        // local variable to store transaction currently being processed
        Transaction currentTransaction = null;

        boolean userExited = false; // user has not chosen to exit

        // loop while user has not chosen option to exit system
        while (!userExited) {
            // show main menu and get user selection
            int mainMenuSelection = displayMainMenu();

            // decide how to proceed based on user's menu selection
            switch (mainMenuSelection) {
                // user chose to perform one of three transaction types
                case BALANCE_INQUIRY:
                case WITHDRAWAL:
                case TRANSFER:
                    // initialize as new object of chosen type
                    currentTransaction =
                            createTransaction(mainMenuSelection);
                    currentTransaction.execute(); // execute transaction
                    if (currentTransaction instanceof BalanceInquiry) {
                        keypad.setEnableDigitInput(false);
                        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_BOTTOM, "Exit");
                        keypad.getActionInput();
                    }
                    if (currentTransaction instanceof Withdrawal) {
                        userExited = true; // this hardwares.ATM session should end if it is withdrawal
                    }
                    break;
                case EXIT: // user chose to terminate session
                    userExited = true; // this hardwares.ATM session should end
                    break;
                default: // user did not enter an integer from 1-4
                    // This is unlikely to happen
                    screen.displayMessageLine(
                            "\nYou did not enter a valid selection.\nTry again.");
                    break;
            } // end switch
        } // end while
    } // end method performTransactions

    // display the main menu and return an input selection
    protected int displayMainMenu() {
        keypad.setEnableDigitInput(false);
        screen.clearMessage();
        screen.displayMessage("Main menu");
        screen.renderToScreen();
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.LEFT_TOP, "View my balance");
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_TOP, "Withdraw cash");
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.LEFT_BOTTOM, "Transfer");
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_BOTTOM, "Exit");
        int input = keypad.getActionInput();
        keypad.resetActionButtons();
        screen.clearMessage();
        return input; // return user's selection
    } // end method displayMainMenu

    // return object of specified actions.Transaction subclass
    protected Transaction createTransaction(int type) {
        Transaction temp = null; // temporary actions.Transaction variable
        // determine which type of actions.Transaction to create
        switch (type) {
            case BALANCE_INQUIRY: // create new actions.BalanceInquiry transaction
                temp = new BalanceInquiry(
                        currentAccountNumber, screen, bankDatabase);
                break;
            case WITHDRAWAL: // create new actions.Withdrawal transaction
                temp = new Withdrawal(currentAccountNumber, screen,
                        bankDatabase, keypad, cashDispenser);
                break;
            case TRANSFER: // create new actions.Withdrawal transaction
                temp = new Transfer(currentAccountNumber, screen,
                        bankDatabase, keypad);
                break;
        } // end switch


        return temp; // return the newly created object
    } // end method createTransaction
} // end class hardwares.ATM


/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 * *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/