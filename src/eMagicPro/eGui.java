package eMagicPro;

import javax.swing.*;
import java.awt.event.*;

public class eGui extends WindowAdapter {

    public static int returnValue;
    public static String npcName;
    public static String itemName;

    public static void eGuiDialogue() {
        String[] buttons = {"Only Splashing", "Alch & Splash"};

        ImageIcon eIcon = new ImageIcon(eMagicPro.eGui.class.getResource("mage-book-logo.png"));

        returnValue = JOptionPane.showOptionDialog(null,
                "<html><b>Description:</b></html>\n"
                        + "Trains magic effectively while letting you to be away from keyboard. \n"
                        + "You must have required runes and target nearby.\n"
                        + "If you choose Only Splashing the bot won't alch anything.\n"
                        + "If you choose Alch & Splash the bot will perform both tasks for great xp.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.WARNING_MESSAGE, 0, eIcon, buttons, buttons[0]);
    }

    public static void main(String[] args) {
        eGuiDialogue();
    }
}
