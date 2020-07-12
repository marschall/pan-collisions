package com.github.marschall.pancollisions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class CollisionGenerator {

  public static void main(String[] args) {
    Path binRangeFile;
    if (args.length > 0) {
      binRangeFile = Paths.get(args[0]);
    } else {
      binRangeFile = Paths.get("src/main/resources/latest.csv");
    }

    List<BinRange> ranges;
    try {
      ranges = BinRange.readMastercardRanges(binRangeFile);
    } catch (IOException e) {
      throw new RuntimeException("could not parse bin range file " + binRangeFile.toAbsolutePath(), e);
    }

    long totalSize = 0L;
    for (BinRange range : ranges) {
      totalSize = Math.addExact(totalSize, range.size());
    }
    System.out.println(totalSize);

  }

}
