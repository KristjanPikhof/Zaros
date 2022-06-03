package eJadSlayer;

import javax.swing.*;

//import java.awt.Image;
import java.awt.event.*;

public class ExchangeTask extends WindowAdapter {

    public static int returnValue;


    public static void ExchangeDialogue()
    {
        String[] buttons = { "Jad Slayer", "Cape Exchange" };

        ImageIcon overloadIcon = new ImageIcon(ExchangeTask.class.getResource("overload2.png"));
        /*
         * Image resizedJad = jadIcon.getImage(); Image scaledImage =
         * resizedJad.getScaledInstance(100, 100, Image.SCALE_DEFAULT); Icon scaledIcon
         * = new ImageIcon( scaledImage );
         */

        returnValue = JOptionPane.showOptionDialog(null,
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

    public static void main(String[] args) {
        ExchangeDialogue();
    }
}
