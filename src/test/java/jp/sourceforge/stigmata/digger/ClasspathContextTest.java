package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test for ClasspathContext class.
 * 
 * @author Haruaki Tamada
 * @version $Revision$ 
 */
public class ClasspathContextTest{
    private ClasspathContext context;

    @Before
    public void setup(){
        context = new ClasspathContext();
    }

    @Test
    public void testBasic() throws ClassNotFoundException{
        Class<?> classFileEntryClass = context.findClass("jp.sourceforge.stigmata.digger.ClassFileEntry");
        Class<?> classpathContextClass = context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext");

        Assert.assertNotNull(classFileEntryClass);
        Assert.assertNotNull(classpathContextClass);
        Assert.assertNotNull(context.findEntry("jp.sourceforge.stigmata.digger.ClassFileEntry"));
        Assert.assertNotNull(context.findEntry("jp.sourceforge.stigmata.digger.ClasspathContext"));
    }

    @Test(expected=ClassNotFoundException.class)
    public void testNotExistedClass() throws Exception{
        context.findClass("not.exists.ClassName");
    }

    @Test
    public void testFindEntryNotExistedClass() throws Exception{
        Assert.assertFalse(context.hasEntry("not.exists.ClassName"));
        Assert.assertNull(context.findEntry("not.exists.ClassName"));
    }

    @Test
    public void testFindEntryNotSearchSystemClasspath() throws Exception{
        context.setIncludeSystemClasses(false);
        Assert.assertFalse(context.hasEntry("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertNull(context.findEntry("jp.sourceforge.stigmata.digger.ClasspathContext"));
    }

    @Test
    public void testFindEntryNotSearchSystemClasspath2() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);
        // fail because classpath was not set.
        Assert.assertFalse(context.hasEntry("org.objectweb.asm.ClassReader"));
        Assert.assertNull(context.findEntry("org.objectweb.asm.ClassReader"));
    }

    @Test
    public void testFindEntryNotSearchSystemClasspath3() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);
        // always success to load
        Assert.assertTrue(context.hasEntry("java.lang.Object"));
        Assert.assertNotNull(context.findClass("java.lang.Object"));
        // success because addClasspath method was called above.
        Assert.assertTrue(context.hasEntry("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertNotNull(context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
    }

    @Test
    public void testFindEntryNotSearchSystemClasspath4() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.addClasspath(new File("target/asm-all-2.2.3.jar").toURI().toURL());
        context.setIncludeSystemClasses(false);

        Assert.assertTrue(context.hasEntry("java.lang.Object"));
        Assert.assertNotNull(context.findClass("java.lang.Object"));
        Assert.assertTrue(context.hasEntry("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertNotNull(context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertTrue(context.hasEntry("org.objectweb.asm.ClassReader"));
        Assert.assertNotNull(context.findClass("org.objectweb.asm.ClassReader"));
    }

    @Test(expected=ClassNotFoundException.class)
    public void testNotSearchSystemClasspath() throws Exception{
        context.setIncludeSystemClasses(false);
        context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext");
    }

    @Test(expected=ClassNotFoundException.class)
    public void testNotSearchSystemClasspath2() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);
        // fail because classpath was not set.
        context.findClass("org.objectweb.asm.ClassReader");
    }

    @Test
    public void testNotSearchSystemClasspath3() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);
        // always success to load
        Assert.assertNotNull(context.findClass("java.lang.Object"));
        // success because addClasspath method was called above.
        Assert.assertNotNull(context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
    }

    @Test
    public void testNotSearchSystemClasspath4() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.addClasspath(new File("target/asm-all-2.2.3.jar").toURI().toURL());
        context.setIncludeSystemClasses(false);
        Assert.assertNotNull(context.findClass("java.lang.Object"));
        Assert.assertNotNull(context.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertNotNull(context.findClass("org.objectweb.asm.ClassReader"));
    }

    @Test
    public void testSubContextBasic() throws ClassNotFoundException{
        ClasspathContext subContext = new ClasspathContext(context);
        Class<?> classFileEntryClass = subContext.findClass("jp.sourceforge.stigmata.digger.ClassFileEntry");
        Class<?> classpathContextClass = subContext.findClass("jp.sourceforge.stigmata.digger.ClasspathContext");

        Assert.assertNotNull(classFileEntryClass);
        Assert.assertNotNull(classpathContextClass);
    }

    @Test(expected=ClassNotFoundException.class)
    public void testSubContextNotExistedClass() throws Exception{
        ClasspathContext subContext = new ClasspathContext(context);
        subContext.findClass("not.exists.class");
    }

    @Test(expected=ClassNotFoundException.class)
    public void testSubContextNotSearchSystemClasspath() throws Exception{
        context.setIncludeSystemClasses(false);

        ClasspathContext subContext = new ClasspathContext(context);
        subContext.findClass("jp.sourceforge.stigmata.digger.ClasspathContext");
    }

    @Test(expected=ClassNotFoundException.class)
    public void testSubContextNotSearchSystemClasspath2() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);

        ClasspathContext subContext = new ClasspathContext(context);
        // fail because classpath was not set.
        subContext.findClass("org.objectweb.asm.ClassReader");
    }

    public void testSubContextNotSearchSystemClasspath3() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.setIncludeSystemClasses(false);

        ClasspathContext subContext = new ClasspathContext(context);
        // always success to load
        Assert.assertNotNull(subContext.findClass("java.lang.Object"));
        // success because addClasspath method was called above.
        Assert.assertNotNull(subContext.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
    }

    @Test
    public void testSubContextNotSearchSystemClasspath4() throws Exception{
        context.addClasspath(new File("target/classes").toURI().toURL());
        context.addClasspath(new File("target/asm-all-2.2.3.jar").toURI().toURL());
        context.setIncludeSystemClasses(false);

        ClasspathContext subContext = new ClasspathContext(context);
        Assert.assertNotNull(subContext.findClass("java.lang.Object"));
        Assert.assertNotNull(subContext.findClass("jp.sourceforge.stigmata.digger.ClasspathContext"));
        Assert.assertNotNull(subContext.findClass("org.objectweb.asm.ClassReader"));
    }
}
