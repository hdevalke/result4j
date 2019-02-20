package result4j;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void isOk() {
        assertTrue(Result.ok(1).isOk());
        assertFalse(Result.err(1).isOk());
    }

    @Test
    void isErr() {
        assertFalse(Result.ok(1).isErr());
        assertTrue(Result.err(1).isErr());
    }

    @Test
    void ok() {
        assertEquals(Optional.of(1), Result.ok(1).ok());
        assertEquals(Optional.empty(), Result.err(1).ok());
    }

    @Test
    void err() {
        assertEquals(Optional.of(1), Result.err(1).err());
        assertEquals(Optional.empty(), Result.ok(1).err());
    }

    @Test
    void map() {
        assertEquals(Result.ok(2), Result.ok(1).map(i -> i + 1));
        assertEquals(Result.err(1), Result.<Integer, Integer>err(1).map(i -> i + 1));
    }

    @Test
    void mapOrElse() {
        assertEquals(Integer.valueOf(2), Result.<Integer, Integer>ok(1).mapOrElse(i -> i - 1, i -> i + 1));
        assertEquals(Integer.valueOf(0), Result.<Integer, Integer>err(1).mapOrElse(i -> i - 1, i -> i + 1));
    }

    @Test
    void mapErr() {
        assertEquals(Result.ok(1), Result.<Integer, Integer>ok(1).mapErr(i -> i + 1));
        assertEquals(Result.err(2), Result.<Integer, Integer>err(1).mapErr(i -> i + 1));
    }

    @Test
    void and() {
        assertEquals(Result.ok(2), Result.ok(1).and(Result.ok(2)));
        assertEquals(Result.err(1), Result.err(1).and(Result.ok(2)));
    }

    @Test
    void andThen() {
        assertEquals(Result.ok(2), Result.ok(1).andThen(i -> Result.ok(i + 1)));
        assertEquals(Result.err(1), Result.<Integer, Integer>err(1).andThen(i -> Result.ok(i + 1)));
    }

    @Test
    void or() {
        assertEquals(Result.ok(1), Result.ok(1).or(Result.ok(2)));
        assertEquals(Result.ok(2), Result.err(1).or(Result.ok(2)));
    }

    @Test
    void orElse() {
        assertEquals(Result.ok(1), Result.ok(1).orElse(i -> Result.ok(2)));
        assertEquals(Result.ok(2), Result.err(1).orElse(i -> Result.ok(2)));
    }

    @Test
    void unwrapOr() {
        assertEquals(Integer.valueOf(1), Result.ok(1).unwrapOr(2));
        assertEquals(Integer.valueOf(2), Result.<Integer, Integer>err(1).unwrapOr(2));
    }

    @Test
    void unwrapOrElse() {
        assertEquals(Integer.valueOf(1), Result.ok(1).unwrapOrElse(i -> 2));
        assertEquals(Integer.valueOf(2), Result.<Integer, Integer>err(1).unwrapOrElse(i -> 2));
    }

    @Test
    void unwrap() {
        assertEquals(1, Result.ok(1).unwrap());
        assertThrows(Result.ResultException.class, Result.err(1)::unwrap);
    }

    @Test
    void expect() {
        assertEquals(1, Result.ok(1).expect("no error"));
        assertThrows(Result.ResultException.class, () -> Result.err(1).expect("error msg"), "error msg");
    }

    @Test
    void unwrapErr() {
        assertEquals(1, Result.err(1).unwrapErr());
        assertThrows(Result.ResultException.class, Result.ok(1)::unwrapErr);
    }

    @Test
    void expectErr() {
        assertEquals(1, Result.err(1).expectErr("no error"));
        assertThrows(Result.ResultException.class, () -> Result.ok(1).expectErr("error msg"), "error msg");
    }

    @Test
    void iterator() {
        assertFalse(Result.err(1).iterator().hasNext());
        assertEquals(1, Result.ok(1).iterator().next());
    }

    @Test
    void equals() {
        var a = Result.ok(1);
        var b = Result.ok(2);
        var c = Result.err(1);
        var d = Result.err(2);
        var e = Result.ok(1);
        var g = Result.err(1);
        assertNotEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(b, c);
        assertNotEquals(b, d);
        assertNotEquals(c, d);
        assertEquals(a, e);
        assertEquals(c, g);
    }

    @Test
    void testHashCode() {
        var a = Result.ok(1);
        var b = Result.ok(2);
        var c = Result.err(1);
        var d = Result.err(2);
        assertNotEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a.hashCode(), d.hashCode());
        assertNotEquals(b.hashCode(), c.hashCode());
        assertEquals(b.hashCode(), d.hashCode());
        assertNotEquals(c.hashCode(), d.hashCode());
    }

    @Test
    void stream() {
        assertEquals(1, Result.ok(1).stream().count());
        assertEquals(0, Result.err(1).stream().count());
    }
}