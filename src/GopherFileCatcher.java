import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class GopherFileCatcher {

     /**
     * Fetches a text file from a Gopher server, applies tarpit/firehose logic,
     * and updates the statistics object.
     *
     * @param item        The GopherItem describing the file
     * @param stats       The statistics tracker
     * @param logicalPath Logical full path of the file in the Gopher crawl
     */
    public static void fetchTextFile(GopherItem item,GopherStats stats, String logicalPath) {
    try (Socket sock = new Socket(item.host, item.port)) {
        sock.setSoTimeout(5000); 

        OutputStream out = sock.getOutputStream();
        out.write((item.selector + "\r\n").getBytes("UTF-8"));
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
        StringBuilder content = new StringBuilder();
        String line;

        int maxLines = 5000;
        int lineCount = 0;
       

        long startTime = System.currentTimeMillis();

        while ((line = in.readLine()) != null) {
            if (line.equals(".")) break;
            content.append(line).append("\n");
            lineCount++;

            if (lineCount > maxLines) {
                System.out.printf(" Firehose detected: %s \n", item.selector);
                stats.invalidRefs.add("[Firehose]" + item.selector);
                return;
            }
            

            long duration = System.currentTimeMillis() - startTime;
            if (duration > 15000 && lineCount < 10) {
                System.out.printf("Tarpit suspected in: %s \n", item.selector);
                stats.invalidRefs.add("[Tarpit] " + item.selector);
                return;
            }
            
        }

        int size = content.toString().getBytes(StandardCharsets.UTF_8).length;
        if (size < stats.minTextSize) {
            stats.minTextSize = size;
            stats.minTextLabel = item.user_name;
            stats.smallestTextContent = content.toString();
        }

        if (size > stats.maxTextSize) {
            stats.maxTextSize = size;
            stats.maxTextLabel = item.user_name;
        }

    
        stats.textFileCount++;

        //String fullPath = item.host + ":" + item.port + item.selector; //initial logical path
        stats.textFilePaths.add(logicalPath);

        System.out.printf("Text file (%d chars): %s\n", size, logicalPath);    

    } catch (SocketTimeoutException ste) {
        stats.invalidRefs.add("[Godot] " + item.selector);
        System.out.printf(" Timeout: No data received from %s \n", item.selector);

    } catch (Exception e) {
        System.out.println("Failed to fetch text file: " + item.selector);
    }
}

/**
     * Fetches a binary file from a Gopher server and updates the statistics object.
     *
     * @param item        The GopherItem describing the binary file
     * @param stats       The statistics tracker
     * @param logicalPath Logical full path of the file
     */
public static void fetchBinaryFile(GopherItem item,GopherStats stats, String logicalPath) {
        try (Socket sock = new Socket(item.host, item.port)) {
            OutputStream out = sock.getOutputStream();
            out.write((item.selector + "\r\n").getBytes("UTF-8"));
            out.flush();

            InputStream in = sock.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[4096];
            int bytesRead;
            int maxBytes = 500000; // 500 KB
            int totalBytes = 0;
            
            
            while ((bytesRead = in.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
                totalBytes += bytesRead;
                if (totalBytes > maxBytes) {
                    System.out.printf("firehose detected: %s \n", item.selector);
                    
                    break;
                }
            }

            int size = buffer.size();
            if (size < stats.minBinarySize) {
                stats.minBinarySize = size;
                stats.minBinaryLabel = item.user_name;
            }

            if (size > stats.maxBinarySize) {
                stats.maxBinarySize = size;
                stats.maxBinaryLabel = item.user_name;
            }

            stats.binaryFileCount++;

            String fullPath = item.host + ":" + item.port + item.selector;
            stats.binaryFilePaths.add(fullPath);

            System.out.printf("Binary file (%d bytes): %s\n", size, fullPath);
        

        } catch (Exception e) {
            System.out.println("failed to fetch binary file: " + item.selector);
        }
    }

}
