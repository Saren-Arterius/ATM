package hardwares;// Represents the screen of the hardwares.ATM

public class Screen {

    private final ATMPanel panel;
    private String textBuffer = "";

    public Screen(ATMPanel panel) {
        this.panel = panel;
    }

    public void displayMessage(String message) {
        textBuffer += message.replace("\n", "<br>");
    }

    public void displayMessageLine(String message) {
        textBuffer += message.replace("\n", "<br>") + "<br>";
    }

    public void renderToScreen() {
        panel.getTextDisplay().setText("<html>" + textBuffer + "</html>");
    }

    // Sleep to simulate the delay
    // There is no delay for test account
    public void displayDelayedMessage(String message, int count, long intervalMS) {
        displayMessage(message);
        renderToScreen();
        try {
            for (int i = 0; i < count; i++) {
                Thread.sleep(intervalMS);
                displayMessage(".");
                renderToScreen();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleepThenClear(long ms) {
        try {
            Thread.sleep(ms);
            clearMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clearMessage() {
        textBuffer = "";
    }

    // display a dollar amount
    public void displayDollarAmount(double amount) {
        displayMessage(String.format("$%,.2f", amount));
    } // end method displayDollarAmount
} // end class hardwares.Screen


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