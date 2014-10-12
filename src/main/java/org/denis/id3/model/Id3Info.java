package org.denis.id3.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Id3Info {

  private final List<File> filesToProcess = new ArrayList<File>();

  private final String composer;
  private final String album;
  private final String genre;
  private final int    year;

  public Id3Info(String composer, String album, String genre, int year, Collection<File> filesToProcess) {
    this.composer = composer;
    this.album = album;
    this.genre = genre;
    this.year = year;
    this.filesToProcess.addAll(filesToProcess);
  }

  public String getComposer() {
    return composer;
  }

  public String getAlbum() {
    return album;
  }

  public String getGenre() {
    return genre;
  }

  public int getYear() {
    return year;
  }

  public List<File> getFilesToProcess() {
    return filesToProcess;
  }
}
