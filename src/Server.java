import java.io.BufferedReader;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import org.apache.commons.io.IOUtils;


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
		//contextLogin.getFilters().add(new ParameterFilter());
		//����SQL����
		HttpContext contextSQL = httpServer.createContext("/execute", new ExecuteHandler());
		//contextSQL.getFilters().add(new ParameterFilter());
		
		httpServer.setExecutor(null);
		httpServer.start();
		System.out.println("Server started!");
	}
	
	//Login������
	static class LoginHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange Exchange) throws IOException {		
			//��Ӧ��Ϣ
			responseMsg responseMsg = new responseMsg();
			responseMsg.msg = "{\"msg\":\"login failed!\"}";
			Headers responseHeaders = Exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "application/json");
			
//			//���request����
//			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
//			String username = params.get("username");
//			String password = params.get("password");
//			System.out.println("username:" + username);
//			System.out.println("password:" + password);
			
			//���post����
			//parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(Exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);    
			String username = parameters.get("username").toString();
			String password = parameters.get("password").toString();
			System.out.println("username:" + username);
			System.out.println("password:" + password);
            
			
			//��֤�û�
			if(!db.login(username, password)) {
				//��Ӧ��ʽ
				responseMsg.msg = "{\"msg\":\"login failed!\"}";
				Exchange.sendResponseHeaders(500, responseMsg.msg.getBytes().length);
			}
			else {
				//��Ӧ��ʽ
				//responseMsg.msg = db.getInfo();
				responseMsg.msg = "{\"msg\":\"login failed!\"}";
				Exchange.sendResponseHeaders(200, responseMsg.msg.getBytes().length);
			}
			
			//Exchange.sendResponseHeaders(200, responseMsg.msg.getBytes().length);
			//��������
			OutputStream out = Exchange.getResponseBody();
			out.write(responseMsg.msg.getBytes());
			out.flush();
			Exchange.close();
		}
	}
	
	//SQL������
	static class ExecuteHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange Exchange) throws IOException {
			//��Ӧ��Ϣ
			responseMsg responseMsg = new responseMsg();
			
			//���request����
//			Map<String, String> params = (Map<String, String>)Exchange.getAttribute("parameters");
//			String username = params.get("username");
//			String password = params.get("password");
//			String sql = params.get("sql");
//			System.out.println("username:" + username);
//			System.out.println("password:" + password);
//			System.out.println("sql:" + sql);
			
			//���post����
			// parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(Exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            System.out.println(query);
            parseQuery(query, parameters);    
            System.out.println(parameters);
			String username = (String) parameters.get("username");
			String password = (String) parameters.get("password");
			String sql = (String) parameters.get("sql");
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
	
	public static void parseQuery(String query, Map<String, 
			Object> parameters) throws UnsupportedEncodingException {

		         if (query != null) {
		                 String pairs[] = query.split("[&]");
		                 for (String pair : pairs) {
		                          String param[] = pair.split("[=]");
		                          String key = null;
		                          String value = null;
		                          if (param.length > 0) {
		                          key = URLDecoder.decode(param[0], 
		                          	System.getProperty("file.encoding"));
		                          }

		                          if (param.length > 1) {
		                                   value = URLDecoder.decode(param[1], 
		                                   System.getProperty("file.encoding"));
		                          }

		                          if (parameters.containsKey(key)) {
		                                   Object obj = parameters.get(key);
		                                   if (obj instanceof List<?>) {
		                                            List<String> values = (List<String>) obj;
		                                            values.add(value);

		                                   } else if (obj instanceof String) {
		                                            List<String> values = new ArrayList<String>();
		                                            values.add((String) obj);
		                                            values.add(value);
		                                            parameters.put(key, values);
		                                   }
		                          } else {
		                                   parameters.put(key, value);
		                          }
		                 }
		         }
		}
	
}
