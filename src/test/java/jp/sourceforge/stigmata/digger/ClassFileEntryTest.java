package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Haruaki Tamada
 * @version $Revision$
 */
public class ClassFileEntryTest{
    private ClasspathContext context;

    @Before
    public void setup() throws MalformedURLException{
        context = new ClasspathContext();
    }

    @Test
    public void testBasic() throws Exception{
        Class<?> clazz = context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext");
        ClassFileEntry entry = context.findEntry("jp.sourceforge.stigmata.digger.ClasspathContext");

        Assert.assertNotNull(clazz);
        Assert.assertNotNull(entry);

        File file = new File("target/classes/jp/sourceforge/stigmata/digger/ClasspathContext.class");
        Assert.assertEquals("jp.sourceforge.stigmata.digger.ClasspathContext", entry.getClassName());
        Assert.assertEquals(file.toURI().toURL(), entry.getLocation());
    }
}
