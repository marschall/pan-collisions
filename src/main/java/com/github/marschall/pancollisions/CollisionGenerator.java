package com.github.marschall.pancollisions;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;

public final class CollisionGenerator {

//  private static final long MAX = 1_000_000_000L;
  private static final long MAX = 500_000_000L;
//  private static final long MAX = 1_000_000L;

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
//    Set<String> countries = Set.of("DEU", "AUT", "CHE");
    Set<String> countries = Set.of("CHE");
    ranges = ranges.stream().filter(r -> countries.contains(r.getCountry())).collect(toList());

    long totalSize = 0L;
    for (BinRange range : ranges) {
      totalSize = Math.addExact(totalSize, range.size());
    }
    System.out.println("potential number of PANs: " + totalSize);

    generateCollisions(ranges);

  }

  private static void generateCollisions(List<BinRange> ranges) {
    MutableBag<I160> collisions = HashBag.newBag(Math.toIntExact(MAX));
    long generated = 0L;
    for (BinRange range : ranges) {
      Pan pan = range.getStart();
      Hasher hasher = pan.createHasher();
      for (int i = 0; i < range.size(); i++) {
        I160 hash = hasher.hash(pan);
        if (collisions.addOccurrences(hash, 1) > 1) {
          System.out.println("XXX collision for: " + pan);
        }
        if (generated > MAX) {
          System.out.println("limit reached after " + MAX + " PANs, aborting");
          return;
        }
        generated += 1L;
        pan.increment();
      }
      System.out.println("finished range, total pans hashed: " + generated + " progres: " + ((double) generated / (double) MAX) + 100.0d + "%");
    }
  }

}
