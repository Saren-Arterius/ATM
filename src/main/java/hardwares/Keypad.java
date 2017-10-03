package hardwares;// Represents the keypad of the hardwares.ATM

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Keypad {

    private static final Color disabledColor = Color.decode("#009688");
    private static final Color enabledColor = Color.decode("#26a69a");
    private static final int MAX_DECIMAL_COUNT = 2;
    private final ATMPanel panel;
    private final Object inputLock = new Object();
    private final Object actionInputLock = new Object();
    private ActionListener cancelActionListener;
    private String inputBuffer = "";
    private int actionInput;
    private boolean maskInput = false;
    private boolean enableDot = false;
    private boolean enableDigitInput = true;

    public Keypad(ATMPanel panel) {
        this.panel = panel;
        bindButtons(panel);
    }

    public void setEnableDigitInput(boolean enableDigitInput) {
        this.enableDigitInput = enableDigitInput;
        if (!enableDigitInput) {
            resetInput();
        }
    }

    public void setInputBuffer(String inputBuffer) {
        this.inputBuffer = inputBuffer;
    }

    public Object getInputLock() {
        return inputLock;
    }

    public void setEnableDot(boolean enableDot) {
        this.enableDot = enableDot;
    }

    private void bindButtons(ATMPanel panel) {
        bindDigitButton(panel.getA0Button());
        bindDigitButton(panel.getA00Button());
        bindDigitButton(panel.getA7Button());
        bindDigitButton(panel.getA8Button());
        bindDigitButton(panel.getA9Button());
        bindDigitButton(panel.getA4Button());
        bindDigitButton(panel.getA5Button());
        bindDigitButton(panel.getA6Button());
        bindDigitButton(panel.getA1Button());
        bindDigitButton(panel.getA2Button());
        bindDigitButton(panel.getA3Button());
        bindDigitButton(panel.getDotButton());
        panel.getClearButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resetInput();
            }
        });
        panel.getConfirmButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!inputBuffer.isEmpty()) {
                    synchronized (inputLock) {
                        inputLock.notifyAll();
                    }
                }
            }
        });
        for (final ActionButton actionButton : ActionButton.values()) {
            actionButton.getButton(panel).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    actionInput = actionButton.ordinal() + 1;
                    synchronized (actionInputLock) {
                        actionInputLock.notifyAll();
                    }
                }
            });
        }
    }

    public void resetActionButtons() {
        for (final ActionButton actionButton : ActionButton.values()) {
            JButton button = actionButton.getButton(panel);
            button.setText("");
            button.setEnabled(false);
            button.setBackground(disabledColor);
        }
    }

    public void setActionButtonTextAndEnable(ActionButton actionButton, String text) {
        JButton button = actionButton.getButton(panel);
        button.setEnabled(true);
        button.setText(text);
        button.setBackground(enabledColor);
    }

    public void setMaskInput(boolean maskInput) {
        this.maskInput = maskInput;
    }

    public void setCancelButtonListener(ActionListener actionListener) {
        if (cancelActionListener != null) {
            panel.getCancelButton().removeActionListener(cancelActionListener);
        }
        cancelActionListener = actionListener;
        if (actionListener != null) {
            panel.getCancelButton().addActionListener(actionListener);
        }
    }

    public void resetInput() {
        inputBuffer = "";
        updateInputText();
    }

    public void updateInputText() {
        if (maskInput) {
            panel.getInputField().setText(new String(new char[inputBuffer.length()]).replace("\0", "*"));
        } else {
            panel.getInputField().setText(inputBuffer);
        }
    }

    public void bindDigitButton(JButton button) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!enableDigitInput) {
                    return;
                }
                if (actionEvent.getActionCommand().equals(".")) {
                    if (!enableDot || inputBuffer.contains(".")) {
                        return;
                    }
                }
                if (inputBuffer.contains(".")) {
                    String[] spliced = inputBuffer.split("\\.");
                    if (spliced.length == 2 && spliced[1].length() >= MAX_DECIMAL_COUNT) {
                        return;
                    }
                }
                inputBuffer += actionEvent.getActionCommand();
                updateInputText();
            }
        });
    }

    public int getActionInput() {
        synchronized (actionInputLock) {
            try {
                actionInputLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return actionInput;
    }

    // return an integer value entered by user
    public int getInput() {
        setEnableDigitInput(true);
        synchronized (inputLock) {
            try {
                inputLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int result;
        try {
            result = Integer.parseInt(inputBuffer);
        } catch (NumberFormatException e) {
            resetInput();
            result = getInput();
        }
        resetInput();
        return result;
    } // end method getInput

    public double getDoubleInput() {
        setEnableDigitInput(true);
        synchronized (inputLock) {
            try {
                inputLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double result;
        try {
            result = Double.parseDouble(inputBuffer);
        } catch (NumberFormatException e) {
            resetInput();
            result = getDoubleInput();
        }
        resetInput();
        return result;
    } // end method getInput

    public enum ActionButton {
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM;

        public JButton getButton(ATMPanel panel) {
            switch (this) {
                case LEFT_TOP:
                    return panel.getLtButton();
                case RIGHT_TOP:
                    return panel.getRtButton();
                case LEFT_BOTTOM:
                    return panel.getLbButton();
                case RIGHT_BOTTOM:
                    return panel.getRbButton();
            }
            return null;
        }
    }
} // end class hardwares.Keypad


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