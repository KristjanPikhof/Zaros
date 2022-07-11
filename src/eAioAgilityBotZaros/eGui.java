package eAioAgilityBotZaros;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;

public class eGui extends WindowAdapter {

    public static String courseName;

    public static void eGuiDialogue() {
        String[] buttons = new String[] {"Al-Kharid Rooftop","Varrock Rooftop", "Canifis Rooftop", "Seers Rooftop", "Pollnivneach Rooftop", "Rellekka Rooftop", "Ardougne Rooftop"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mark-of-grace-logo.png")));

        courseName = (String) JOptionPane.showInputDialog(null,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "AIO Rooftops agility courses training.\n"
                        + "\n"
                        + "1) It's recommended to zoom out to max.\n"
                        + "2) Start anywhere with normal spellbook\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eAioAgilityBot by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, buttons, buttons[0]);
    }

    public static void main(String[] args) {
        eGuiDialogue();
    }
}
