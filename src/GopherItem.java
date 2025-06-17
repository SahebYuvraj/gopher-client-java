public class GopherItem {
    public char type;
    public String user_name;
    public String selector;
    public String host;
    public int port;

    public GopherItem(char type, String username, String selector, String host, int port) {
        this.type = type;
        this.user_name = username;
        this.selector = selector;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("[%c] %s -> %s (%s : %d)", type, user_name, selector, host, port);
    }

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
