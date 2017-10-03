package data;

/**
 * Created by saren on 24/9/15.
 */
public class CurrentAccount extends Account {

    private static final double DEFAULT_OVERDRAWN_LIMIT = 10000;
    private double overdrawnLimit;
    private double drawnAmount;

    public CurrentAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance,
                          double overdrawnLimit) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.overdrawnLimit = overdrawnLimit;
    }

    public CurrentAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.overdrawnLimit = DEFAULT_OVERDRAWN_LIMIT;
    }

    @Override
    public void debit(double amount) {
        super.debit(amount);
        drawnAmount += amount;
    }

    public double getOverdrawnLimit() {
        return overdrawnLimit;
    }

    public void setOverdrawnLimit(double overdrawnLimit) {
        this.overdrawnLimit = overdrawnLimit;
    }

    public double getDrawnAmount() {
        return drawnAmount;
    }


}
