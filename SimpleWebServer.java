import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleWebServer {

    public static void main(String[] args) throws IOException {
        int port = 8111;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        // 모든 요청을 FileHandler가 처리하도록 설정합니다.
        server.createContext("/", new FileHandler());
        server.start();
        System.out.println("Server started on port " + port + ". Open http://localhost:"+port);
    }

    // 파일을 읽어서 응답하는 핸들러 클래스
    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 요청된 경로(URI)를 가져옵니다. 예: "/", "/styles.css"
            String requestPath = exchange.getRequestURI().getPath();

            // 만약 경로가 "/" 이면 "index.html"을 요청한 것으로 간주합니다.
            if ("/".equals(requestPath)) {
                requestPath = "/index.html";
            }

            // public 폴더 기준으로 파일 경로를 생성합니다.
            String filePath = "public" + requestPath;
            Path path = Paths.get(filePath);

            // 파일이 존재하고 일반 파일인지 확인합니다.
            if (Files.exists(path) && !Files.isDirectory(path)) {
                // 파일 확장자에 따라 Content-Type을 설정합니다.
                String contentType = "text/plain"; // 기본값
                if (filePath.endsWith(".html")) {
                    contentType = "text/html";
                } else if (filePath.endsWith(".css")) {
                    contentType = "text/css";
                }
                exchange.getResponseHeaders().set("Content-Type", contentType);

                // 파일 내용을 읽어 응답합니다.
                byte[] fileBytes = Files.readAllBytes(path);
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                // 파일이 없으면 404 Not Found 에러를 보냅니다.
                String response = "<h1>404 Not Found</h1><p>File not found.</p>";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
