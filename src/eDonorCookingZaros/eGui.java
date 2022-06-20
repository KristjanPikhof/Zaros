package eDonorCookingZaros;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.JOptionPane;

public class eGui extends WindowAdapter {
    public static String returnItem;
    public static Component f;

    public static void eGuiDialogueTarget() {
        String[] targetSelect  = new String[] {"Raw shrimps", "Raw anchovies", "Raw sardine", "Raw herring", "Raw mackerel", "Raw trout", "Raw salmon", "Raw tuna", "Raw lobster", "Raw bass", "Raw swordfish", "Raw karambwan", "Raw monkfish", "Raw shark", "Raw anglerfish"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eDonorCookingZaros.eGui.class.getResource("Fishing_cape.png")));

        returnItem = (String) JOptionPane.showInputDialog(f,
                "\n"
                        + "<html><b>What fish you will be cooking?</b></html>\n"
                        + "\n"
                        + "Before starting eDonorCookingZaros by Esmaabi you must\n"
                        + "set the Last-preset to chosen fish.\n"
                        + "\n"
                        + "Anti-ban features included!\n"
                        + "Zoom out to maximum, else bot will handle!\n"
                        + "\n",
                "Choose fish to cook - eDonorCookingZaros by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, targetSelect, targetSelect[12]);
    }


    public static void main(String[] args) {
        eGuiDialogueTarget();
    }
}

