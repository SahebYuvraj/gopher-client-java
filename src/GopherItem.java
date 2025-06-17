/**
 * Represents a single item in a Gopher directory listing.
 * Each item includes a type indicator, user-friendly label, selector path,
 * host, and port — as per RFC 1436.
 */
public class GopherItem {
    public char type;
    public String user_name;
    public String selector;
    public String host;
    public int port;

    /**
     * Constructs a new GopherItem.
     *
     * @param type      Type character (e.g., '0', '1', '9', 'i', etc.)
     * @param userName  Display label for the item
     * @param selector  Selector string used in Gopher requests
     * @param host      Hostname where the item resides
     * @param port      Port number of the host
     */
    public GopherItem(char type, String username, String selector, String host, int port) {
        this.type = type;
        this.user_name = username;
        this.selector = selector;
        this.host = host;
        this.port = port;
    }

    /**
     * Returns a formatted string representation of the Gopher item.
     */
    @Override
    public String toString() {
        return String.format("[%c] %s -> %s (%s : %d)", type, user_name, selector, host, port);
    }

    /**
     * Parses a raw Gopher directory line into a GopherItem object.
     *
     * @param line A raw line starting with the item type character followed by tab-separated fields
     * @return A GopherItem object, or null if the line is informational ('i') or malformed
     */
    public static GopherItem parseLineToItem(String line) {
        try {
            char type = line.charAt(0);
            if (type == 'i') {
            System.out.println(" I : " + line.substring(1)); //information lines
            return null;} 

            String[] parts = line.substring(1).split("\t");
            if (parts.length < 4) return null; //error handling

            String label = parts[0];
            String selector = parts[1];
            String host = parts[2];
            int port = Integer.parseInt(parts[3]);

            return new GopherItem(type, label, selector, host, port);
        } catch (Exception e) {
            return null;
        }
    }
}
