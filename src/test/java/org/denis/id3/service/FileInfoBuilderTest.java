package org.denis.id3.service;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FileInfoBuilderTest {

  private final FileInfoBuilder builder = new FileInfoBuilder();

  @Test
  public void noOp() {
    doTest(Arrays.asList("a.mp3", "b.mp3", "c.mp3"), Arrays.asList("a.mp3", "b.mp3", "c.mp3"));
  }

  @Test
  public void addFileExtension() {
    doTest(Arrays.asList("a", "b", "c"), Arrays.asList("a.mp3", "b.mp3", "c.mp3"));
  }

  @Test
  public void simpleTrackNumber() {
    doTest(Arrays.asList("1 - a.mp3", "2 - b.mp3", "3 - c.mp3"), Arrays.asList("a.mp3", "b.mp3", "c.mp3"));
    doTest(Arrays.asList("01 - a.mp3", "02 - b.mp3", "03 - c.mp3"), Arrays.asList("a.mp3", "b.mp3", "c.mp3"));
  }

  @Test
  public void removeRepeating() {
    doTest(Arrays.asList("01 - Artist - a.mp3", "02 - Artist - b.mp3", "03 - Artist - c.mp3"), Arrays.asList("a.mp3", "b.mp3", "c.mp3"));
  }

  @Test
  public void dots() {
    doTest(Arrays.asList("01. Foreword.mp3", "02. Don't say.mp3"), Arrays.asList("Foreword.mp3", "Don't say.mp3"));
  }

  private void doTest(List<String> initial, List<String> expected) {
    List<File> initialFiles = new ArrayList<File>();
    for (String s : initial) {
      initialFiles.add(new File(s));
    }
    List<FileInfo> fileInfos = builder.build(initialFiles);
    assertEquals(expected.size(), fileInfos.size());
    for (int i = 0; i < fileInfos.size(); i++) {
      assertEquals(expected.get(i), fileInfos.get(i).getFileName());
    }
  }
}