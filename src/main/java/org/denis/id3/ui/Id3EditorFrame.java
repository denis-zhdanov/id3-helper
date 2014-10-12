package org.denis.id3.ui;

import javax.swing.*;
import java.awt.*;

public class Id3EditorFrame extends JFrame {

  public Id3EditorFrame() throws HeadlessException {
    setContentPane(new Id3Panel());
  }
}
