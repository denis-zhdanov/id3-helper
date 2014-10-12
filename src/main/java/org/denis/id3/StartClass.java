package org.denis.id3;

import org.denis.id3.ui.Id3EditorFrame;

import java.awt.*;

public class StartClass {
  public static void main(String[] args) {
    Id3EditorFrame frame = new Id3EditorFrame();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(sizeFraction(screenSize.width), sizeFraction(screenSize.height));
    frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
    frame.setVisible(true);
  }

  private static int sizeFraction(int available) {
    return available / 3;
  }
}
