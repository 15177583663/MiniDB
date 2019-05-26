package minidb.server;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;
import com.sun.net.ssl.HttpsURLConnection;

import minidb.server.ParameterFilter;;

public class Server {
	//�����������������ͻ�������
	public static void main(String[] args) throws IOException {
		//HttpServerProvider provider = HttpServerProvider.provider();
		//�����˿�8080��ͬʱ����100������
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 100);
		//����Login����
		HttpContext contextLogin = server.createContext("/login", new LoginHandler());
		contextLogin.getFilters().add(new ParameterFilter());
		//����SQL����
		HttpContext contextSQL = server.createContext("/execute", new ExecuteHandler());
		contextSQL.getFilters().add(new ParameterFilter());
		
		//server.setExecutor(null);
		server.start();
		System.out.println("Server started!");
	}
	
	//Login������
	static class LoginHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange Exchange) throws IOException {
			
			
			//��Ӧ��Ϣ
			String responseMsg = "Login OK!";
			//���������
			InputStream body = Exchange.getRequestBody(); 
			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];
			final StringBuilder builder = new StringBuilder();
			Reader in = new InputStreamReader(body, "UTF-8");
			for (; ; ) {
			    int rsz = in.read(buffer, 0, buffer.length);
			    if (rsz < 0)
			        break;
			    builder.append(buffer, 0, rsz);
			}
			//���request����
			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
			String username = params.get("username");
			String password = params.get("password");
			System.out.println("username:" + username);
			System.out.println("password:" + password);
			
			// ��ȡ����ͷ
            String userAgent = Exchange.getRequestHeaders().getFirst("User-Agent");
            System.out.println("User-Agent: " + userAgent);
			
			String method = Exchange.getRequestMethod();
			System.out.println("addr: " + Exchange.getRemoteAddress() +     // �ͻ���IP��ַ
                    "; protocol: " + Exchange.getProtocol() +               // ����Э��: HTTP/1.1
                    "; method: " + method +            // ���󷽷�: GET, POST ��
                    "; body: " + builder.toString() +
                    "; URI: " + Exchange.getRequestURI());
			
			//��Ӧ��ʽ
			Headers responseHeaders = Exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "application/json");
			Exchange.sendResponseHeaders(HttpsURLConnection.HTTP_OK, responseMsg.getBytes().length);
			
			
			//��������
			OutputStream out = Exchange.getResponseBody();
			out.write(responseMsg.getBytes());
			out.flush();
			Exchange.close();
		}
	}
	
	//SQL������
	static class ExecuteHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange Exchange) throws IOException {
			//��Ӧ��Ϣ
			String responseMsg = "<font color='#ff0000'>SQL Handler!";
			//���������
			InputStream in = Exchange.getRequestBody(); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String temp = null;
			while((temp = reader.readLine()) != null) {
				System.out.println("Client request:" + temp);
			}
			
			// ��ȡ����ͷ
            String userAgent = Exchange.getRequestHeaders().getFirst("User-Agent");
            System.out.println("User-Agent: " + userAgent);
			
			//���request����
			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
			String username = params.get("username");
			String password = params.get("password");
			String sql = params.get("sql");
			System.out.println("username:" + username);
			System.out.println("password:" + password);
			
			//��Ӧ��ʽ
			Headers responseHeaders = Exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "application/json");
			Exchange.sendResponseHeaders(HttpsURLConnection.HTTP_OK, responseMsg.getBytes().length);
			
			
			//��������
			OutputStream out = Exchange.getResponseBody();
			out.write(responseMsg.getBytes());
			out.flush();
			Exchange.close();
		}
	}
	
}
