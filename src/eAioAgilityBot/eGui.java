package eAioAgilityBot;

import javax.swing.*;
import java.awt.event.*;

public class eGui extends WindowAdapter {

    public static int courseName;

    public static void eGuiDialogue() {
        String[] buttons = {"Canifis"};

        ImageIcon eIcon = new ImageIcon(eAioAgilityBot.eGui.class.getResource("mark-of-grace-logo.png"));

        courseName = JOptionPane.showOptionDialog(null,
                "<html><b>Description:</b></html>\n"
                        + "Rooftops agility courses bot \n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.WARNING_MESSAGE, 0, eIcon, buttons, buttons[0]);
    }

    public static void main(String[] args) {
        eGuiDialogue();
    }
}
