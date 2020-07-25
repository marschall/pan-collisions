package com.github.marschall.pancollisions;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;

public final class CollisionGenerator {

  private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.0000");

//  private static final long MAX = 1_000_000_000L;
  private static final long MAX = 500_000_000L;
//  private static final long MAX = 1_000_000L;

  public static void main(String[] args) throws InterruptedException {
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

    generateCollisionsHyperLogLog(ranges, totalSize);
  }

  private static void generateCollisionsHyperLogLog(List<BinRange> ranges, long totalSize) throws InterruptedException {
    ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    HyperLogLog hyperLogLog = new HyperLogLog(128);
    AtomicLong generated = new AtomicLong();
    for (BinRange range : ranges) {
      threadPool.submit(() -> enumerateBinRange(range, hyperLogLog, generated, totalSize));
    }
    threadPool.awaitTermination(365, TimeUnit.DAYS);
    System.out.println("finished, total pans hashed: " + generated.longValue());
    System.out.println("number of unique hashes: " + hyperLogLog.size());
  }

  private static void enumerateBinRange(BinRange range, HyperLogLog hyperLogLog, AtomicLong generatedAccumulator, long totalSize) {
    long generated = 0L;Pan pan = range.getStart();
    Hasher hasher = pan.createHasher();
    for (int i = 0; i < range.size(); i++) {
      I160 hash = hasher.hash(pan);
      hyperLogLog.add(hash);
      generated += 1L;
      pan.increment();
    }
    long totalGenerated = generatedAccumulator.addAndGet(generated);
    double percent = ((double) totalGenerated / (double) totalSize) * 100.0d;
    System.out.println("finished range, pans hashed: " + generated + " total pans hashed: " + totalGenerated + " progres: " + PERCENT_FORMAT.format(percent) + "%");
  }

  private static void generateCollisionsSet(List<BinRange> ranges) {
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
      double percent = ((double) generated / (double) MAX) * 100.0d;
      System.out.println("finished range, total pans hashed: " + generated + " progres: " + PERCENT_FORMAT.format(percent) + "%");
    }
  }

}
