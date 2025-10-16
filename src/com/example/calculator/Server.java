package com.example.calculator;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 9090;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve the webpage
        server.createContext("/", exchange -> {
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            if (path.equals("/") || path.equals("/index.html")) {
                byte[] bytes = Files.readAllBytes(Paths.get("html/index.html"));
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });

        // Calculator API (example: /calc?op=add&a=5&b=3)
        server.createContext("/calc", exchange -> {
            try {
                Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                String op = params.getOrDefault("op", "");
                double a = Double.parseDouble(params.getOrDefault("a", "0"));
                double b = Double.parseDouble(params.getOrDefault("b", "0"));

                Calculator calc = new Calculator();
                double result = switch (op) {
                    case "add" -> calc.add(a, b);
                    case "sub" -> calc.subtract(a, b);
                    case "mul" -> calc.multiply(a, b);
                    case "div" -> calc.divide(a, b);
                    default -> throw new IllegalArgumentException("Invalid operation");
                };

                sendText(exchange, 200, String.valueOf(result));
            } catch (Exception e) {
                sendText(exchange, 400, "Error: " + e.getMessage());
            }
        });

        server.start();
        System.out.println("✅ Server started at: http://localhost:" + port);
    }

    static void sendText(HttpExchange ex, int code, String text) throws IOException {
        byte[] bytes = text.getBytes("UTF-8");
        ex.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length > 0)
                map.put(URLDecoder.decode(kv[0], java.nio.charset.StandardCharsets.UTF_8),
                        kv.length > 1 ? URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8) : "");
        }
        return map;
    }
}

