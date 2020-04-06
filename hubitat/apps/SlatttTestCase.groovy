class SlatttTestCase extends GroovyTestCase
{
    void testAssertions()
    {
        def slattt = new SLATTT();
        assertTrue(1 == 1)
        assertEquals("test", "test")

        def x = "42"
        assertNotNull "x must not be null", x
        assertNull null

        assertSame x, x
    }
}
