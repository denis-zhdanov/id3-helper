package org.denis.id3.ui;

import net.miginfocom.swing.MigLayout;
import org.denis.id3.model.Id3Info;
import org.denis.id3.service.Id3Applier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Id3Panel extends JPanel {

  private static final String NONE                      = "None";
  private static final int    TEXT_FIELD_COLUMNS_NUMBER = 30;

  private static final String TARGET_DIR_PATH_KEY = "target.dir";

  private final JFileChooser fileChooser    = new JFileChooser();
  private final JTextField   targetDirField = new JTextField(TEXT_FIELD_COLUMNS_NUMBER);
  private final JTextField   composerField  = new JTextField(TEXT_FIELD_COLUMNS_NUMBER);
  private final JTextField   albumField     = new JTextField(TEXT_FIELD_COLUMNS_NUMBER);
  private final JTextField   yearField      = new JTextField(TEXT_FIELD_COLUMNS_NUMBER);
  private final JComboBox    genreBox       = new JComboBox(new Object[]{
    NONE, "Alternative", "Hip Hop/Rap", "Reggae", "Trance", "Русский рок"
  });

  public Id3Panel() {
    setLayout(new MigLayout());
    add(new JLabel("Target dir:"));
    add(targetDirField);
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    JButton chooseTargetDirButton = new JButton("Change");
    chooseTargetDirButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String path = targetDirField.getText();
        if (path != null && !path.isEmpty()) {
          File startDir = new File(path);
          if (startDir.isDirectory()) {
            fileChooser.setCurrentDirectory(startDir);
          }
        }

        int ret = fileChooser.showOpenDialog(Id3Panel.this);
        if (ret == JFileChooser.APPROVE_OPTION) {
          String newDir = fileChooser.getSelectedFile().getAbsolutePath();
          targetDirField.setText(newDir);
          storePreferences();
        }
      }
    });
    add(chooseTargetDirButton, "wrap");

    add(new JLabel("Composer:"));
    add(composerField, "span 2, wrap");

    add(new JLabel("Album:"));
    add(albumField, "span 2, wrap");

    add(new JLabel("Year:"));
    add(yearField, "span 2, wrap");

    add(new JLabel("Genre:"));
    add(genreBox, "span 2, wrap");

    final Id3Applier applier = new Id3Applier();
    JButton goButton = new JButton("Go");
    goButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Id3Info info = getInfo();
        if (info != null) {
          applier.apply(info);
        }
      }
    });
    add(goButton, "span 3");

    loadPreferences();
  }

  private void storePreferences() {
    Preferences preferences = Preferences.userNodeForPackage(getClass());
    String targetDirPath = targetDirField.getText();
    if (targetDirPath != null && !targetDirPath.isEmpty()) {
      preferences.put(TARGET_DIR_PATH_KEY, targetDirPath);
    }
    try {
      preferences.flush();
    }
    catch (BackingStoreException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadPreferences() {
    Preferences preferences = Preferences.userNodeForPackage(getClass());
    String targetDirPath = preferences.get(TARGET_DIR_PATH_KEY, null);
    if (targetDirPath != null) {
      targetDirField.setText(targetDirPath);
    }
  }

  public Id3Info getInfo() {
    String targetDirPath = targetDirField.getText();
    if (targetDirPath == null || targetDirPath.isEmpty()) {
      return null;
    }
    File targetDir = new File(targetDirPath);
    if (!targetDir.isDirectory()) {
      return null;
    }
    File[] toProcess = targetDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith("mp3");
      }
    });
    if (toProcess == null || toProcess.length <= 0) {
      return null;
    }
    Object selectedGenre = genreBox.getSelectedItem();
    String genre = selectedGenre == NONE ? null : selectedGenre.toString();
    String yearAsText = yearField.getText();
    int year = (yearAsText == null || yearAsText.isEmpty()) ? -1 : Integer.parseInt(yearAsText);
    return new Id3Info(composerField.getText(), albumField.getText(), genre, year, Arrays.asList(toProcess));
  }
}
