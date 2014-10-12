package org.denis.id3.service;

class FileInfo {
  public final String name;
  public final String extension;

  FileInfo(String name, String extension) {
    this.name = name;
    this.extension = extension;
  }

  String getFileName() {
    return name + "." + extension;
  }
}
