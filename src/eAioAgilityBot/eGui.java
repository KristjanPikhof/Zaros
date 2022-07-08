package eAioAgilityBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class eGui extends WindowAdapter {

    public static String courseName;

    public static void eGuiDialogue() {
        String[] buttons = new String[] {"Canifis", "Seers", "Pollnivneach"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mark-of-grace-logo.png")));

        courseName = (String) JOptionPane.showInputDialog(null,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "AIO Rooftops agility courses training.\n"
                        + "\n"
                        + "1) It's recommended to zoom out to max.\n"
                        + "2) Last teleport must be chosen agility course.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eAioAgilityBot by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, buttons, buttons[0]);
    }

    public static void main(String[] args) {
        eGuiDialogue();
    }
}
