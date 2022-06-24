package eJadSlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class ExchangeTask extends WindowAdapter {

    public static int returnValue;
    public static int returnPrayer;
    public static Component f;


    public static void ExchangeDialogue() {
        String[] buttons = { "Jad Slayer", "Cape Exchange" };

        ImageIcon overloadIcon = new ImageIcon(Objects.requireNonNull(ExchangeTask.class.getResource("TzTok-Jad-logo.png")));
        returnValue = JOptionPane.showOptionDialog(f,
                "<html><b style =\"color:red;\">Features:</b></html> \n"
                        + "Teleportation to Bank \n"
                        + "Potion Support (Ranging, Prayer, Defence) \n"
                        + "Fire Cape auto trade in. \n"
                        + "Perfect prayers vs TzTok-Jad \n"
                        + "Healer priority to optimize food. \n"
                        + "<html><b style =\"color:red;\">Description:</b></html>\n"
                        + "Slays TzTok-Jad efficiently and effectively! Start with any ranged gear setup. \n"
                        + "For more information check out Trick on SimpleBot!",
                "Overload Scripts - Jad Slayer",
                JOptionPane.WARNING_MESSAGE, 0, overloadIcon, buttons, buttons[0]);
    }

    public static void prayerOptions() {
        String[] buttons = { "Eagle Eye", "Rigour" };

        ImageIcon overloadIcon = new ImageIcon(Objects.requireNonNull(ExchangeTask.class.getResource("TzTok-Jad-logo.png")));
        returnPrayer = JOptionPane.showOptionDialog(f,
                "<html><b style =\"color:red;\">Choose which prayer to use?</b></html> \n"
                        + "If you have unlocked Rigour prayer choose corresponding option \n"
                        + "please choose corresponding option below. \n",
                "Overload Scripts - Jad Slayer",
                JOptionPane.WARNING_MESSAGE, 0, overloadIcon, buttons, buttons[0]);
    }

    public static void main(String[] args) {
        ExchangeDialogue();
        prayerOptions();
    }
}
