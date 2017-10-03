package actions;

import data.BankDatabase;
import hardwares.Keypad;
import hardwares.Screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by saren on 24/9/15.
 */
public class Transfer extends Transaction {
    private final static double CANCELED_AMOUNT = -1;
    private final static int CANCELED = -1;
    private final Keypad keypad;
    private double amount;
    private int targetAccountNumber;

    public Transfer(int userAccountNumber, Screen atmScreen,
                    BankDatabase atmBankDatabase, Keypad atmKeypad) {
        super(userAccountNumber, atmScreen, atmBankDatabase);

        // initialize references to keypad and cash dispenser
        keypad = atmKeypad;
    }

    // perform transaction
    @Override
    public void execute() {
        boolean transferred = false; // cash was not dispensed yet
        double availableBalance; // amount available for withdrawal

        // get references to bank database and screen
        BankDatabase bankDatabase = getBankDatabase();
        Screen screen = getScreen();

        // loop until cash is dispensed or the user cancels
        do {
            targetAccountNumber = promptTargetAccountNumber();

            // check whether user input a target account number or canceled
            if (targetAccountNumber == CANCELED) {
                screen.displayMessage("Canceling transaction...");
                screen.renderToScreen();
                screen.sleepThenClear(1000);
                return; // return to main menu because user canceled
            }

            if (targetAccountNumber == getAccountNumber()) {
                screen.displayMessage("Can not transfer to own account.\nPlease check your input.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            // check whether anotherAccount exists
            if (!bankDatabase.userExists(targetAccountNumber)) {
                screen.displayMessage("Target account does not exist.\nPlease check your input.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            // obtain a chosen withdrawal amount from the user
            amount = promptTransferAmount();

            // check whether user chose a withdrawal amount or canceled
            if (amount == CANCELED_AMOUNT) {
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

            // check whether user confirm the transaction
            if (!promptProceed()) {
                screen.displayMessage("Canceling transaction...");
                screen.renderToScreen();
                screen.sleepThenClear(1000);
                return; // return to main menu because user canceled
            }

            screen.displayDelayedMessage("Transferring", 3, 500);
            screen.sleepThenClear(1000);
            // update the account involved to reflect withdrawal
            bankDatabase.debit(getAccountNumber(), amount);
            bankDatabase.credit(targetAccountNumber, amount);
            transferred = true; // cash was dispensed

            screen.displayMessage("Would you like to get a receipt?");
            screen.renderToScreen();
            keypad.setActionButtonTextAndEnable(Keypad.ActionButton.LEFT_BOTTOM, "No");
            keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_BOTTOM, "Yes");
            boolean getReceipt = keypad.getActionInput() == 4;
            screen.clearMessage();
            keypad.resetActionButtons();

            // instruct user to take cash
            screen.displayMessage("Transfer complete.");
            if (getReceipt) {
                screen.displayMessage("\nPlease take you receipt now.");
            }
            screen.renderToScreen();
            screen.sleepThenClear(3000);
        } while (!transferred);

    } // end method execute

    // display a dialog of transfer amounts and the input to cancel;
    // return the chosen amount or 0 if the user chooses to cancel
    private double promptTransferAmount() {
        double userChoice = 0; // local variable to store return value

        Screen screen = getScreen(); // get screen reference

        // loop while no valid choice has been made
        keypad.setEnableDot(true);
        keypad.setCancelButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                keypad.setInputBuffer(String.valueOf(CANCELED_AMOUNT));
                synchronized (keypad.getInputLock()) {
                    keypad.getInputLock().notifyAll();
                }
            }
        });
        while (userChoice == 0) {
            // display the menu
            screen.displayMessage("Please enter transfer amount.");
            screen.renderToScreen();
            double input = keypad.getDoubleInput(); // get user input through keypad

            // determine how to proceed based on the input value
            if (input == CANCELED_AMOUNT) {
                userChoice = CANCELED_AMOUNT;
                break;
            }

            if (input == 0) {
                screen.clearMessage();
                screen.displayMessage("Invalid transfer amount.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            userChoice = input;
        } // end while
        keypad.setEnableDot(false);
        keypad.setEnableDigitInput(false);
        keypad.setCancelButtonListener(null);
        screen.clearMessage();
        return userChoice; // return withdrawal amount or CANCELED_AMOUNT
    } // end method promptTransferAmount

    // display a dialog of prompting target account number and the option to cancel;
    // return the target account number or 0 if the user chooses to cancel
    private int promptTargetAccountNumber() {
        int targetAccount = 0; // local variable to store return value

        Screen screen = getScreen(); // get screen reference

        // loop while no valid choice has been made
        keypad.setCancelButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                keypad.setInputBuffer(String.valueOf(CANCELED));
                synchronized (keypad.getInputLock()) {
                    keypad.getInputLock().notifyAll();
                }
            }
        });
        while (targetAccount == 0) {
            // display the menu
            screen.displayMessage("Please enter target account number.");
            screen.renderToScreen();
            int input = keypad.getInput(); // get user input through keypad

            // determine how to proceed based on the input value
            if (input == CANCELED) {
                targetAccount = CANCELED;
                break;
            }

            if (input == 0) {
                screen.clearMessage();
                screen.displayMessage("Invalid account number.");
                screen.renderToScreen();
                screen.sleepThenClear(3000);
                continue;
            }

            targetAccount = input;
        } // end while
        keypad.setEnableDigitInput(false);
        keypad.setCancelButtonListener(null);
        screen.clearMessage();
        return targetAccount; // return withdrawal amount or CANCELED_ACCOUNT
    } // end method promptTransferAmount

    // return the target account number or 0 if the user chooses to cancel
    private boolean promptProceed() {
        Screen screen = getScreen();
        screen.displayMessageLine("Transferring: $" + amount);
        screen.displayMessageLine("To account number: " + targetAccountNumber);
        screen.displayMessageLine("\nPress 'Proceed' to proceed.");
        screen.displayMessage("Press 'Cancel transaction' otherwise.");
        screen.renderToScreen();
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.LEFT_BOTTOM, "Cancel transaction");
        keypad.setActionButtonTextAndEnable(Keypad.ActionButton.RIGHT_BOTTOM, "Proceed");
        boolean result = keypad.getActionInput() == 4;
        screen.clearMessage();
        keypad.resetActionButtons();
        return result;
    } // end method promptTransferAmount

}