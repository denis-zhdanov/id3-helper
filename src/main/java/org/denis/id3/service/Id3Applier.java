package org.denis.id3.service;

import org.denis.id3.model.Id3Info;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.id3.ID3v22Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Id3Applier {

  private final FileInfoBuilder fileInfoBuilder = new FileInfoBuilder();

  public void apply(Id3Info info) {
    try {
      doApply(info);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void doApply(Id3Info info)
    throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException
  {
    List<File> filesToProcess = info.getFilesToProcess();
    if (filesToProcess.isEmpty()) {
      return;
    }
    List<FileInfo> fileInfos = fileInfoBuilder.build(filesToProcess);
    for (int i = 0; i < filesToProcess.size(); i++) {
      File file = filesToProcess.get(i);
      FileInfo fileInfo = fileInfos.get(i);
      AudioFile audioFile = AudioFileIO.read(file);
      Tag tag = audioFile.getTag();
      boolean setTag = false;
      if (tag == null) {
        setTag = true;
        tag = new ID3v22Tag();
      }
      tag.setField(FieldKey.TITLE, fileInfo.name);
      tag.setField(FieldKey.ALBUM, info.getAlbum());
      tag.setField(FieldKey.YEAR, String.valueOf(info.getYear()));
      try {
        tag.setField(FieldKey.ALBUM_ARTIST, info.getComposer());
      } catch (Exception ignore) {
      }
      tag.setField(FieldKey.ARTIST, info.getComposer());
      if (info.getGenre() != null) {
        tag.setField(FieldKey.GENRE, info.getGenre());
      }
      tag.setField(FieldKey.TRACK, String.valueOf(i + 1));
      try {
        tag.setField(FieldKey.TRACK_TOTAL, String.valueOf(filesToProcess.size()));
      } catch (Exception ignore) {
      }
      tag.setField(FieldKey.COMMENT, ""); // Reset comment
      if (setTag) {
        audioFile.setTag(tag);
      }
      AudioFileIO.write(audioFile);

      String desiredFileName = fileInfo.getFileName();
      if (!desiredFileName.equals(file.getName())) {
        boolean ok = file.renameTo(new File(file.getParent(), desiredFileName));
        assert ok;
      }
    }
  }
}
