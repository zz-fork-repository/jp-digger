package jp.sourceforge.stigmata.digger;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    }

    @Test(expected=ClassNotFoundException.class)
    public void testNotExistedClass() throws Exception{
        context.findClass("not.exists.class");
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
}
