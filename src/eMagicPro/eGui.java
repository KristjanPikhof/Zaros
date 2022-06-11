package eMagicPro;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.JOptionPane;

public class eGui extends WindowAdapter {

    public static int returnMode;
    public static int returnSuicide;
    public static int returnNpc;

    public static void eGuiDialogueMode() {
        String[] modeSelect  = {"Only Splashing", "Alch & Splash"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnMode = JOptionPane.showOptionDialog(null,
                "<html><b>Description:</b></html>\n"
                        + "Trains magic effectively while letting you to be away from keyboard. \n"
                        + "You must have required runes and target nearby.\n"
                        + "If you choose Only Splashing the bot won't alch anything.\n"
                        + "If you choose Alch & Splash the bot will perform both tasks for great xp.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, 0, eIcon, modeSelect, modeSelect[0]);
    }

    public static void eGuiDialogueTarget() {
        String[] targetSelect  = {"Duck", "Rat", "Man", "Woman", "Goblin", "Imp", "Chicken", "Cow"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnNpc = JOptionPane.showOptionDialog(null,
                "\n"
                        + "<html><b>Who will be your splashing target?</b></html>\n"
                        + "\n"
                        + "NB! You must select preferred autocast spell from spellbook!\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, 0, eIcon, targetSelect, targetSelect[0]);
    }

    public static void eGuiDialogueSuicide() {
        String[] suicideMode  = {"Activate", "Deactivate"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("mage-book-logo.png")));

        returnSuicide = JOptionPane.showOptionDialog(null,
                "\n"
                        + "<html><b>Do you want to activate antiban?</b></html>\n"
                        + "\n"
                        + "Antiban means that if other players are around bot will \n"
                        + "stop alching task and will proceed only with splashing task\n"
                        + "as long as you are alone again, so you won't look suspicious\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, 0, eIcon, suicideMode, suicideMode[0]);
    }

    public static void main(String[] args) {
        eGuiDialogueMode();
        eGuiDialogueTarget();
        eGuiDialogueSuicide();
    }
}
