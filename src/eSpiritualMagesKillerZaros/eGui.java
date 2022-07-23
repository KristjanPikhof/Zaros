package eSpiritualMagesKillerZaros;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.Objects;

public class eGui extends WindowAdapter {

    public static int returnMode;

    public static Component f;

    public static void eGuiDialogueMode() {
        String[] modeSelect  = {"New Start", "Continue"};

        ImageIcon eIcon = new ImageIcon(Objects.requireNonNull(eGui.class.getResource("max-cape.png")));

        returnMode = JOptionPane.showOptionDialog(f,
                "\n"
                        + "<html><b>Description:</b></html>\n"
                        + "\n"
                        + "Kills spiritual mages for dragon boots! \n"
                        + "You must have enough prayer restore potions banked.\n"
                        + "Any dragon dagger spec & piety prayer flick supported.\n"
                        + "Bank last-preset must be selected before usage.\n"
                        + "\n"
                        + "If you choose \"New Start\" then you can start anywhere.\n"
                        + "If you choose \"Continue\" you can continue killing at Zamorak area.\n"
                        + "\n"
                        + "For more information check out Esmaabi on SimpleBot!",
                "eMagicPro by Esmaabi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, eIcon, modeSelect, modeSelect[0]);
    }

    public static void main(String[] args) {
        eGuiDialogueMode();
    }
}
