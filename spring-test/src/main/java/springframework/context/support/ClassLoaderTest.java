// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/5/17 21:35
 **/
public class ClassLoaderTest {


    public static void main(String[] args) throws Exception {

        /**
         * @see sun.misc.Launcher.AppClassLoader
         */

        URLStreamHandler jarHandler = new Factory().getJarHandler();

        String paths = System.getProperty("java.class.path");
        for (String path : paths.split(":")) {
            if (path.endsWith("jar")) {

                URL jarUrl = new URL("jar", "", -1, path + "!/", jarHandler);
                URLConnection urlConnection = jarUrl.openConnection();
                System.out.println(urlConnection.getContentType());


            }
            System.out.println(path);
        }

    }

    private static class Factory implements URLStreamHandlerFactory {
        private static String PREFIX = "sun.net.www.protocol";

        private Factory() {
        }

        private URLStreamHandler getJarHandler() {
            URLStreamHandler handler = createURLStreamHandler("jar");
            return handler;
        }

        public URLStreamHandler createURLStreamHandler(String var1) {
            String var2 = PREFIX + "." + var1 + ".Handler";
            try {
                Class var3 = Class.forName(var2);
                return (URLStreamHandler)var3.newInstance();
            } catch (ReflectiveOperationException var4) {
                throw new InternalError("could not load " + var1 + "system protocol handler", var4);
            }
        }
    }
}