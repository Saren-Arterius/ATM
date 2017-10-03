package actions;// actions.Withdrawal.java
// Represents a withdrawal hardwares.ATM transaction

import data.BankDatabase;
import hardwares.CashDispenser;
import hardwares.Keypad;
import hardwares.Screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Withdrawal extends Transaction {
    // constant corresponding to menu option to cancel
    public final static int CANCELED = -1;
    private int amount; // amount to withdraw
    private Keypad keypad; // reference to keypad
    private CashDispenser cashDispenser; // reference to cash dispenser

    // actions.Withdrawal constructor
    public Withdrawal(int userAccountNumber, Screen atmScreen,
                      BankDatabase atmBankDatabase, Keypad atmKeypad,
                      CashDispenser atmCashDispenser) {
        // initialize superclass variables
        super(userAccountNumber, atmScreen, atmBankDatabase);

        // initialize references to keypad and cash dispenser
        keypad = atmKeypad;
        cashDispenser = atmCashDispenser;
    } // end actions.Withdrawal constructor

    // perform transaction
    @Override
    public void execute() {
        boolean cashDispensed = false; // cash was not dispensed yet
        double availableBalance; // amount available for withdrawal

        // get references to bank database and screen
        BankDatabase bankDatabase = getBankDatabase();
        Screen screen = getScreen();

        // loop until cash is dispensed or the user cancels
        do {
            // obtain a chosen withdrawal amount from the user
            amount = promptWithdrawalAmount();

            // check whether user chose a withdrawal amount or canceled
            if (amount == CANCELED) {
                screen.displayMessage("Canceling transaction...");
                screen.renderToScreen();
                screen.sleepThenClear(1000);
                return; // return to main menu because user canceled
            }

            // get available balance of account involved
            availableBalance =
                    bankDatabase.getAvailableBalance(getAccountNumber());


            // check whether the user has enough money in the account
            if (amount > availableBalance) {
                screen.displayMessage("Insufficient funds in your account.\nPlease choose a smaller amount.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            // instruct user to take card
            screen.displayDelayedMessage("Preparing cash", 3,
                    bankDatabase.isTestAccount(getAccountNumber()) ? 0 : 500);
            screen.sleepThenClear(1000);

            // check whether the cash dispenser has enough money
            if (!cashDispenser.isSufficientCashAvailable(amount)) {
                screen.displayMessage("Insufficient cash available in the ATM.\nPlease choose a smaller amount.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            screen.displayMessage("Would you like to get a receipt?");
            screen.renderToScreen();
            keypad.setActionButtonTextAndEnable(Keypad.ActionButton.LEFT_BOTTOM, "No");
            keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_BOTTOM, "Yes");
            boolean getReceipt = keypad.getActionInput() == 4;
            screen.clearMessage();
            keypad.resetActionButtons();

            if (getReceipt) {
                screen.displayMessage("Please take you receipt now.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
            }

            // update the account involved to reflect withdrawal
            bankDatabase.debit(getAccountNumber(), amount);

            cashDispenser.dispenseCash(amount); // dispense cash
            cashDispensed = true; // cash was dispensed

            // instruct user to take card
            screen.displayDelayedMessage("Please take your card now", 3,
                    bankDatabase.isTestAccount(getAccountNumber()) ? 0 : 500);
            // instruct user to take cash
            screen.sleepThenClear(1000);
            screen.displayMessage("Please take your cash now.");
            screen.renderToScreen();
            screen.sleepThenClear(2000);
        } while (!cashDispensed);


    } // end method execute

    // display a dialog of withdrawal amounts and the option to cancel;
    // return the chosen amount or 0 if the user chooses to cancel
    private int promptWithdrawalAmount() {
        int userInput = 0; // local variable to store return value

        Screen screen = getScreen(); // get screen reference
        keypad.setCancelButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                keypad.setInputBuffer(String.valueOf(Withdrawal.CANCELED));
                synchronized (keypad.getInputLock()) {
                    keypad.getInputLock().notifyAll();
                }
            }
        });

        // loop while no valid choice has been made
        while (userInput == 0) {
            // display the menu
            screen.displayMessageLine("Please enter withdrawal amount.");
            screen.renderToScreen();
            int input = keypad.getInput(); // get user input through keypad

            // determine how to proceed based on the input value
            if (input == CANCELED) {
                userInput = CANCELED;
                break;
            }

            if (input == 0) {
                screen.clearMessage();
                screen.displayMessage("Invalid withdrawal amount.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            if (input % 100 != 0) {
                keypad.setEnableDigitInput(false);
                screen.clearMessage();
                screen.displayMessage("Input must be multiple of $100.\nInput again.");
                screen.renderToScreen();
                screen.sleepThenClear(2000);
                continue;
            }

            userInput = input;
        } // end while
        screen.clearMessage();
        keypad.setCancelButtonListener(null);
        keypad.setEnableDigitInput(false);
        return userInput; // return withdrawal amount or CANCELED
    } // end method promptWithdrawalAmount
} // end class actions.Withdrawal


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