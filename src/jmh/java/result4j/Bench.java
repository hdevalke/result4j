package result4j;

import org.openjdk.jmh.annotations.Benchmark;
import result4j.v2.Resultv2;

public class Bench {

    @Benchmark
    public Resultv2<Integer,Object> generateOkResultv2() {
        return Resultv2.ok(5).map(i -> i + 5);
    }

    @Benchmark
    public Result<Integer,Object> generateOkResult() {
        return Result.ok(5).map(i -> i + 5);
    }
}
