package eMultiPurposeMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.Objects;

public class eGui extends WindowAdapter {

    public static int returnMode;
    public static String returnItem;
    public static Component f;

    public static void eGuiDialogueMode() {
        String[] modeSelect  = {"Crafting", "Fleching"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("eMultiPurposeMaker-logo.png")));

        returnMode = JOptionPane.showOptionDialog(f,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "\n"
                        + "You must choose between crafting or fletching. \n"
                        + "You must setup and start using \"last-preset\" with tool and materials.\n"
                        + "You must setup \"Space\" as making action (before starting bot).\n"
                        + "If you choose \"Crafting\" the bot will click chisel and item in inventory.\n"
                        + "If you choose \"Fletching\" the bot will click knife and item in inventory.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMultiPurposeMaker by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, eIcon, modeSelect, modeSelect[0]);
    }

    public static void eGuiDialogueItem() {

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("eMultiPurposeMaker-logo.png")));

        returnItem = (String) JOptionPane.showInputDialog(f,
                "\n"
                        + "<html><b>What item you want to craft/fletch?</b></html>\n"
                        + "\n"
                        + "You can type full name of item or part of it.\n"
                        + "For example type: \"ame\" to create items from \"Amethyst\",\n"
                        + "you could type \"logs\" for \"Yew logs\" or \"Magic logs\".\n"
                        + "Make sure you don't have other items except tool in inventory.\n"
                        + "\n",
                "Type item name - eMultiPurposeMaker by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, null, "Yew logs");
    }

    public static void main(String[] args) {
        eGuiDialogueMode();
        eGuiDialogueItem();
    }
}
