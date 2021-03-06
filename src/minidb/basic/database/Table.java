package minidb.basic.database;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import minidb.basic.database.Schema;
import minidb.basic.index.PrimaryIndex;
import minidb.basic.index.PrimaryKey;
import minidb.basic.index.PrimaryKeyValue;
import minidb.basic.index.SecondaryIndex;
import minidb.basic.index.SecondaryKey;
import minidb.basic.index.Value;
import minidb.result.QueryResult;
import minidb.result.Result;
import minidb.result.SearchResult;
import minidb.types.TypeConst;

public class Table implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String tableName;
	String dbName;
	public Schema schema;
	transient PrimaryIndex index;
	transient HashMap<String,SecondaryIndex> indexs;
	int keySize;
	int keyType;
	int valueSize=0;
	int gCount=0;
	boolean hasPrimary=false;
	
	public Table(String dbName,String tableName,Schema schema,boolean hasPrimary) throws IOException {
		this.tableName=tableName;
		this.dbName=dbName;
		this.schema=schema;
		this.indexs=new HashMap<String,SecondaryIndex>();

		for(Entry<String, SchemaDescriptor> entry:schema.descriptors.entrySet()) {
			SchemaDescriptor sd=entry.getValue();
			if(hasPrimary&&sd.isPrimary()) {
				this.hasPrimary=true;
				keyType=sd.getType();
				this.schema.primaryKey=entry.getKey();
				keySize=sd.getSize();
			}
			valueSize+=sd.getSize()+TypeConst.VALUE_SIZE_NULL;
		}
		if(!hasPrimary) {
			keyType=TypeConst.VALUE_TYPE_INT;
			keySize=TypeConst.VALUE_SIZE_INT;
			this.schema.primaryKey="_id";
			valueSize+=keySize+TypeConst.VALUE_SIZE_NULL;
			SchemaDescriptor sd=new SchemaDescriptor();
			sd.setPrimary();
			sd.setSize(keySize);
			sd.setType(keyType);
			this.schema.descriptors.put("_id", sd);
		}

		for(Entry<String, SchemaDescriptor> entry:schema.descriptors.entrySet()) {
			SchemaDescriptor sd=entry.getValue();
			if(!sd.isPrimary()) {
				this.createSecondaryIndex(entry);
			}
		}
		this.createPrimaryIndex();

		this.schema.keyType=keyType;

	}
	
	public void commit() throws IOException {
		this.index.commit();
		for(SecondaryIndex index:this.indexs.values()) {
			index.commit();
		}
	}
	
	public void createSecondaryIndex(Entry<String, SchemaDescriptor> entry) throws IOException {
		SchemaDescriptor sd=entry.getValue();
		SecondaryIndex si=null;
		si=new SecondaryIndex(1024, sd.getType(), sd.getSize(), keyType, keySize, keySize, 1024,dbName+"_"+ tableName+"_"+entry.getKey()+".index");
		if(indexs==null)
			this.indexs=new HashMap<String,SecondaryIndex>();
		indexs.put(entry.getKey(), si);	
	}
	
	public void insertIndexsB(Object key,HashMap<String, String> pairs) throws IOException {
		if(key==null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		List<String> values=new ArrayList<String>();
		for(String Skey:this.schema.descriptors.keySet()) {
			if(pairs.containsKey(Skey)) {
				values.add(pairs.get(Skey));
			}
			else {
				values.add(null);
			}
		}
		insertIndexs(key,values);

	}
	
	protected byte[] getKeyArray(Object key) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);
		switch(this.keyType){
		case TypeConst.VALUE_TYPE_INT:
			dos.writeInt((Integer)key);
			break;
		case TypeConst.VALUE_TYPE_LONG:
			dos.writeLong((Long)key);
			break;
		case TypeConst.VALUE_TYPE_FLOAT:
			dos.writeFloat((Float)key);
			break;
		case TypeConst.VALUE_TYPE_DOUBLE:
			dos.writeDouble((Double)key);
			break;
		case TypeConst.VALUE_TYPE_STRING:
			dos.writeChars((String)key);
			int len=((String)key).length();
			for(int i=0;i<keySize/TypeConst.VALUE_SIZE_CHAR-len;i++) {
				dos.writeChar(0);
			}
			break;
		}
		dos.flush();
		byte[] array=outputStream.toByteArray();
		return array;
	}
	
	
	public void insertIndexs(Object key,List<String> values) throws IOException {
		if(key==null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		byte[] array=this.getKeyArray(key);
		int c=0;
		for(Entry<String,SchemaDescriptor> entry:this.schema.descriptors.entrySet()) {
			if(entry.getValue().isPrimary()) {
				c++;
				continue;
			}
			String value=values.get(c);
			if(value!=null)
				this.insertSecondaryIndex(entry.getKey(), entry.getValue(), value, key,array);
			c++;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void insertSecondaryIndex(String name,SchemaDescriptor sd,String value ,Object okvalue,byte[] kvalue) throws IOException {
		@SuppressWarnings("rawtypes")
		SecondaryKey key=constructSecondaryKey(sd.getType(),sd.getSize(),value,okvalue);
		PrimaryKeyValue kv=new PrimaryKeyValue(kvalue,this.keyType);
		@SuppressWarnings("rawtypes")
		SecondaryIndex indext= indexs.get(name);
		indext.insert(key, kv);
	}
	
	protected LinkedHashMap<String,Object> filterNames(List<String> names,LinkedHashMap<String,Object> data){
		LinkedHashMap<String,Object> res=new LinkedHashMap<String,Object>();
		for(String name:names) {
			res.put(name, data.get(name));
		}
		return res;
		
	}
		
	protected PrimaryKey constructPrimaryKeyB(byte[] cdValue) throws IOException {
		PrimaryKey keyi=null;
		ByteArrayInputStream in = new ByteArrayInputStream(cdValue);
		DataInputStream inst=new DataInputStream(in);
		switch(this.keyType) {
			case TypeConst.VALUE_TYPE_INT:
				keyi= new PrimaryKey<Integer>(inst.readInt(), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT);
				break;
			case TypeConst.VALUE_TYPE_LONG:
				keyi= new PrimaryKey<Long>(inst.readLong(), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG);
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				keyi= new PrimaryKey<Float>(inst.readFloat(), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT);
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				keyi= new PrimaryKey<Double>(inst.readDouble(), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE);
				break;
			case TypeConst.VALUE_TYPE_STRING:
				String str="";
				int len=this.keySize/TypeConst.VALUE_SIZE_CHAR;
		    	for(int i=0;i<len;i++) {
		    		char c=inst.readChar();
		    		if(c!=0)
						str+=c;
		    	}
				keyi= new PrimaryKey<String>(str, TypeConst.VALUE_TYPE_STRING, this.keySize);
				break;
			}
		return keyi;
	}

	protected PrimaryKey constructPrimaryKey(String cdValue) {
		PrimaryKey keyi=null;
		switch(this.keyType) {
			case TypeConst.VALUE_TYPE_INT:
				keyi= new PrimaryKey<Integer>(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT);
				break;
			case TypeConst.VALUE_TYPE_LONG:
				keyi= new PrimaryKey<Long>(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG);
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				keyi= new PrimaryKey<Float>(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT);
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				keyi= new PrimaryKey<Double>(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE);
				break;
			case TypeConst.VALUE_TYPE_STRING:
				keyi= new PrimaryKey<String>(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, keySize);
				break;
			}
		return keyi;
	}
	
	public static String parseString(String str) {
		if(str.charAt(0)=='\''&&str.charAt(str.length()-1)=='\'') {
			return str.substring(1,str.length()-1);
		}
		else {
			throw new IllegalArgumentException("parse String error");
		}
	}

	protected PrimaryKey constructPrimaryKeyO(Object cdValue) {
		PrimaryKey keyi=null;
		switch(this.keyType) {
			case TypeConst.VALUE_TYPE_INT:
				keyi= new PrimaryKey<Integer>((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT);
				break;
			case TypeConst.VALUE_TYPE_LONG:
				keyi= new PrimaryKey<Long>((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG);
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				keyi= new PrimaryKey<Float>((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT);
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				keyi= new PrimaryKey<Double>((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE);
				break;
			case TypeConst.VALUE_TYPE_STRING:
				keyi= new PrimaryKey<String>((String)cdValue, TypeConst.VALUE_TYPE_STRING, keySize);
				break;
			}
		return keyi;

	}
		@SuppressWarnings({ "unchecked", "rawtypes" })
	protected SecondaryKey constructSecondaryKeyO(int type,int size,Object cdValue, Object keyValue) {
			SecondaryKey keyr = null;
			switch(type) {
			case TypeConst.VALUE_TYPE_INT:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey((Integer)(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_LONG:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey((Long)(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey((Float)(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey((Double)(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_STRING:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey((String)cdValue, TypeConst.VALUE_TYPE_STRING, size,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey((String)cdValue, TypeConst.VALUE_TYPE_STRING, size,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey((String)cdValue, TypeConst.VALUE_TYPE_STRING, size,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey((String)cdValue, TypeConst.VALUE_TYPE_STRING, size,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey((String)cdValue, TypeConst.VALUE_TYPE_STRING, size,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			}
			return keyr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected SecondaryKey constructSecondaryKey(int type,int size,String cdValue, Object keyValue) {
			SecondaryKey keyr = null;
			switch(type) {
			case TypeConst.VALUE_TYPE_INT:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey(Integer.parseInt(cdValue), TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_LONG:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey(Long.parseLong(cdValue), TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey(Float.parseFloat(cdValue), TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey(Double.parseDouble(cdValue), TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			case TypeConst.VALUE_TYPE_STRING:
				switch(keyType) {
				case TypeConst.VALUE_TYPE_INT:
					keyr= new SecondaryKey(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, size,(Integer)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_LONG:
					keyr= new SecondaryKey(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, size,(Long)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_FLOAT:
					keyr= new SecondaryKey(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, size,(Float)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_DOUBLE:
					keyr= new SecondaryKey(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, size,(Double)keyValue,keyType,keySize);
					break;
				case TypeConst.VALUE_TYPE_STRING:
					keyr= new SecondaryKey(parseString(cdValue), TypeConst.VALUE_TYPE_STRING, size,(String)keyValue,keyType,keySize);
					break;
				}
				break;
			}
			return keyr;
	}
	protected LinkedList<Row> searchRowsO(String cdName,Object cdValue, int op) throws IOException{
		LinkedList<Row> rows=null;
		if(this.schema.primaryKey.equalsIgnoreCase(cdName)) {
			@SuppressWarnings("rawtypes")
			PrimaryKey keyi=constructPrimaryKeyO(cdValue);
			rows=searchByOp(keyi,op);
		}else {
			SchemaDescriptor sd=this.schema.descriptors.get(cdName);
			@SuppressWarnings("rawtypes")
			SecondaryKey keyr=constructSecondaryKeyO(sd.getType(),sd.getSize(),cdValue,null);
			rows=searchByOpS(keyr,cdName,op);
		}
		return rows;
	}

	protected LinkedList<Row> searchRows(String cdName,String cdValue, int op) throws IOException{
		LinkedList<Row> rows=null;
		if(this.schema.primaryKey.equalsIgnoreCase(cdName)) {
			@SuppressWarnings("rawtypes")
			PrimaryKey keyi=constructPrimaryKey(cdValue);
			rows=searchByOp(keyi,op);
		}else {
			SchemaDescriptor sd=this.schema.descriptors.get(cdName);
			@SuppressWarnings("rawtypes")
			SecondaryKey keyr=constructSecondaryKey(sd.getType(),sd.getSize(),cdValue,null);
			rows=searchByOpS(keyr,cdName,op);
		}
		return rows;
	}

	public static Result queryJT(boolean isStar, HashMap<String, Table> tables, List<Pair<String, String>> cnames,
			List<Pair<String, Integer>> jnames, List<Pair<Pair<String, String>, Pair<String, String>>> onConditions,
			boolean existWhere, CLogicTree lt) throws ClassNotFoundException, IOException {
		Result res;
		if(existWhere) {
			if(lt.isLeaf) {
				res=(QueryResult) Table.queryJ(isStar,tables,cnames,jnames,onConditions,existWhere,lt.isImme,lt.cdNameP,lt.cdValue,lt.cdNamerP,lt.op);
				return res;
			}
			else {
				QueryResult resa=(QueryResult) queryJT(isStar,tables,cnames,jnames,onConditions,existWhere,lt.ltree);
				QueryResult resb=(QueryResult) queryJT(isStar,tables,cnames,jnames,onConditions,existWhere,lt.rtree);
				res=combineQResJ(resa,resb,lt.lop);
				return res;
			}
		}
		else {
			res= Table.queryJ(isStar,tables,cnames,jnames,onConditions,existWhere,true,null,"",null,0);
			return res;
		}

	}




	@SuppressWarnings("unchecked")
	public static Result queryJ(Boolean isStar,HashMap<String, Table> tables, List<Pair<String, String>> cnames, List<Pair<String, Integer>> jnames,
			List<Pair<Pair<String, String>, Pair<String, String>>> onConditions, boolean existWhere, boolean isImme,
			Pair<String, String> cdNameP, String cdValue, Pair<String, String> cdNamerP, int op) throws ClassNotFoundException, IOException {
		if(!tables.containsKey(jnames.get(0).l)){
			throw new IllegalArgumentException("table"+jnames.get(0).l+" not exist");
		}
		Table rootTb=tables.get(jnames.get(0).l);
		ArrayList<LinkedHashMap<String,Object>> res=rootTb.fromRawJ(rootTb.index.searchAll().rows);
		Table thatTb;

		for(int i=0;i<onConditions.size();i++) {
			Pair<String,Integer> jname=jnames.get(i+1);
			String name=jname.l;
			if(!tables.containsKey(name)){
				throw new IllegalArgumentException("table"+name+" not exist");
			}
			thatTb=tables.get(name);
			Pair<Pair<String, String>, Pair<String, String>> cond=onConditions.get(i);
			if(cond.l.l.equals(thatTb.tableName)){
				if(jname.r==StatementSelectB.join)
					res=thatTb.join(cond.r.l,res, cond.l.r,cond.r.r);
				if(jname.r==StatementSelectB.leftOuterJoin)
					res=thatTb.leftOuterJoin(cond.r.l,res, cond.l.r,cond.r.r);
				if(jname.r==StatementSelectB.rightOuterJoin)
					res=thatTb.rightOuterJoin(cond.r.l,res, cond.l.r,cond.r.r);
				if(jname.r==StatementSelectB.fullOuterJoin)
					res=thatTb.fullOuterJoin(cond.r.l,res, cond.l.r,cond.r.r);
			}
			else {
				if(jname.r==StatementSelectB.join)
					res=thatTb.join(cond.l.l,res, cond.r.r,cond.l.r);
				if(jname.r==StatementSelectB.leftOuterJoin)
					res=thatTb.leftOuterJoin(cond.l.l,res, cond.r.r,cond.l.r);
				if(jname.r==StatementSelectB.rightOuterJoin)
					res=thatTb.rightOuterJoin(cond.l.l,res, cond.r.r,cond.l.r);
				if(jname.r==StatementSelectB.fullOuterJoin)
					res=thatTb.fullOuterJoin(cond.l.l,res, cond.r.r,cond.l.r);
			}
		}

		ArrayList<LinkedHashMap<String,Object>> fres=new ArrayList<LinkedHashMap<String,Object>>();
		if(existWhere) {
			if(isImme) {
				RowFilter rf= buildFilterJV(tables.get(cdNameP.l),cdNameP.r,op,cdValue);
				for(LinkedHashMap<String,Object> objs:res) {
					if(rf.method(objs)) {
						LinkedHashMap<String,Object> nobjs=filterNamesJ(isStar,cnames,objs);
						fres.add(nobjs);
					}
				}
			}
			else {
				//tofix
				RowFilter rf= buildFilterJ(tables.get(cdNameP.l),tables.get(cdNamerP.l),cdNameP.r,op,cdNamerP.r);
				for(LinkedHashMap<String,Object> objs:res) {
					if(rf.method(objs)) {
						LinkedHashMap<String,Object> nobjs=filterNamesJ(isStar,cnames,objs);
						fres.add(nobjs);
					}
				}	
			}
			
		}
		else{
			for(LinkedHashMap<String,Object> objs:res) {
				LinkedHashMap<String,Object> nobjs=filterNamesJ(isStar,cnames,objs);
				fres.add(nobjs);
			}
		}

		QueryResult qr=new QueryResult();
		qr.data=fres;
		return qr;
	}

	protected static LinkedHashMap<String,Object> filterNamesJ(Boolean isStar,List<Pair<String, String>> cnames,LinkedHashMap<String,Object> data){
		if(isStar) return data;
		LinkedHashMap<String,Object> res=new LinkedHashMap<String,Object>();
		for(Pair<String,String> namep:cnames) {
			String name=namep.l+"."+namep.r;
			res.put(name, data.get(name));
		}
		return res;
	}
	//should consider about la is empty
	protected ArrayList<LinkedHashMap<String,Object>> rightOuterJoin(String thatTableName,ArrayList<LinkedHashMap<String,Object>> la,String thisCon,String thatCon) throws ClassNotFoundException, IOException{
		@SuppressWarnings("unchecked")
		Set<Object> rowRec=new HashSet();

		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();

		if(la.size()==0) return res;

		LinkedHashMap<String, Object> nullRec = new LinkedHashMap<String,Object> ();
		LinkedHashMap<String,Object> smpl=la.get(0);
		for(String name:smpl.keySet()) {
			nullRec.put(name, null);
		}

		for(LinkedHashMap<String,Object> rowa: la) {
			Object value=rowa.get(thatTableName+"."+thatCon);
			if(value==null) continue;
			ArrayList<LinkedHashMap<String,Object>> rows=fromRaw(this.searchRowsO(thisCon,value,Statement.eq));
			if(rows.size()>0) {
				rowRec.add(value);
			}
			for(LinkedHashMap<String,Object> rowb:rows) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rowb.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(rowa);
				rowres.putAll(rowbr);

				res.add(rowres);
			}
		}
		
		LinkedList<Row> rows=index.searchAll().rows;
		ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
		for(LinkedHashMap<String,Object> rec:rowl) {
			if(!rowRec.contains(rec.get(thisCon))) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rec.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(nullRec);
				rowres.putAll(rowbr);
				res.add(rowres);
			}
		}
		
		return res;
	}
	//should consider about la is empty
	protected ArrayList<LinkedHashMap<String,Object>> fullOuterJoin(String thatTableName,ArrayList<LinkedHashMap<String,Object>> la,String thisCon,String thatCon) throws ClassNotFoundException, IOException{
		@SuppressWarnings("unchecked")
		Set<Object> rowRec=new HashSet();

		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();

		if(la.size()==0) return res;

		LinkedHashMap<String, Object> nullRec = new LinkedHashMap<String,Object> ();
		LinkedHashMap<String, Object> nullRecb = new LinkedHashMap<String,Object> ();
		for(String name:this.schema.descriptors.keySet()) {
			nullRecb.put(this.tableName+"."+name, null);
		}
		LinkedHashMap<String,Object> smpl=la.get(0);
		for(String name:smpl.keySet()) {
			nullRec.put(name, null);
		}

		for(LinkedHashMap<String,Object> rowa: la) {
			Object value=rowa.get(thatTableName+"."+thatCon);
			if(value==null) continue;
			ArrayList<LinkedHashMap<String,Object>> rows=fromRaw(this.searchRowsO(thisCon,value,Statement.eq));
			if(rows.size()>0) {
				rowRec.add(value);
			}
			if(rows.size()==0) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				rowres.putAll(rowa);
				rowres.putAll(nullRecb);
				res.add(rowres);
				continue;
			}
			for(LinkedHashMap<String,Object> rowb:rows) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rowb.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(rowa);
				rowres.putAll(rowbr);

				res.add(rowres);
			}
		}
		
		LinkedList<Row> rows=index.searchAll().rows;
		ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
		for(LinkedHashMap<String,Object> rec:rowl) {
			if(!rowRec.contains(rec.get(thisCon))) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rec.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(nullRec);
				rowres.putAll(rowbr);
				res.add(rowres);
			}
		}
		
		return res;
	}


	protected ArrayList<LinkedHashMap<String,Object>> leftOuterJoin(String thatTableName,ArrayList<LinkedHashMap<String,Object>> la,String thisCon,String thatCon) throws ClassNotFoundException, IOException{
		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		if(la.size()==0) return res;
		LinkedHashMap<String, Object> nullRec = new LinkedHashMap<String,Object> ();
		for(String name:this.schema.descriptors.keySet()) {
			nullRec.put(this.tableName+"."+name, null);
		}

		for(LinkedHashMap<String,Object> rowa: la) {
			Object value=rowa.get(thatTableName+"."+thatCon);
			if(value==null) continue;
			ArrayList<LinkedHashMap<String,Object>> rows=fromRaw(this.searchRowsO(thisCon,value,Statement.eq));
			if(rows.size()==0) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				rowres.putAll(rowa);
				rowres.putAll(nullRec);
				res.add(rowres);
				continue;
			}
			for(LinkedHashMap<String,Object> rowb:rows) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rowb.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(rowa);
				rowres.putAll(rowbr);
				res.add(rowres);
			}
		}
		return res;
	}


	protected ArrayList<LinkedHashMap<String,Object>> join(String thatTableName,ArrayList<LinkedHashMap<String,Object>> la,String thisCon,String thatCon) throws ClassNotFoundException, IOException{
		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		if(la.size()==0) return res;
		for(LinkedHashMap<String,Object> rowa: la) {
			Object value=rowa.get(thatTableName+"."+thatCon);
			if(value==null) continue;
			ArrayList<LinkedHashMap<String,Object>> rows=fromRaw(this.searchRowsO(thisCon,value,Statement.eq));
			for(LinkedHashMap<String,Object> rowb:rows) {
				LinkedHashMap<String,Object> rowres=new LinkedHashMap<String,Object>();
				LinkedHashMap<String,Object> rowbr=new LinkedHashMap<String,Object> ();
				for(Entry<String,Object> entry:rowb.entrySet()) {
					rowbr.put(this.tableName+"."+entry.getKey(), entry.getValue());
				}
				rowres.putAll(rowa);
				rowres.putAll(rowbr);
				res.add(rowres);
			}
		}
		return res;
	}

	protected QueryResult queryT(List<String> names,Boolean existWhere,LogicTree lt) throws IOException, ClassNotFoundException {
		QueryResult res;
		if(existWhere) {
			if(lt.isLeaf) {
				if(lt.isImme)
					res=query(names, existWhere, lt.cdName, lt.cdValue, lt.op);
				else
					res=queryI(names, existWhere, lt.cdName, lt.cdNamer, lt.op);
				return res;
			}
			else {
				QueryResult resa=queryT(names,existWhere,lt.ltree);
				QueryResult resb=queryT(names,existWhere,lt.rtree);
				res=combineQRes(resa,resb,lt.lop);
				return res;
			}
		}
		else {
			res=query(names, existWhere, "", "", 0);
			return res;
		}
	}
	private static QueryResult combineQResJ(QueryResult resa, QueryResult resb, int lop) {
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		QueryResult qr=new QueryResult();
		if(lop==LogicTree.and) {
			Set<LinkedHashMap<String,Object>> map =new HashSet();
			for(LinkedHashMap rec:resa.data) {
				map.add(rec);
			}
			for(LinkedHashMap<String,Object> rec:resb.data) {
				if(map.contains(rec)) {
					res.add(rec);
				}
			}
			qr.data=res;
			qr.types=resa.types;
			return qr;
		}
		if(lop==LogicTree.or) {
			Set<LinkedHashMap<String,Object>> set =new HashSet();
			for(LinkedHashMap<String,Object> rec:resa.data) {
				set.add(rec);
			}
			for(LinkedHashMap<String,Object> rec:resb.data) {
				set.add(rec);
			}
			for(LinkedHashMap<String,Object> rec:set) {
				res.add(rec);
			}
			qr.data=res;
			qr.types=resa.types;
			return qr;
		}
		return qr;
	}
	private QueryResult combineQRes(QueryResult resa, QueryResult resb, int lop) {
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		QueryResult qr=new QueryResult();
		if(lop==LogicTree.and) {
			Set<Object> map =new HashSet();
			for(LinkedHashMap<String,Object> rec:resa.data) {
				map.add(rec.get(this.schema.primaryKey));
			}
			for(LinkedHashMap<String,Object> rec:resb.data) {
				if(map.contains(rec.get(this.schema.primaryKey))) {
					res.add(rec);
				}
			}
			qr.data=res;
			qr.types=resa.types;
			return qr;
		}
		if(lop==LogicTree.or) {
			HashMap<Object,LinkedHashMap<String,Object>> map =new HashMap<Object,LinkedHashMap<String,Object>>();
			for(LinkedHashMap<String,Object> rec:resa.data) {
				map.put(rec.get(this.schema.primaryKey), rec);
			}
			for(LinkedHashMap<String,Object> rec:resb.data) {
				map.put(rec.get(this.schema.primaryKey), rec);
			}
			for(LinkedHashMap<String,Object> rec:map.values()) {
				res.add(rec);
			}

			qr.data=res;
			qr.types=resa.types;
			return qr;
		}
		return qr;
	}

	@SuppressWarnings("unchecked")
	protected QueryResult query(List<String> names,Boolean existWhere,String cdName,String cdValue, int op) throws IOException, ClassNotFoundException {
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		LinkedList<Row> rows=null;
		if(!existWhere) {
			rows=index.searchAll().rows;
			QueryResult qr=new QueryResult();
			ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
			for(LinkedHashMap<String,Object> objs:rowl) {
				LinkedHashMap<String,Object> nobjs=filterNames(names,objs);
				res.add((nobjs));
			}
			qr.data=res;
			qr.types=this.schema.types;
			return qr;
		}
		else
			rows=searchRows(cdName,cdValue,op);
		QueryResult qr=new QueryResult();
		ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
		for(LinkedHashMap<String,Object> objs:rowl) {
			LinkedHashMap<String,Object> nobjs=filterNames(names,objs);
			res.add((nobjs));
		}
	
		qr.data=res;
		qr.types=this.schema.types;
		return qr;
	}
	private LinkedHashMap<String, Object> fixA(LinkedHashMap<String, Object> nobjs) {
		if(!this.hasPrimary) {
			nobjs.remove("_id");
		}
		return nobjs;
	}

		@SuppressWarnings("unchecked")
	protected LinkedList<Row> searchByOpS(@SuppressWarnings("rawtypes") SecondaryKey key,String keyName,int op) throws IOException{
		SecondaryIndex indext=indexs.get(keyName);
		LinkedList<PrimaryKeyValue> vs = null;
		LinkedList<Row> rows = new LinkedList<Row>();
		switch(op) {
		case Statement.lg:
			vs= indext.searchByRange(key, true, true, null, false, true,false).rows;
			break;
		case Statement.lt:
			vs= indext.searchByRange(null, false, true,key, true, true,false).rows;
			break;
		case Statement.lge:
			vs= indext.searchByRange(key, true, false, null, false, true,false).rows;
			break;
		case Statement.lte:
			vs= indext.searchByRange(null, false, true,key, true, false,false).rows;
			break;
		case Statement.eq:
			vs= indext.search(key,false).rows;
			break;
		case Statement.neq:
			vs=indext.searchNotEqual(key).rows;
			break;
		}
		for(PrimaryKeyValue pk:vs) {
			PrimaryKey pkey=this.constructPrimaryKeyB(pk.array);
			rows.addAll(index.search(pkey).rows);
		}
		return rows;

	}

	@SuppressWarnings("unchecked")
	protected LinkedList<Row> searchByOp(@SuppressWarnings("rawtypes") PrimaryKey key,int op) throws IOException{
		switch(op) {
		case Statement.lg:
			return index.searchByRange(key, true, true, null, false, true).rows;
		case Statement.lt:
			return index.searchByRange(null, false, true,key, true, true).rows;
		case Statement.lge:
			return index.searchByRange(key, true, false, null, false, true).rows;
		case Statement.lte:
			return index.searchByRange(null, false, true,key, true, false).rows;
		case Statement.eq:
			return index.search(key).rows;
		case Statement.neq:
			return index.searchNotEqual(key).rows;
		}
		return null;
	}
	protected QueryResult queryI(List<String> names,Boolean existWhere,String cdName,String cdNamer, int op) throws IOException, ClassNotFoundException {
		@SuppressWarnings("unchecked")

		LinkedList<Row> rows=index.searchAll().rows;
		ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();

		if(existWhere) {
			RowFilter rf= buildFilter(this,cdName,op,cdNamer);
			for(LinkedHashMap<String,Object> objs:rowl) {
				if(rf.method(objs)) {
					LinkedHashMap<String,Object> nobjs=filterNames(names,objs);
					res.add((nobjs));
				}
			}
		}
		else{
			for(LinkedHashMap<String,Object> objs:rowl) {
				LinkedHashMap<String,Object> nobjs=filterNames(names,objs);
				res.add((nobjs));
			}
		}
		QueryResult qr=new QueryResult();
		qr.data=res;
		qr.types=this.schema.types;
		return qr;
	}
	
	protected static RowFilter buildFilter(Table tb,String cdName,int op,String cdNamer) {
		SchemaDescriptor sd = tb.schema.descriptors.get(cdName);
		RowFilter rf=(row)->{
			Object va=row.get(cdName);
			Object vb=row.get(cdNamer);
			if(va==null||vb==null)
				return false;
			else
				return tb.compare(op,sd.getType(),row.get(cdName),row.get(cdNamer));
		};
		return rf;
	}
	protected static RowFilter buildFilterJ(Table tba,Table tbb,String cdName,int op,String cdNamer) {
		SchemaDescriptor sd = tba.schema.descriptors.get(cdName);
		RowFilter rf=(row)->{
			Object va=row.get(tba.tableName+"."+cdName);
			Object vb=row.get(tbb.tableName+"."+cdNamer);
			if(va==null||vb==null)
				return false;
			else
				return tba.compare(op,sd.getType(),va,vb);
		};
		return rf;
	}
	protected static RowFilter buildFilterJV(Table tb,String cdName,int op,String Value) {
		SchemaDescriptor sd = tb.schema.descriptors.get(cdName);
		RowFilter rf=(row)->{
			Object va=row.get(tb.tableName+"."+cdName);
			if(va==null)
				return false;
			else
				return tb.compareV(op,sd.getType(),row.get(tb.tableName+"."+cdName),Value);
		};
		return rf;
	}
	protected <T extends Comparable <T>> boolean compareT(int op,T va,T vb) {
		int res=va.compareTo(vb);
		switch(op) {
		case Statement.eq:
			return res==0;
		case Statement.lg:
			return res>0;
		case Statement.lt:
			return res<0;
		case Statement.lge:
			return res>=0;
		case Statement.lte:
			return res<=0;
		}
		return true;
	}

	protected boolean compareV(int op,int type,Object va,String vb) {
		switch(type) {
		case TypeConst.VALUE_TYPE_INT:
			return compareT(op,(Integer)va,Integer.parseInt(vb));
		case TypeConst.VALUE_TYPE_LONG:
			return compareT(op,(Long)va,Long.parseLong(vb));
		case TypeConst.VALUE_TYPE_FLOAT:
			return compareT(op,(Float)va,Float.parseFloat(vb));
		case TypeConst.VALUE_TYPE_DOUBLE:
			return compareT(op,(Double)va,Double.parseDouble(vb));
		case TypeConst.VALUE_TYPE_STRING:
			return compareT(op,(String)va,(String)parseString(vb));
		}
		return true;
		
	}


	protected boolean compare(int op,int type,Object va,Object vb) {
		switch(type) {
		case TypeConst.VALUE_TYPE_INT:
			return compareT(op,(Integer)va,(Integer)(vb));
		case TypeConst.VALUE_TYPE_LONG:
			return compareT(op,(Long)va,(Long)(vb));
		case TypeConst.VALUE_TYPE_FLOAT:
			return compareT(op,(Float)va,(Float)(vb));
		case TypeConst.VALUE_TYPE_DOUBLE:
			return compareT(op,(Double)va,(Double)(vb));
		case TypeConst.VALUE_TYPE_STRING:
			return compareT(op,(String)va,(String)vb);
		}
		return true;
		
	}

	protected ArrayList<LinkedHashMap<String,Object>> fromRawJ(LinkedList<Row> rows) throws ClassNotFoundException, IOException{
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		int c=0;
		for(Row v:rows) {
			res.add(extractJ(v));
			c++;
		}
		return res;
	}

	protected ArrayList<LinkedHashMap<String,Object>> fromRaw(LinkedList<Row> rows) throws ClassNotFoundException, IOException{
		ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
		int c=0;
		for(Row v:rows) {
			res.add(extract(v));
			c++;
		}
		return res;
	}
		
	protected LinkedHashMap<String,Object> extractJ(Row row) throws IOException, ClassNotFoundException{
		LinkedHashMap<String,Object> objs=new LinkedHashMap<String,Object>();
		int pos=0;
		for(Entry<String,SchemaDescriptor> e:this.schema.descriptors.entrySet()) {
			SchemaDescriptor sd=e.getValue();
			byte[] nulls = Arrays.copyOfRange(row.array, pos, pos+2);
			byte[] slice = Arrays.copyOfRange(row.array, pos+2, pos+2+sd.getSize());
			pos+=sd.getSize()+2;
		    ByteArrayInputStream in = new ByteArrayInputStream(slice);
		    DataInputStream inst=new DataInputStream(in);
		    ByteArrayInputStream inn = new ByteArrayInputStream(nulls);
		    DataInputStream instn=new DataInputStream(inn);
		    
		    char isnull=instn.readChar();
		    if(isnull=='n') {
		    	objs.put(this.tableName+"."+e.getKey(), null);
		    	continue;
		    }
		    switch(sd.getType()) {
		    case TypeConst.VALUE_TYPE_INT:
				objs.put(this.tableName+"."+e.getKey(),(Object)inst.readInt());
		    	break;
		    case TypeConst.VALUE_TYPE_LONG:
				objs.put(this.tableName+"."+e.getKey(),(Object)inst.readLong());
		    	break;
		    case TypeConst.VALUE_TYPE_DOUBLE:
				objs.put(this.tableName+"."+e.getKey(),(Object)inst.readDouble());
		    	break;
		    case TypeConst.VALUE_TYPE_FLOAT:
				objs.put(this.tableName+"."+e.getKey(),(Object)inst.readFloat());
		    	break;
		    case TypeConst.VALUE_TYPE_STRING:
				String str="";
				int len=sd.getSize()/TypeConst.VALUE_SIZE_CHAR;
		    	for(int i=0;i<len;i++) {
		    		char c=inst.readChar();
		    		if(c!=0)
						str+=c;
		    	}
				objs.put(this.tableName+"."+e.getKey(),(Object)str);
		    	break;
		    }
		}
		return objs;
	}

	protected LinkedHashMap<String,Object> extract(Row row) throws IOException, ClassNotFoundException{
		LinkedHashMap<String,Object> objs=new LinkedHashMap<String,Object>();
		int pos=0;
		for(Entry<String,SchemaDescriptor> e:this.schema.descriptors.entrySet()) {
			SchemaDescriptor sd=e.getValue();
			byte[] nulls = Arrays.copyOfRange(row.array, pos, pos+2);
			byte[] slice = Arrays.copyOfRange(row.array, pos+2, pos+2+sd.getSize());
			pos+=sd.getSize()+2;
		    ByteArrayInputStream in = new ByteArrayInputStream(slice);
		    DataInputStream inst=new DataInputStream(in);
		    ByteArrayInputStream inn = new ByteArrayInputStream(nulls);
		    DataInputStream instn=new DataInputStream(inn);
		    char isnull=instn.readChar();
		    if(isnull=='n') {
		    	objs.put(e.getKey(), null);
		    	continue;
		    }
		    switch(sd.getType()) {
		    case TypeConst.VALUE_TYPE_INT:
				objs.put(e.getKey(),(Object)inst.readInt());
		    	break;
		    case TypeConst.VALUE_TYPE_LONG:
				objs.put(e.getKey(),(Object)inst.readLong());
		    	break;
		    case TypeConst.VALUE_TYPE_DOUBLE:
				objs.put(e.getKey(),(Object)inst.readDouble());
		    	break;
		    case TypeConst.VALUE_TYPE_FLOAT:
				objs.put(e.getKey(),(Object)inst.readFloat());
		    	break;
		    case TypeConst.VALUE_TYPE_STRING:
				String str="";
				int len=sd.getSize()/TypeConst.VALUE_SIZE_CHAR;
		    	for(int i=0;i<len;i++) {
		    		char c=inst.readChar();
		    		if(c!=0)
						str+=c;
		    	}
				objs.put(e.getKey(),(Object)str);
		    	break;
		    }
		}
		return objs;
	}
	
	public void createPrimaryIndex() throws IOException {
		switch(keyType) {
		case TypeConst.VALUE_TYPE_INT:
			index=new PrimaryIndex<Integer>(1024, keyType, keySize, valueSize, 1024, dbName+"_"+tableName.concat(".index"));
			break;
		case TypeConst.VALUE_TYPE_LONG:
			index=new PrimaryIndex<Long>(1024, keyType, keySize, valueSize, 1024, dbName+"_"+tableName.concat(".index"));
			break;
		case TypeConst.VALUE_TYPE_DOUBLE:
			index=new PrimaryIndex<Double>(1024, keyType, keySize, valueSize, 1024, dbName+"_"+tableName.concat(".index"));
			break;
		case TypeConst.VALUE_TYPE_FLOAT:
			index=new PrimaryIndex<Float>(1024, keyType, keySize, valueSize, 1024, dbName+"_"+tableName.concat(".index"));
			break;
		case TypeConst.VALUE_TYPE_STRING:
			index=new PrimaryIndex<String>(1024, keyType, keySize, valueSize, 1024, dbName+"_"+tableName.concat(".index"));
			break;
		}
	}
	
	public static Table loadFromFile(String path) throws ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Table db = (Table)ois.readObject();
        ois.close();
        return db;
	}
	
	public void storeToFile (String path) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(this);
        oos.close();
        logToFile();
	}

	public void logToFile () throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(dbName+".log",true));
	    writer.append(dbName+"_"+this.tableName.concat(".schema"));
	    writer.append('\n');
	    writer.close();
	}
	
	@SuppressWarnings("unchecked")
	public void delete(LogicTree lt) throws IOException, ClassNotFoundException {

		ArrayList<LinkedHashMap<String,Object>> rowl=searchRowsT(lt);
		
		for(LinkedHashMap<String,Object> row:rowl) {
			Object key=row.get(this.schema.primaryKey);
			@SuppressWarnings("rawtypes")
			PrimaryKey keyi=constructPrimaryKeyO(key);
			this.index.delete(keyi);
			for(Entry<String,Object> obj:row.entrySet()) {
				SchemaDescriptor sdt=this.schema.descriptors.get(obj.getKey());
				if(sdt.isPrimary()) continue;
				SecondaryKey keyr=constructSecondaryKeyO(sdt.getType(),sdt.getSize(),obj.getValue(),key);
				this.indexs.get(obj.getKey()).delete(keyr);
			}

		}
		
	}
	
	private ArrayList<LinkedHashMap<String, Object>> searchRowsT(LogicTree lt) throws IOException, ClassNotFoundException {
		if(lt.isLeaf) {
			if(lt.isImme) {
				LinkedList<Row> rows=searchRows(lt.cdName,lt.cdValue,lt.op);
				return fromRaw(rows);
			}
			else{
				ArrayList<LinkedHashMap<String,Object>> res=new ArrayList<LinkedHashMap<String,Object>>();
				LinkedList<Row> rows=index.searchAll().rows;
				ArrayList<LinkedHashMap<String,Object>> rowl=fromRaw(rows);
				RowFilter rf= buildFilter(this,lt.cdName,lt.op,lt.cdNamer);
				for(LinkedHashMap<String,Object> objs:rowl) {
					if(rf.method(objs)) {
						res.add((objs));
					}
				}
				return res;
			}
		}
		else {
			ArrayList<LinkedHashMap<String, Object>> resa=searchRowsT(lt.ltree);
			ArrayList<LinkedHashMap<String, Object>> resb=searchRowsT(lt.rtree);
			QueryResult qa=new QueryResult();
			QueryResult qb=new QueryResult();
			qa.data=resa;
			qb.data=resb;
			ArrayList<LinkedHashMap<String, Object>> res=this.combineQRes(qa, qb, lt.lop).data;
			return res;
		}
	}

	@SuppressWarnings("unchecked")
	public void update(LogicTree lt,String setName,String setValue) throws IOException, ClassNotFoundException {

		ArrayList<LinkedHashMap<String,Object>> rowl=searchRowsT(lt);

		SchemaDescriptor sd=this.schema.descriptors.get(setName);
		for(LinkedHashMap<String,Object> row:rowl) {
			Object secondary=row.get(setName);
			Object key=row.get(this.schema.primaryKey);
			if(!sd.isPrimary()) {
				@SuppressWarnings("rawtypes")
				SecondaryKey secondaryKey=constructSecondaryKeyO(sd.getType(),sd.getSize(),secondary,key);
				this.indexs.get(setName).delete(secondaryKey);
			}
			else {
				PrimaryKey keyi=constructPrimaryKeyO(secondary);
				this.index.delete(keyi);

				for(Entry<String,Object> obj:row.entrySet()) {
					SchemaDescriptor sdt=this.schema.descriptors.get(obj.getKey());
					if(sdt.isPrimary()) continue;
					SecondaryKey keyr=constructSecondaryKeyO(sdt.getType(),sdt.getSize(),obj.getValue(),key);
					this.indexs.get(obj.getKey()).delete(keyr);
				}
			}
		
			switch(sd.getType()) {
			case TypeConst.VALUE_TYPE_INT:
				row.put(setName, Integer.parseInt(setValue));
				break;
			case TypeConst.VALUE_TYPE_LONG:
				row.put(setName, Long.parseLong(setValue));
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				row.put(setName, Float.parseFloat(setValue));
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				row.put(setName, Double.parseDouble(setValue));
				break;
			case TypeConst.VALUE_TYPE_STRING:
				row.put(setName, parseString(setValue));
				break;
			}


			if(!sd.isPrimary()) {
				this.simpleUpdate(key, mkRowO(row).r);
				SecondaryKey secondaryKeyl=constructSecondaryKey(sd.getType(),sd.getSize(),setValue,key);
				byte[] array=this.getKeyArray(key);
				PrimaryKeyValue kv=new PrimaryKeyValue(array,this.keyType);
				this.indexs.get(setName).insert(secondaryKeyl, kv);
			}
			else {
	     		key=row.get(this.schema.primaryKey);
				this.simpleInsert(key, mkRowO(row).r);
				byte[] array=this.getKeyArray(key);
				PrimaryKeyValue kv=new PrimaryKeyValue(array,this.keyType);
				for(Entry<String,Object> obj:row.entrySet()) {
					SchemaDescriptor sdt=this.schema.descriptors.get(obj.getKey());
					if(sdt.isPrimary()) continue;
					SecondaryKey keyr=constructSecondaryKeyO(sd.getType(),sd.getSize(),obj.getValue(),key);
					this.indexs.get(obj.getKey()).insert(keyr,kv);
				}
			}
		}
	}


	public Pair<Object,Row> mkRowB(HashMap<String,String> pairs) throws NumberFormatException, IOException {
		if(pairs.size()>this.schema.descriptors.size()) {
			throw new IllegalArgumentException("too many values");
		}
		List<String> values=new ArrayList<String>();
		for(String key:this.schema.descriptors.keySet()) {
			if(pairs.containsKey(key)) {
				values.add(pairs.get(key));
			}
			else {
				values.add(null);
			}
		}
		return mkRow(values);
	}

	public Pair<Object,Row> mkRowO(HashMap<String,Object> values) throws NumberFormatException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);
		Row row=new Row();
		Object res= null;
		Object key= null;
		for(Entry<String, SchemaDescriptor> entry:schema.descriptors.entrySet()) {
			SchemaDescriptor s=entry.getValue();
			Object t=values.get(entry.getKey());
			switch(s.getType()) {
			case TypeConst.VALUE_TYPE_INT:
				if(t!=null) {
					dos.writeChar(0);
					res=(Integer)t;
					dos.writeInt((Integer)res);
				}else {
					dos.writeChar('n');
					dos.writeInt(0);
				}
				break;
			case TypeConst.VALUE_TYPE_LONG:
				if(t!=null) {
					dos.writeChar(0);
					res=(Long)(t);
					dos.writeLong((Long)res);
				}else {
					dos.writeChar('n');
					dos.writeLong(0);
				}
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				if(t!=null) {
					dos.writeChar(0);
					res=(Float)(t);
					dos.writeFloat((Float)res);
				}else {
					dos.writeChar('n');
					dos.writeFloat(0);
				}
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				if(t!=null) {
					dos.writeChar(0);
					res=(Double)(t);
					dos.writeDouble((Double)res);
				}else {
					dos.writeChar('n');
					dos.writeDouble(0);
				}
				break;
			case TypeConst.VALUE_TYPE_STRING:
				if(t!=null) {
					dos.writeChar(0);
					res=t;
					dos.writeChars((String)t);
					for(int i=0;i<s.getSize()/TypeConst.VALUE_SIZE_CHAR-((String)t).length();i++) {
						dos.writeChar(0);
					}
				}else {
					dos.writeChar('n');
					for(int i=0;i<s.getSize()/TypeConst.VALUE_SIZE_CHAR;i++) {
						dos.writeChar(0);
					}
				}
				break;
			}
			if(s.isPrimary()) {
				key=(Object)res;
			}
		}
		dos.flush();
		byte[] array=outputStream.toByteArray();
		row.array=array;
		return new Pair<Object,Row>(key,row); 
	}


	public Pair<Object,Row> mkRow(List<String> values) throws NumberFormatException, IOException {
		if(values.size()>this.schema.descriptors.size()) {
			throw new IllegalArgumentException("too many values");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);
		Row row=new Row();
		Object res= null;
		Object key= null;
		int c=0;
		for(SchemaDescriptor s:schema.descriptors.values()) {
			String t=values.get(c);
			if(t==null&&s.isNotNull()) {
				throw new IllegalArgumentException("not null attribute cannot be null");
			}
			switch(s.getType()) {
			case TypeConst.VALUE_TYPE_INT:
				if(t!=null) {
					dos.writeChar(0);
					res=Integer.parseInt(t);
					dos.writeInt((Integer)res);
				}else {
					dos.writeChar('n');
					dos.writeInt(0);
				}
				break;
			case TypeConst.VALUE_TYPE_LONG:
				if(t!=null) {
					dos.writeChar(0);
					res=Long.parseLong(t);
					dos.writeLong((Long)res);
				}else {
					dos.writeChar('n');
					dos.writeLong(0);
				}
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				if(t!=null) {
					dos.writeChar(0);
					res=Float.parseFloat(t);
					dos.writeFloat((Float)res);
				}else {
					dos.writeChar('n');
					dos.writeFloat(0);
				}
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				if(t!=null) {
					dos.writeChar(0);
					res=Double.parseDouble(t);
					dos.writeDouble((Double)res);
				}else {
					dos.writeChar('n');
					dos.writeDouble(0);
				}
				break;
			case TypeConst.VALUE_TYPE_STRING:
				if(t!=null) {
					dos.writeChar(0);
					t=parseString(t);
					res=t;
					dos.writeChars((String)t);
					for(int i=0;i<s.getSize()/TypeConst.VALUE_SIZE_CHAR-t.length();i++) {
						dos.writeChar(0);
					}
				}else {
					dos.writeChar('n');
					for(int i=0;i<s.getSize()/TypeConst.VALUE_SIZE_CHAR;i++) {
						dos.writeChar(0);
					}
				}
				break;
			}
			if(s.isPrimary()) {
				key=(Object)res;
			}
			c++;
		}
		dos.flush();
		byte[] array=outputStream.toByteArray();
		row.array=array;
		return new Pair<Object,Row>(key,row); 
	}

	@SuppressWarnings({ "unchecked", "null" })
	public void simpleUpdate(Object key, Row row) throws IOException {
		if(key==null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		switch(keyType) {
			case TypeConst.VALUE_TYPE_INT:
				PrimaryKey<Integer> keyi = new PrimaryKey<Integer>((Integer)key, TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT);
				index.update(keyi, row);	
				break;
			case TypeConst.VALUE_TYPE_LONG:
				PrimaryKey<Long> keyl = new PrimaryKey<Long>((Long)key, TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG);
				index.update(keyl, row);	
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				PrimaryKey<Float> keyf = new PrimaryKey<Float>((Float)key, TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT);
				index.update(keyf, row);	
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				PrimaryKey<Double> keyd = new PrimaryKey<Double>((Double)key, TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE);
				index.update(keyd, row);	
				break;
			case TypeConst.VALUE_TYPE_STRING:
				PrimaryKey<String> keys = new PrimaryKey<String>((String)key, TypeConst.VALUE_TYPE_STRING, keySize);
				index.update(keys, row);	
				break;
			}
	}

	@SuppressWarnings({ "unchecked", "null" })
	public void simpleInsert(Object key, Row row) throws IOException {
		if(key==null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		switch(keyType) {
			case TypeConst.VALUE_TYPE_INT:
				PrimaryKey<Integer> keyi = new PrimaryKey<Integer>((Integer)key, TypeConst.VALUE_TYPE_INT, TypeConst.VALUE_SIZE_INT);
				index.insert(keyi, row);	
				break;
			case TypeConst.VALUE_TYPE_LONG:
				PrimaryKey<Long> keyl = new PrimaryKey<Long>((Long)key, TypeConst.VALUE_TYPE_LONG, TypeConst.VALUE_SIZE_LONG);
				index.insert(keyl, row);	
				break;
			case TypeConst.VALUE_TYPE_FLOAT:
				PrimaryKey<Float> keyf = new PrimaryKey<Float>((Float)key, TypeConst.VALUE_TYPE_FLOAT, TypeConst.VALUE_SIZE_FLOAT);
				index.insert(keyf, row);	
				break;
			case TypeConst.VALUE_TYPE_DOUBLE:
				PrimaryKey<Double> keyd = new PrimaryKey<Double>((Double)key, TypeConst.VALUE_TYPE_DOUBLE, TypeConst.VALUE_SIZE_DOUBLE);
				index.insert(keyd, row);	
				break;
			case TypeConst.VALUE_TYPE_STRING:
				PrimaryKey<String> keys = new PrimaryKey<String>((String)key, TypeConst.VALUE_TYPE_STRING, keySize);
				index.insert(keys, row);	
				break;
			}
	}

	public List<String> fixValues(List<String> values) {
		if(!this.hasPrimary) {
			gCount+=1;
			values.add(Integer.toString(gCount));
		}
		return values;
	}

	public HashMap<String, String> fixPairs(HashMap<String, String> pairs) {
		if(!this.hasPrimary) {
			gCount+=1;
			pairs.put("_id", Integer.toString(gCount));
		}
		return pairs;
	}


}
