import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    BookInventoryInfo[] bookInfo;

    @Before
    public void setUp() throws Exception {
       bookInfo=new BookInventoryInfo[]{new BookInventoryInfo("Harry Potter", 1, 90), new BookInventoryInfo("The Hunger Games", 90, 102), new BookInventoryInfo("SPL", 0, 102)};

    }
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() throws Exception {
        Inventory i1=Inventory.getInstance();
        if (i1 == null)
            throw new NullPointerException();
        Inventory i2=Inventory.getInstance();
        if(!(i1.equals(i2)))
            throw new Exception("more than single instance");
    }

    @Test
    public void load() throws Exception {
        Inventory i=Inventory.getInstance();
        i.load(bookInfo);
        assertEquals(i.checkAvailabiltyAndGetPrice("Harry Potter"), 90);
        assertEquals(i.checkAvailabiltyAndGetPrice("The Hunger Games"), 102);
        assertEquals(i.checkAvailabiltyAndGetPrice("SPL"), -1);
    }

    @Test
    public void take() throws Exception {
        Inventory i=Inventory.getInstance();
        assertEquals(i.take("Harry Potter").name(),"SUCCESSFULLY_TAKEN");
        assertEquals(i.take("The Hunger Games").name(),"SUCCESSFULLY_TAKEN");
        assertEquals(i.take("Harry Potter").name(),"NOT_IN_STOCK");//see if the books exists after first order
        assertEquals(i.take("SPL").name(),"NOT_IN_STOCK");
    }

    @Test
    public void checkAvailabiltyAndGetPrice() throws Exception {
        Inventory i=Inventory.getInstance();
        assertEquals(i.checkAvailabiltyAndGetPrice("Harry Potter"), 90);
        assertEquals(i.checkAvailabiltyAndGetPrice("The Hunger Games"), 102);
        assertEquals(i.checkAvailabiltyAndGetPrice("SPL"), -1);
    }

    @Test
    public void printInventoryToFile() throws Exception {//todo how to check if what it printed is correct
    }

}