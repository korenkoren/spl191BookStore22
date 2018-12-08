import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
public class FutureTest {

    Future<Integer> f;

    @Before
    public void setUp() throws Exception {
         f = new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() throws Exception {
        f.resolve(5);
        assertEquals(f.get().intValue(),5);
    }

    @Test
    public void resolve() throws Exception {
        assertEquals(f.isDone(),true);
    }

    @Test
    public void isDone() throws Exception {
        assertEquals(f.isDone(), false);
        f.resolve(5);
        assertEquals(f.isDone(),true);
    }

    @Test
    public void get1() throws Exception {
        f.resolve(5);
        f.get(1, TimeUnit.NANOSECONDS);
        assertEquals(f.get().intValue(),5);
    }

}