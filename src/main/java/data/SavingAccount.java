package data;

/**
 * Created by saren on 24/9/15.
 */
public class SavingAccount extends Account {

    private static final double DEFAULT_INTEREST_RATE = 0.001;
    public double interestRate;

    public SavingAccount(int theAccountNumber, int thePIN, double theAvailableBalance,
                         double theTotalBalance, double interestRate) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.interestRate = interestRate;
    }

    public SavingAccount(int theAccountNumber, int thePIN, double theAvailableBalance,
                         double theTotalBalance) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.interestRate = DEFAULT_INTEREST_RATE;
    }

    public double getAnnualInterest() {
        return getTotalBalance() * interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }


}
