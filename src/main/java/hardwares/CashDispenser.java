package hardwares;// Represents the cash dispenser of the hardwares.ATM

import java.util.Arrays;

public class CashDispenser {
    // the default initial number of bills in the cash dispenser
    private final static int INITIAL_COUNT = 500;
    private int[] billCounts; // number of $1000, 500, 100 bills remaining

    // no-argument hardwares.CashDispenser constructor initializes billCounts to default
    public CashDispenser() {
        billCounts = new int[]{INITIAL_COUNT, INITIAL_COUNT, INITIAL_COUNT}; // set billCounts attribute to default
    } // end hardwares.CashDispenser constructor

    public CashDispenser(int[] billCounts) {
        this.billCounts = billCounts; // set billCounts attribute to custom
    } // end hardwares.CashDispenser constructor

    public static int getBillTypeAmount(int billType) {
        switch (billType) {
            case 0:
                return 1000;
            case 1:
                return 500;
            default:
                return 100;
        }
    }

    public int[] getBillCountsCopy() {
        return Arrays.copyOf(billCounts, billCounts.length);
    }

    // simulates dispensing of specified amount of cash
    public void dispenseCash(int amount) {
        int[] billsRequired = amountsOfBillsRequired(amount); // number of $100 bills required
        for (int i = 0; i < billCounts.length; i++) {
            billCounts[i] -= billsRequired[i];
        }
    } // end method dispenseCash

    // indicates whether cash dispenser can dispense desired amount
    public boolean isSufficientCashAvailable(int amount) {
        return isSufficientBillsAvailable(amountsOfBillsRequired(amount));
    } // end method isSufficientCashAvailable

    // billsRequired[3] indicates whether there are enough bills that valued equal to amount of money desired
    // If the value is not 0, that means the dispenser can not fulfill the dispense request
    public boolean isSufficientBillsAvailable(int[] billsRequired) {
        return billsRequired[3] == 0;
    } // end method isSufficientCashAvailable

    public int[] amountsOfBillsRequired(int moneyAmount) {
        int[] bills = new int[4];
        for (int i = 0; i < billCounts.length; i++) {
            int amount = getBillTypeAmount(i);
            bills[i] = Math.min(moneyAmount / amount, billCounts[i]);
            moneyAmount -= bills[i] * amount;
        }
        bills[3] = moneyAmount;
        return bills;
    }


} // end class hardwares.CashDispenser


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