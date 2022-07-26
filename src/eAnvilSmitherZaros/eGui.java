package eAnvilSmitherZaros;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.Objects;

public class eGui extends WindowAdapter {
    public static String returnItem;
    public static Component f;

    public static void eGuiDialogueTarget() {
        String[] targetSelect  = new String[] {"Sword", "Platebody", "Dart tips"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("Smithing_cape_logo.png")));

        returnItem = (String) JOptionPane.showInputDialog(f,
                "\n"
                        + "<html><b>What item you want to smith?</b></html>\n"
                        + "\n"
                        + "Before starting eAnvilSmitherZaros by Esmaabi you must\n"
                        + "1. have full inventory or bars + hammer.\n"
                        + "2. set the Last-preset to the same inventory.\n"
                        + "\n"
                        + "Randomized sleeping times included!\n"
                        + "Start near ::dzone anvil or in Varrock west bank!\n"
                        + "\n",
                "What item you want to smith? - eAnvilSmitherZaros by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, targetSelect, targetSelect[2]);
    }


    public static void main(String[] args) {
        eGuiDialogueTarget();
    }
}

