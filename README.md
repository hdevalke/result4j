# result4j

A Result type for java. Inspired by the Rust Result type.

```java
import static result4j.Result.ok;
import static result4j.Result.err;

public class Main {

    public static void main(String... args) {
        Result<Integer, Integer> good = ok(1);
        assert 2 == good.map(i -> i + 1).unwrapOr(0);

        Result<Integer, Integer> bad = err(1);
        assert 0 == bad.map(i -> i + 1).unwrapOr(0);
    }
}
```
