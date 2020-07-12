package com.github.marschall.pancollisions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.marschall.minicsv.CellSet;
import com.github.marschall.minicsv.CsvParser;
import com.github.marschall.minicsv.Row;

final class PanRange {

  private final Pan start;

  private final int size;

  private PanRange(Pan start, int size) {
    this.start = start;
    this.size = size;
  }

  static List<PanRange> readMastercardRanges(Path path) throws IOException {
    CsvParser csvParser = new CsvParser(',');
    ParseContext parseContext = new ParseContext();

    csvParser.parse(path, StandardCharsets.US_ASCII, parseContext);
    return parseContext.getRanges();
  }

  static final class ParseContext implements Consumer<Row> {

    private final List<PanRange> ranges;

    ParseContext() {
      this.ranges = new ArrayList<>();
    }

    List<PanRange> getRanges() {
      return this.ranges;
    }

    @Override
    public void accept(Row row) {
      CellSet cellSet = row.getCellSet();

      while (cellSet.next()) {
        int columnIndex = cellSet.getColumnIndex();
        Pan start = null;
        Pan end = null;
        if (columnIndex == 3) {
          cellSet.getCharSequence().toString();
        } else if (columnIndex == 4) {
          cellSet.getCharSequence().toString();
        } else if (columnIndex > 4) {
          break;
        }
      }

    }

  }

}
