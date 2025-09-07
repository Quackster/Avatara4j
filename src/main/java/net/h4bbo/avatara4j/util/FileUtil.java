package net.h4bbo.avatara4j.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {
    private static final FileUtil INSTANCE = new FileUtil(FileSource.CLASSPATH);

    public enum FileSource { FILESYSTEM, CLASSPATH }

    private final FileSource fileSource;
    private final ClassLoader classLoader;

    public FileUtil(FileSource fileSource) {
        this.fileSource = fileSource;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    // ----- PUBLIC API -----

    public Document solveXmlFile(String directory, String fileNameContains) {
        if (fileNameContains == null) fileNameContains = directory;
        // String filePath = solveFile(directory, fileNameContains);
        List<InputStream> fileStream = solveFile(directory, fileNameContains);
        //return filePath == null ? null : readXmlFile(filePath);
        return fileStream.isEmpty() ? null : readXmlFile(fileStream.stream().findFirst().get());
    }

    public Document solveXmlFile(String directory) {
        return solveXmlFile(directory, null);
    }

    public Document readXmlFile(InputStream path) {
        InputStream in = null;
        try {
            in = path;
            if (in == null) return null;

            String text = readAllText(in);

            // Remove newline before XML declaration
            text = text.replace("\n<?xml", "<?xml");
            // Remove <graphics> tags
            text = text.replace("<graphics>", "");
            text = text.replace("</graphics>", "            ");

            // Parse the cleaned XML text
            InputStream cleanIn = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(cleanIn);
            cleanIn.close();
            return doc;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (in != null) try { in.close(); } catch (IOException ignored) {}
        }
    }

    public InputStream getFile(String directory, String file) {
        List<InputStream> result = new ArrayList<>();

        if (fileSource == FileSource.FILESYSTEM) {
            Path dirPath = Paths.get(directory);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path entry : stream) {
                    String fileName = (entry.getFileName().toString());
                    if (fileName.equals((file))) {
                        result.add(Files.newInputStream(entry));
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            // CLASSPATH (inside jar or classpath dir)
            String dir = directory.endsWith("/") ? directory : directory + "/";
            try {
                Enumeration<URL> resources = classLoader.getResources(dir);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if ("jar".equals(url.getProtocol())) {
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (JarFile jar = new JarFile(jarPath)) {
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (entry.getName().startsWith(dir) && !entry.isDirectory()) {
                                    String entryName = new File(entry.getName()).getName();
                                    String fileName = (entryName);
                                    if (fileName.equals((file))) {
                                        InputStream is = classLoader.getResourceAsStream(entry.getName());
                                        if (is != null) {
                                            result.add(is);
                                        }
                                    }
                                }
                            }
                        }
                    } else if ("file".equals(url.getProtocol())) {
                        File f = new File(url.toURI());
                        File[] files = f.listFiles();
                        if (files != null) {
                            for (File child : files) {
                                String fileName = (child.getName());
                                if (fileName.equals((file))) {
                                    result.add(Files.newInputStream(child.toPath()));
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return result.stream().findFirst().get();
    }

    public List<InputStream> solveFile(String directory, String fileNameContains) {
        List<InputStream> result = new ArrayList<>();

        if (fileSource == FileSource.FILESYSTEM) {
            Path dirPath = Paths.get(directory);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path entry : stream) {
                    String fileName = getFileNameWithoutExtension(entry.getFileName().toString());
                    if (fileName.contains(fileNameContains)) {
                        result.add(Files.newInputStream(entry));
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            // CLASSPATH (inside jar or classpath dir)
            String dir = directory.endsWith("/") ? directory : directory + "/";
            try {
                Enumeration<URL> resources = classLoader.getResources(dir);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if ("jar".equals(url.getProtocol())) {
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (JarFile jar = new JarFile(jarPath)) {
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (entry.getName().startsWith(dir) && !entry.isDirectory()) {
                                    String entryName = new File(entry.getName()).getName();
                                    String fileName = getFileNameWithoutExtension(entryName);
                                    if (fileName.contains(fileNameContains)) {
                                        InputStream is = classLoader.getResourceAsStream(entry.getName());
                                        if (is != null) {
                                            result.add(is);
                                        }
                                    }
                                }
                            }
                        }
                    } else if ("file".equals(url.getProtocol())) {
                        File file = new File(url.toURI());
                        File[] files = file.listFiles();
                        if (files != null) {
                            for (File child : files) {
                                String fileName = getFileNameWithoutExtension(child.getName());
                                if (fileName.contains(fileNameContains)) {
                                    result.add(Files.newInputStream(child.toPath()));
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<InputStream> getFigureXmlFiles() {
        return solveFile("figuredata/xml", "");
    }


    /*
    public List<File> getFigureXmlFiles() {
        List<File> result = new ArrayList<File>();
        String xmlDir = "figuredata/xml";
        if (fileSource == FileSource.FILESYSTEM) {
            File dir = new File(xmlDir);
            if (!dir.exists()) dir.mkdirs();
            File[] files = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });
            if (files != null) {
                for (File f : files) {
                    result.add(f);
                }
            }
        } else {
            // CLASSPATH mode: find resources inside jar/classpath
            try {
                String xmlDirWithSlash = xmlDir.endsWith("/") ? xmlDir : xmlDir + "/";
                Enumeration<URL> resources = classLoader.getResources(xmlDirWithSlash);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if ("jar".equals(url.getProtocol())) {
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (JarFile jar = new JarFile(jarPath)) {
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (entry.getName().startsWith(xmlDirWithSlash)
                                        && entry.getName().endsWith(".xml")
                                        && !entry.isDirectory()) {
                                    // Not a real File, but lets you get path
                                    result.add(new File(entry.getName()));
                                }
                            }
                        }
                    } else if ("file".equals(url.getProtocol())) {
                        // For running from IDE/project dir
                        File folder = new File(url.toURI());
                        File[] files = folder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".xml");
                            }
                        });
                        if (files != null) {
                            for (File f : files) {
                                result.add(f);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

     */

    /*
    private InputStream openInputStream(String path) throws IOException {
        if (fileSource == FileSource.FILESYSTEM) {
            return Files.newInputStream(Paths.get(path));
        } else {
            // For CLASSPATH: resource path must use '/' and be relative
            String resourcePath = path.startsWith("/") ? path.substring(1) : path;
            InputStream in = classLoader.getResourceAsStream(resourcePath);
            if (in == null) {
                // Try again in case file name is not prefixed with directory (rare case)
                int lastSlash = resourcePath.lastIndexOf('/');
                if (lastSlash >= 0) {
                    in = classLoader.getResourceAsStream(resourcePath.substring(lastSlash + 1));
                }
            }
            return in;
        }
    }*/

    private String getFileNameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }

    private String readAllText(InputStream in) throws IOException {
        // Java 8 compatible way to read all text from InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ( (line = reader.readLine()) != null ) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static FileUtil getInstance() {
        return INSTANCE;
    }
}
