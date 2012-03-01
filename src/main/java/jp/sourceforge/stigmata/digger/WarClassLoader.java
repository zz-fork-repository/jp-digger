package jp.sourceforge.stigmata.digger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * ClassLoader for war file.
 * A war file layouts classes in /WEB-INF/classes directory.
 * Therefore, plain jar class loader cannot load classes included in war file.
 * Because plain jar class loader only loads classes in top directory.
 * Then, this class loader can load classes included in a plain jar file and a war file.
 * 
 * @author Haruaki Tamada
 */
public class WarClassLoader extends URLClassLoader{
    public WarClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory){
        super(urls, parent, factory);
    }

    public WarClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
    }

    public WarClassLoader(URL... urls){
        super(urls);
    }

    public WarClassLoader(ClassLoader parent, URL... urls){
        this(urls, parent);
    }

    public WarClassLoader(ClassLoader parent, URLStreamHandlerFactory factory, URL... urls){
        this(urls, parent, factory);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException{
        Class<?> clazz = null;
        try{
            clazz = super.findClass(name);
        } catch(ClassNotFoundException e){
            String path = "WEB-INF/classes/" + name.replace('.', '/') + ".class";
            for(URL url: getURLs()){
                if(url.toString().endsWith(".war")){
                    InputStream in = null;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try{
                        URL newurl = new URL("jar:" + url + "!/" + path);
                        in = newurl.openStream();
                        byte[] data = new byte[256];
                        int read = 0;
                        while((read = in.read(data, 0, data.length)) != -1){
                            out.write(data, 0, read);
                        }
                        byte[] classdata = out.toByteArray();
                        clazz = defineClass(name, classdata, 0, classdata.length);
                    } catch(IOException exp){
                        throw e;
                    } finally{
                        if(in != null){
                            try{ in.close(); }
                            catch(IOException exception){
                                throw new InternalError(exception.getMessage());
                            }
                        }
                        try{ 
                            out.close();
                        } catch(IOException exception){
                            throw new InternalError(exception.getMessage());
                        }
                    }
                    break;
                }
            }
        }
        if(clazz == null){
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }
}
