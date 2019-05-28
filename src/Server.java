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


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import minidb.basic.database.MiniDB;
import minidb.result.Result;
import minidb.server.ParameterFilter;


public class Server {
	
	static MiniDB db;
	
	static class responseMsg {
		String msg = null;
		
		public responseMsg() {
			// TODO �Զ����ɵĹ��캯�����
			this.msg = null;
		}
	}
	
	public static boolean sqlExecute(String sql, responseMsg responseMsg) throws ClassNotFoundException, IOException {
		if(sql==null || sql.length()==0) {
			responseMsg = null;
			return true;
		}
		CharStream input = CharStreams.fromString(sql);
		MiniSQLLexer lexer = new MiniSQLLexer(input);
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		MiniSQLParser parser = new MiniSQLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);

		try {
			ParseTree tree = parser.sql();
			
			MyListener extractor = new MyListener();
			ParseTreeWalker walker=new ParseTreeWalker();
			walker.walk(extractor, tree);		
			
			Result res = db.execute(extractor.st);
			responseMsg.msg = res.json();
			return true;
		}
		catch(Exception e) {
			responseMsg.msg = "{\"msg\":\"syntax error!\"}";
			return false;
		}

	}
	
	//�����������������ͻ�������
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//��ʼ�� MiniDB
		db = new MiniDB();
		
		
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
			
//			// ��ȡ����ͷ
//            String userAgent = Exchange.getRequestHeaders().getFirst("User-Agent");
//            System.out.println("User-Agent: " + userAgent);
			
			//��Ӧ��Ϣ
			String responseMsg;
			
			//���������
//			InputStream body = Exchange.getRequestBody(); 
//			final int bufferSize = 1024;
//			final char[] buffer = new char[bufferSize];
//			final StringBuilder builder = new StringBuilder();
//			Reader in = new InputStreamReader(body, "UTF-8");
//			for (; ; ) {
//			    int rsz = in.read(buffer, 0, buffer.length);
//			    if (rsz < 0)
//			        break;
//			    builder.append(buffer, 0, rsz);
//			}
			
			
			//���request����
			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
			String username = params.get("username");
			String password = params.get("password");
			System.out.println("username:" + username);
			System.out.println("password:" + password);
			
			//��֤�û�
			if(!db.login(username, password)) {
				//��Ӧ��ʽ
				responseMsg = "{\"msg\":\"login failed!\"}";
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
				responseMsg = db.getInfo();
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
			responseMsg responseMsg = new responseMsg();
//			//���������
//			InputStream in = Exchange.getRequestBody(); 
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String temp = null;
//			while((temp = reader.readLine()) != null) {
//				System.out.println("Client request:" + temp);
//			}
			
//			// ��ȡ����ͷ
//            String userAgent = Exchange.getRequestHeaders().getFirst("User-Agent");
//            System.out.println("User-Agent: " + userAgent);
			
			//���request����
			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
			String username = params.get("username");
			String password = params.get("password");
			String sql = params.get("sql");
			System.out.println("username:" + username);
			System.out.println("password:" + password);
			System.out.println("sql:" + sql);
			
			try {
				if(sqlExecute(sql, responseMsg)) {
					System.out.println("responseMsg:" + responseMsg.msg);
					//��Ӧ��ʽ
					Headers responseHeaders = Exchange.getResponseHeaders();
					responseHeaders.set("Content-Type", "application/json");
					Exchange.sendResponseHeaders(200, responseMsg.msg.getBytes().length);
				}
				else {
					System.out.println("responseMsg:" + responseMsg.msg);
					//��Ӧ��ʽ
					Headers responseHeaders = Exchange.getResponseHeaders();
					responseHeaders.set("Content-Type", "application/json");
					Exchange.sendResponseHeaders(500, responseMsg.msg.getBytes().length);
				}
			} catch (ClassNotFoundException e) {
				System.out.println(e);
			}
			
			//��������
			OutputStream out = Exchange.getResponseBody();
			out.write(responseMsg.msg.getBytes());
			out.flush();
			Exchange.close();
		}
	}
	
}
