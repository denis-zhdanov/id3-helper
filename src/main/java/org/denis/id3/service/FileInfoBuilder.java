package org.denis.id3.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInfoBuilder {

  private static final Pattern PREFIX = Pattern.compile("[\\d ]+\\s*[\\-\\.]\\s*");

  public List<FileInfo> build(List<File> baseFiles) {
    List<TmpFileInfo> tmp = stripTrackNumbers(baseFiles);
    stripRepeatingTokens(tmp);
    List<FileInfo> result = new ArrayList<FileInfo>();
    for (TmpFileInfo info : tmp) {
      result.add(info.toFileInfo());
    }
    return result;
  }

  private static List<TmpFileInfo> stripTrackNumbers(List<File> baseFiles) {
    List<TmpFileInfo> result = new ArrayList<TmpFileInfo>();
    for (File file : baseFiles) {
      String name = file.getName();
      Matcher matcher = PREFIX.matcher(name);
      int i = name.lastIndexOf('.');
      String extension = i >= 0 ? name.substring(i + 1) : "mp3";
      String nameToUse;
      if (matcher.find() && matcher.start() == 0) {
        nameToUse = i > 0 ? name.substring(matcher.end(), i) : name.substring(matcher.end());
      } else {
        nameToUse = i > 0 ? name.substring(0, i) : name;
      }
      nameToUse = nameToUse.substring(0, 1) + nameToUse.substring(1).toLowerCase();
      result.add(new TmpFileInfo(nameToUse, extension));
    }
    return result;
  }

  private static void stripRepeatingTokens(List<TmpFileInfo> infos) {
    if (infos.size() <= 1) {
      return;
    }
    List<List<Token>> matrix = new ArrayList<List<Token>>();
    int maxTokensToCheck = Integer.MAX_VALUE;
    for (TmpFileInfo info : infos) {
      List<Token> tokens = parse(info.name);
      matrix.add(tokens);
      maxTokensToCheck = Math.min(maxTokensToCheck, tokens.size());
    }
    int tokensToDrop = calculateNumberOfTokensToDrop(matrix, maxTokensToCheck);

    if (tokensToDrop > 0) {
      for (int i = 0; i < matrix.size(); i++) {
        List<Token> tokens = matrix.get(i);
        StringBuilder buffer = new StringBuilder();
        for (int j = tokensToDrop; j < tokens.size(); j++) {
          buffer.append(tokens.get(j).data);
        }
        infos.get(i).name = buffer.toString();
      }
    }
  }

  private static List<Token> parse(String s) {
    if (s.isEmpty()) {
      return Collections.emptyList();
    }
    List<Token> result = new ArrayList<Token>();
    int startOffset = 0;
    TokenType activeType = isSeparator(s.charAt(0)) ? TokenType.SEPARATOR : TokenType.WORD;
    for (int i = 1; i < s.length(); i++) {
      TokenType currentType = isSeparator(s.charAt(i)) ? TokenType.SEPARATOR : TokenType.WORD;
      if (currentType == activeType) {
        continue;
      }
      result.add(new Token(activeType, s.substring(startOffset, i)));
      activeType = currentType;
      startOffset = i;
    }
    if (startOffset < s.length()) {
      result.add(new Token(activeType, s.substring(startOffset)));
    }
    return result;
  }

  private static boolean isSeparator(char c) {
    return c == ' ' || c == '-' || c == ':';
  }

  private static int calculateNumberOfTokensToDrop(List<List<Token>> matrix, int maxTokensToCheck) {
    int tokensToDrop = 0;
    for (int i = 0; i < maxTokensToCheck; i++) {
      Token baseToken = null;
      for (List<Token> tokens : matrix) {
        if (baseToken == null) {
          baseToken = tokens.get(i);
          continue;
        }
        Token token = tokens.get(i);
        if (baseToken.type != token.type) {
          return tokensToDrop;
        }
        if (baseToken.type == TokenType.WORD && !baseToken.data.equalsIgnoreCase(token.data)) {
          return tokensToDrop;
        }
      }
      tokensToDrop++;
    }
    return tokensToDrop;
  }

  private static class TmpFileInfo {
    final String extension;
    String name;

    TmpFileInfo(String name, String extension) {
      this.name = name;
      this.extension = extension;
    }

    public FileInfo toFileInfo() {
      return new FileInfo(name, extension);
    }

    @Override
    public String toString() {
      return name + "." + extension;
    }
  }

  private enum TokenType {
    WORD, SEPARATOR
  }

  private static class Token {
    final TokenType type;
    final String data;

    Token(TokenType type, String data) {
      this.type = type;
      this.data = data;
    }

    @Override
    public String toString() {
      return type + ": '" + data + "'";
    }
  }
}
