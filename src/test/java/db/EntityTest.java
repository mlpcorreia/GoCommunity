package db;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Carlos
 */
public class EntityTest {
    
    @Test
    public void testDummyEntity() {
        TestEntityClass t = new TestEntityClass();
        t.setId(1L);
        t.setWord("x");
        assertEquals((long) 1,(long) t.getId());
        assertEquals("x",t.getWord());
        assertEquals(new Long(1).hashCode(),t.getId().hashCode());
        TestEntityClass t2 = new TestEntityClass();
        t2.setId(1L);
        t2.setWord("x");
        assertTrue(t.equals(t2));
        assertEquals("db.TestEntityClass[ id=1 ]",t.toString());
    }
}
