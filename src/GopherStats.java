import java.util.*;

/**
 * Collects and reports statistics related to the Gopher client crawl.
 * Tracks file counts, sizes, paths, and edge-case references.
 */
public class GopherStats {
    public int gopherDirectoryCount = 0;
    public int textFileCount = 0;
    public int binaryFileCount = 0;

    public int minTextSize = Integer.MAX_VALUE;
    public int maxTextSize = Integer.MIN_VALUE;
    public String minTextLabel = "";
    public String maxTextLabel = "";
    public String smallestTextContent = "";

    public int minBinarySize = Integer.MAX_VALUE;
    public int maxBinarySize = Integer.MIN_VALUE;
    public String minBinaryLabel = "";
    public String maxBinaryLabel = "";

    public List<String> textFilePaths = new ArrayList<>();
    public List<String> binaryFilePaths = new ArrayList<>();
    public Set<String> invalidRefs = new HashSet<>();
}
