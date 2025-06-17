import java.io.*;
import java.net.*;
import java.util.*;

public class GopherClient {
    static Set<String> visited = new HashSet<>();
   
    static Map<String, Boolean> externalServers = new HashMap<>();
    static GopherStats stats = new GopherStats();

    /**
     * Program entry point.
     * @param args CLI arguments (unused)
     */
    public static void main(String[] args) {
        
        String host = "comp3310.ddns.net";
        int port = 70;
        String rootSelector = ""; 

        System.out.printf("Connecting to %s:%d...\n", host, port);
        if (checkExternalServer(host, port)) { //check if the main server is running
            System.out.println("Successfully connected to Gopher server.");
            crawl(host, port, rootSelector,"");
        } else {
            System.out.println("Failed connectoin - unable to connect to the Gopher server.");
            return;
        }

         printSummary();
        
    }

     /**
     * Recursively crawls a Gopher directory, logging each selector request and handling
     * files and subdirectories.
     *
     * @param host     server hostname
     * @param port     server port
     * @param selector Gopher selector string
     * @param path     accumulated logical path
     */
    public static void crawl(String host, int port, String selector,String path) {
        String key = host + ":" + port + selector;
        if (visited.contains(key)) return;   // Skip already visited paths
        if (host.equals("comp3310.ddns.net") && port == 70) {stats.gopherDirectoryCount++;}
        visited.add(key);

        try (Socket sock = new Socket(host, port)) {
            System.out.printf("[%s] Crawling: %s\n", new Date(), selector); //logging request

            OutputStream out = sock.getOutputStream();
            out.write((selector + "\r\n").getBytes("UTF-8"));
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
            String line;

            while ((line = in.readLine()) != null) {
                if (line.equals(".")) break;
                GopherItem item = GopherItem.parseLineToItem(line);
                if (item == null) continue;

                if (item.type == '3') {
                    System.out.println("[3] Invalid Reference: " + item.selector);
                    String refKey = "[3] " + item.user_name + " (" + item.host + ":" + item.port + ")";
                    stats.invalidRefs.add(refKey);
                    continue;
                }

                System.out.println(item);
                if (item.type == '1') { // directory
                    if (!item.host.equalsIgnoreCase("comp3310.ddns.net") || item.port != 70) {
                        String serverKey = item.host + ":" + item.port;
                        if (!externalServers.containsKey(serverKey)) {
                            boolean isUp = checkExternalServer(item.host, item.port);
                            externalServers.put(serverKey, isUp);
                        }
                    } else {
                        String lastSegment = item.selector.contains("/") ?
                        item.selector.substring(item.selector.lastIndexOf('/') + 1) :
                        item.selector;
                        String newPath = normalizePath(path, lastSegment);
                        crawl(item.host, item.port, item.selector,newPath);
                    }
                }
                else if (item.type == '0') {
                    String lastSegment = item.selector.contains("/") ?
                    item.selector.substring(item.selector.lastIndexOf('/') + 1) :
                    item.selector;
                    String fullFilePath = normalizePath(path, lastSegment);
                    GopherFileCatcher.fetchTextFile(item, stats, fullFilePath);
                    }
                else if (item.type == '9') {
                    String fullFilePath = path + "/" + item.user_name;
                    GopherFileCatcher.fetchBinaryFile(item, stats, fullFilePath);}
            }
        } catch (Exception e) {
            System.out.println("Error crawling " + selector + ": " + e.getMessage());
        }
    }

    /**
     * Checks connectivity to a server with a timeout.
     */
    public static boolean checkExternalServer(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000); // 3 sec timeout
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Prints a summary of the crawl: directories, files, sizes, and external servers.
     */
    public static void printSummary() {
        System.out.println("\n===== Crawl Summary =====");
        System.out.printf("Number of Gopher directories: %d\n", stats.gopherDirectoryCount);
        System.out.printf("\nNumber of Text files: %d\n", stats.textFileCount);
        for (String path : stats.textFilePaths) System.out.println(" - " + path);
        System.out.printf("\nNumber of Binary files: %d\n", stats.binaryFileCount);
        for (String path : stats.binaryFilePaths) System.out.println(" - " + path);
        System.out.println("\n===== Smallest Text File Content =====");
        System.out.printf("Smallest text: %s (%d bytes)\n", stats.minTextLabel, stats.minTextSize);
        System.out.println(stats.smallestTextContent);
        System.out.printf("Largest text: %s (%d bytes)\n", stats.maxTextLabel, stats.maxTextSize);
        if (stats.binaryFileCount == 0) {
            System.out.println("Largest binary: N/A");
            System.out.println("Smallest binary: N/A");
        } else {
            System.out.printf("Largest binary: %d bytes\n", stats.maxBinarySize);
            System.out.printf("Smallest binary: %d bytes\n", stats.minBinarySize);
        }
        System.out.println("\n===== External Servers =====");
        for (Map.Entry<String, Boolean> entry : externalServers.entrySet())
            System.out.printf(" - %s : %s\n", entry.getKey(), entry.getValue() ? "UP" : "DOWN");
        System.out.printf("\nInvalid references: %d\n", stats.invalidRefs.size());
        for (String ref : stats.invalidRefs) System.out.println(" - " + ref);
    }

    /**
     * Normalizes and combines path segments into a single, clean path.
     */
    public static String normalizePath(String base, String selector) {
    String combined = base + "/" + selector;
    return combined.replaceAll("/{2,}", "/").replaceAll("/$", "");
}

}
