package jp.sourceforge.stigmata.digger;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class WarClassLoaderTest{
    private WarClassLoader loader;
    private URL url;

    @Before
    public void setUp() throws Exception{
        url = new File("target/test-classes/resources/samplewar.war").toURI().toURL();
    }

    @Test
    public void testBasic() throws Exception{
        loader = new WarClassLoader(url);

        URL url1 = loader.findResource("META-INF/MANIFEST.MF");
        Assert.assertNotNull(url1);

        URL url2 = loader.findResource("src/HelloWorld.java");
        Assert.assertNotNull(url2);

        Class<?> helloWorld = loader.loadClass("HelloWorld");
        Assert.assertEquals("HelloWorld", helloWorld.getName());
    }
}
