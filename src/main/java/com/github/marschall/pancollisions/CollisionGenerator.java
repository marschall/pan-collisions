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

import com.github.marschall.pancollisions.Pan.MutableHasher;

public final class CollisionGenerator {

  private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.0000");

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
    HyperLogLog hyperLogLog = new HyperLogLog();
    AtomicLong generated = new AtomicLong();
    for (BinRange range : ranges) {
      threadPool.submit(() -> enumerateBinRange(range, hyperLogLog, generated, totalSize));
    }
    threadPool.awaitTermination(365, TimeUnit.DAYS);
    System.out.println("finished, total pans hashed: " + generated.longValue());
    System.out.println("number of unique hashes: " + hyperLogLog.size());
    System.out.println("update conflicts observed: " + hyperLogLog.getAndResetConflicts());
  }

  private static void enumerateBinRange(BinRange range, HyperLogLog hyperLogLog, AtomicLong generatedAccumulator, long totalSize) {
    long generated = 0L;Pan pan = range.getStart();
    MutableHasher hasher = pan.createMutableHasher();
    for (int i = 0; i < range.size(); i++) {
      MutableI160 hash = hasher.hash(pan);
      hyperLogLog.add(hash);
      generated += 1L;
      pan.increment();
    }
    long totalGenerated = generatedAccumulator.addAndGet(generated);
    double percent = ((double) totalGenerated / (double) totalSize) * 100.0d;
    System.out.println("finished range, pans hashed: " + generated + " total pans hashed: " + totalGenerated + " progres: " + PERCENT_FORMAT.format(percent) + "%");
  }

}
