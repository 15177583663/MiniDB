package minidb.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import minidb.basic.database.MiniDB;
import minidb.result.Result;
import minidb.server.ParameterFilter;


public class Server {
	
	static MiniDB db = new MiniDB();
	
//	public Server() {
//		Path path = Paths.get("input.sql");
//		byte[] bArray = Files.readAllBytes(path);
//		InputStream targetStream = new ByteArrayInputStream(bArray);
//		//InputStream targetStream = new ByteArrayInputStream(cmds.getBytes());
//		InputStreamReader in=new InputStreamReader(targetStream);
//		BufferedReader br=new BufferedReader(in);
//		while(true) {
//			String cmd=br.readLine();
//			if(cmd==null || cmd.length()==0)continue;
//			CharStream input = CharStreams.fromString(cmd);
//			MiniSQLLexer lexer = new MiniSQLLexer(input);
//			lexer.removeErrorListeners();
//			lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
//	
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//	
//			MiniSQLParser parser = new MiniSQLParser(tokens);
//			parser.removeErrorListeners();
//			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
//			
//			ParseTree tree = parser.sql();
//			
//			MyListener extractor = new MyListener();
//			ParseTreeWalker walker=new ParseTreeWalker();
//			walker.walk(extractor, tree);		
//			
//			Result res=db.execute(extractor.st);
//			res.display();
//		}
//	}
	
	//�����������������ͻ�������
	public static void main(String[] args) throws IOException {
		//��ʼ�� MiniDB
		//Server server = new Server();
		
		
		//HttpServerProvider provider = HttpServerProvider.provider();
		//�����˿�8080��ͬʱ����100������
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 100);
		//����Login����
		HttpContext contextLogin = httpServer.createContext("/login", new LoginHandler());
		contextLogin.getFilters().add(new ParameterFilter());
		//����SQL����
		HttpContext contextSQL = httpServer.createContext("/execute", new ExecuteHandler());
		contextSQL.getFilters().add(new ParameterFilter());
		
		//server.setExecutor(null);
		httpServer.start();
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
			
			//��֤�û�
			if(!db.login(username, password)) {
				//��Ӧ��ʽ
				responseMsg = "Login Failed!";
				Headers responseHeaders = Exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "application/json");
				Exchange.sendResponseHeaders(401, responseMsg.getBytes().length);
				
				
				//��������
				OutputStream out = Exchange.getResponseBody();
				out.write(responseMsg.getBytes());
				out.flush();
				Exchange.close();
			}
			else {
				//��Ӧ��ʽ
				Headers responseHeaders = Exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "application/json");
				Exchange.sendResponseHeaders(200, responseMsg.getBytes().length);
				
				
				//��������
				OutputStream out = Exchange.getResponseBody();
				out.write(responseMsg.getBytes());
				out.flush();
				Exchange.close();
			}
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
			Exchange.sendResponseHeaders(200, responseMsg.getBytes().length);
			
			
			//��������
			OutputStream out = Exchange.getResponseBody();
			out.write(responseMsg.getBytes());
			out.flush();
			Exchange.close();
		}
	}
	
}
