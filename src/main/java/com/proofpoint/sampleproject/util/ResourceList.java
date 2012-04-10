package com.proofpoint.sampleproject.util;

import com.proofpoint.log.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath
 */
public class ResourceList
{
    private static final Logger log = Logger.get(ResourceList.class);

    /**
     * for all elements of java.class.path get a Collection of resources Pattern pattern = Pattern.compile(".*"); gets all
     * resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Map<String, InputStream> getResources(Pattern pattern)
    {
        Map<String, InputStream> retval = new HashMap<String, InputStream>();
        String classPath = System.getProperty("java.class.path", ".");
        String[] classPathElements = classPath.split(":");
        log.debug(classPath);

        for (String element : classPathElements) {
            Map<String, InputStream> tmpFileMap = getResources(element, pattern);
            for (Entry<String, InputStream> myFile : tmpFileMap.entrySet()) {
                retval.put(myFile.getKey(), myFile.getValue());
            }
        }

        return retval;
    }

    public static Map<String, InputStream> getResources(String element, Pattern pattern)
    {
        Map<String, InputStream> retval = new HashMap<String, InputStream>();
        File file = new File(element);

        if (file.isDirectory()) {
            Map<String, InputStream> tmpFileMap = getResourcesFromDirectory(file, pattern);
            for (Entry<String, InputStream> myFile : tmpFileMap.entrySet()) {
                retval.put(myFile.getKey(), myFile.getValue());
            }
        }
        else {
            Map<String, InputStream> tmpFileMap = getResourcesFromJarFile(file, pattern);
            for (Entry<String, InputStream> myFile : tmpFileMap.entrySet()) {
                retval.put(myFile.getKey(), myFile.getValue());
            }
        }

        return retval;
    }

    public static Map<String, InputStream> getResourcesFromJarFile(File file, Pattern pattern)
    {
        Map<String, InputStream> retval = new HashMap<String, InputStream>();
        ZipFile zf;

        try {
            zf = new ZipFile(file);
        }
        catch (ZipException e) {
            throw new Error(e);
        }
        catch (IOException e) {
            throw new Error(e);
        }

        Enumeration e = zf.entries();

        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            String fileName = ze.getName();

            boolean accept = pattern.matcher(fileName).matches();

            if (accept && !fileName.endsWith("/") && !fileName.endsWith("\\")) {
                retval.put(fileName, ResourceList.class.getClassLoader().getResourceAsStream(fileName));
            }
        }
        try {
            zf.close();
        }
        catch (IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }

    public static Map<String, InputStream> getResourcesFromDirectory(File directory, Pattern pattern)
    {
        Map<String, InputStream> retval = new HashMap<String, InputStream>();
        File[] fileList = directory.listFiles();

        for (File file : fileList) {
            if (file.isDirectory()) {
                Map<String, InputStream> tmpFileMap = getResourcesFromDirectory(file, pattern);
                for (Entry<String, InputStream> myFile : tmpFileMap.entrySet()) {
                    retval.put(myFile.getKey(), myFile.getValue());
                }
            }
            else {
                try {
                    String fileName = file.getCanonicalPath();
                    boolean accept = pattern.matcher(fileName).matches();

                    if (accept) {
                        URL url = new URL(String.format("file://%1$s", fileName));
                        retval.put(fileName, url.openStream());
                    }
                }
                catch (IOException e) {
                    throw new Error(e);
                }
            }
        }

        return retval;
    }
}
