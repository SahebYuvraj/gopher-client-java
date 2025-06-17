# Java Gopher Client 🕳️

> A networking project exploring real-time socket programming and legacy internet protocols.  
> Developed in 2025 as part of a university course on systems and networking.

## 🔍 Overview

This project implements a client for the [Gopher protocol (RFC 1436)](https://datatracker.ietf.org/doc/html/rfc1436) using **native Java sockets**. 
It recursively crawls a remote Gopher server, downloads files, classifies content, and handles edge cases such as timeouts, invalid links, and non-responsive servers.

## 💡 Features

- 📡 Connects using raw TCP sockets (no libraries)
- 🔁 Recursively indexes files and directories
- 🧠 Differentiates text, binary, and informational items
- 🧱 Handles errors: tarpits, timeouts, firehoses, loops
- 📊 Logs stats like smallest/largest files, counts, and server references

## 🗂️ Structure

| File                     | Purpose                                    |
|--------------------------|--------------------------------------------|
| `GopherClient.java`      | Main crawler logic and connection manager  |
| `GopherFileCatcher.java` | Download and timeout handling              |
| `GopherItem.java`        | Represents parsed Gopher menu entries      |
| `GopherStats.java`       | Tracks file counts, sizes, and logs output |

## 🧪 Example Log Output
[Wed Apr 24 17:35:02 AEST 2025] Sending selector: "/RFC 1436"
Found binary file: /misc/bootimage.img
Skipping tarpit (slow response): /misc/godot

## 🛠️ How to Run

```bash
javac src/*.java
java src/GopherClient
```

## 🎯 Key Learnings

- Built a real-world network client using low-level Java socket programming
- Understood and implemented a legacy internet protocol from its RFC
- Designed fault-tolerant systems for unreliable servers (timeouts, retries, tarpits)
- Practiced recursive crawling, file classification, and robust logging

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

## 🚀 Try It Yourself

1. Clone the repo
2. Update the server IP/port in `GopherClient.java` if needed
3. Run it > Note: The original server used in this project was a private university-hosted Gopher server and may no longer be accessible. You can configure your own Gopher server locally for testing if needed.

