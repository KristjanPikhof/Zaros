package eMagicPro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.JOptionPane;

public class eGui extends WindowAdapter {

    public static int returnMode;
    public static int returnSuicide;
    public static String returnNpc;
    public static String returnItem;
    public static Component f;

    public static void eGuiDialogueMode() {
        String[] modeSelect  = {"Only Splashing", "Alch & Splash"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnMode = JOptionPane.showOptionDialog(f,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "\n"
                        + "Trains magic effectively while letting you to be away from keyboard. \n"
                        + "You must have required runes and target nearby.\n"
                        + "If you choose \"Only Splashing\" the bot will perform only splashing task.\n"
                        + "If you choose \"Alch & Splash\" the bot will perform both tasks for great xp.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, eIcon, modeSelect, modeSelect[0]);
    }

    public static void eGuiDialogueTarget() {
        String[] targetSelect  = new String[] {"Duck", "Rat", "Man", "Woman", "Goblin", "Imp", "Chicken", "Cow"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnNpc = (String) JOptionPane.showInputDialog(f,
                "\n"
                        + "<html><b>Who will be your splashing target?</b></html>\n"
                        + "\n"
                        + "Before starting eMagicPro by Esmaabi you must\n"
                        + "select preferred autocast spell from combat tab\n"
                        + "and choose splashing target from below:\n"
                        + "\n",
                "Choose NPC to splash - eMagicPro by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, targetSelect, targetSelect[0]);
    }

    public static void eGuiDialogueItem() {

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnItem = (String) JOptionPane.showInputDialog(f,
                "\n"
                        + "<html><b>What do you want to alch?</b></html>\n"
                        + "\n"
                        + "You can type full name of item or part of it.\n"
                        + "For example type: \"mind\" to alch \"Mind rune\"\n"
                        + "you could type \"arrow\" for \"Arrow shafts\" or \"Iron arrow\".\n"
                        + "\n",
                "Type item name to alch - eMagicPro by Esmaabi",
                JOptionPane.WARNING_MESSAGE, eIcon, null, "arrow");
    }

    public static void eGuiDialogueSuicide() {
        String[] suicideMode  = {"Activate", "Deactivate"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnSuicide = JOptionPane.showOptionDialog(f,
                "\n"
                        + "<html><b>Do you want to activate anti-ban?</b></html>\n"
                        + "\n"
                        + "Anti-ban will activate when other players are near you bot will\n"
                        + "stop alching task and will proceed only with splashing task\n"
                        + "as long as you are not alone, so you won't look suspicious.\n"
                        + "\n",
                "Anti-ban option - eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, eIcon, suicideMode, suicideMode[0]);
    }

    public static void main(String[] args) {
        eGuiDialogueMode();
        eGuiDialogueTarget();
        eGuiDialogueSuicide();
        eGuiDialogueItem();
    }
}
