package eAnglerFisherZaros;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.JOptionPane;

public class eGui extends WindowAdapter {

    public static int returnMode;
    public static Component f;

    public static void eGuiDialogueMode() {
        String[] modeSelect  = {"Activate", "Deactivate"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eAnglerFisherZaros.eGui.class.getResource("anglerfish_logo.png")));

        returnMode = JOptionPane.showOptionDialog(f,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "\n"
                        + "Most effective anglerfish catching bot on Zaros! \n"
                        + "Start anywhere with \"Fishing rod\" and \"Sandworms\" in inventory; \n"
                        + "Supported special attack with dragon harpoon equipped. \n"
                        + "\n"
                        + "If you activate anti-ban the XP/hour will drop due \n"
                        + "sleeping times, but your account will be much safer! :) \n"
                        + "\n"
                        + "<html><b>Do you want to activate anti-ban?</b></html>\n",
                "eAnglerFisherZaros by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, eIcon, modeSelect, modeSelect[0]);
    }

    public static void main(String[] args) {
        eGuiDialogueMode();
    }
}

