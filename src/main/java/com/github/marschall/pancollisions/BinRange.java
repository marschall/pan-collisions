package com.github.marschall.pancollisions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.marschall.minicsv.CellSet;
import com.github.marschall.minicsv.CsvParser;
import com.github.marschall.minicsv.ParserConfiguration;
import com.github.marschall.minicsv.Row;

final class BinRange {

  private final Pan start;

  private final long size;

  private BinRange(Pan start, long size) {
    this.start = start;
    this.size = size;
  }

  public long size() {
    return this.size;
  }

  static List<BinRange> readMastercardRanges(Path path) throws IOException {
    ParserConfiguration configuration = ParserConfiguration.builder()
      .delimiter(',')
      .quote('"')
      .ignoreFirstLine()
      .build();
    CsvParser csvParser = new CsvParser(configuration);
    ParseContext parseContext = new ParseContext();

    csvParser.parse(path, StandardCharsets.US_ASCII, parseContext);
    return parseContext.getRanges();
  }

  static final class ParseContext implements Consumer<Row> {

    private final List<BinRange> ranges;

    ParseContext() {
      this.ranges = new ArrayList<>();
    }

    List<BinRange> getRanges() {
      return this.ranges;
    }

    @Override
    public void accept(Row row) {
      CellSet cellSet = row.getCellSet();

      Pan start = null;
      Pan end = null;
      while (cellSet.next()) {
        int columnIndex = cellSet.getColumnIndex();
        if (columnIndex == 3) {
          CharSequence charSequence = cellSet.getCharSequence();
          if (isPan(charSequence)) {
            start = Pan.valueOf(charSequence);
          }
        } else if (columnIndex == 4) {
          CharSequence charSequence = cellSet.getCharSequence();
          if (isPan(charSequence)) {
            end = Pan.valueOf(charSequence);
          }
        } else if (columnIndex > 4) {
          break;
        }
      }
      if ((start != null) && (end != null)) {
        long size = start.difference(end);
        this.ranges.add(new BinRange(start, size));
      }

    }

    private static boolean isPan(CharSequence s) {
      if (s.length() > 16) {
        return false;
      }
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if ((c < '0') || (c > '9')) {
          return false;
        }
      }
      return true;
    }

  }

}
