package SEMANTIC;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SEMANTIC_ClassSymbolInfo extends SEMANTIC_SymbolInfo{
	public List<String> virtualFunctionsOrder;
	public Hashtable<String,String> virtualFunctionsTable;
	public String extendedClassName;
	public List<SEMANTIC_VariableSymbolInfo> fields;
	public List<SEMANTIC_FunctionSymbolInfo> methods;
	public int size;
	
	public SEMANTIC_ClassSymbolInfo(String symbolName, String extendedClassName,List<SEMANTIC_VariableSymbolInfo> fields, List<SEMANTIC_FunctionSymbolInfo> methods){
		super(symbolName);
		
		this.extendedClassName=extendedClassName;
		this.fields=fields;
		this.methods=methods;
		
		if(this.extendedClassName!=null){
			SEMANTIC_ClassSymbolInfo father = SEMANTIC_SymbolTable.getClassSymbolInfo(extendedClassName);
			this.size=father.size;
			this.virtualFunctionsTable=new Hashtable<>(father.virtualFunctionsTable);
			this.virtualFunctionsOrder=new ArrayList<String>(father.virtualFunctionsOrder);
		}
		else{
			this.size=SEMANTIC_SymbolTable.ADDRESS_SIZE;
			this.virtualFunctionsTable=new Hashtable<String,String>();
			this.virtualFunctionsOrder=new ArrayList<String>();
		}
	}
	
	public SEMANTIC_ClassSymbolInfo(String symbolName, String extendedClassName,List<SEMANTIC_VariableSymbolInfo> fields, List<SEMANTIC_FunctionSymbolInfo> methods, int size){
		super(symbolName);
		this.extendedClassName=extendedClassName;
		this.fields=fields;
		this.methods=methods;
		this.size=size;
	}
	
	public String getVFTableLabel(){
		return String.format("VFTable_%s", this.symbolName);
	}
	
	public void addMethod(SEMANTIC_FunctionSymbolInfo method){
		if(this.methods==null){
			this.methods=new ArrayList<SEMANTIC_FunctionSymbolInfo>();
		}
		this.methods.add(method);
		
		if((!method.isMainFunc) && (method.functionLabel!=null)&&(!method.isPrintFunc)){
			if(!this.virtualFunctionsOrder.contains(method.symbolName)){
				method.offset=this.virtualFunctionsOrder.size()*SEMANTIC_SymbolTable.ADDRESS_SIZE;
				this.virtualFunctionsOrder.add(method.symbolName);
			}
			
			this.virtualFunctionsTable.put(method.symbolName, method.functionLabel);
		}
	}
	
	public void addField(SEMANTIC_VariableSymbolInfo field){
		if(this.fields==null){
			this.fields=new ArrayList<SEMANTIC_VariableSymbolInfo>();
		}
		this.fields.add(field);
		this.size+= SEMANTIC_SymbolTable.ADDRESS_SIZE;
	}

	public int getMainFunctionsCount(){
		int count=0;
		
		if (this.methods == null){
			return 0;
		}
		
		for(int i=0;i<this.methods.size();i++){
			SEMANTIC_FunctionSymbolInfo methodSymbolInfo = this.methods.get(i);
			if(methodSymbolInfo.symbolName.equals(SEMANTIC_SymbolTable.MAIN_FUNC_SYMBOL_NAME)){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public SymbolType getSymbolType() {
		return SymbolType.SYMBOL_TYPE_CLASS;
	}
	
	public SEMANTIC_VariableSymbolInfo searchField(String symbolName){
		if(this.fields!=null){
			for(int i=0;i<this.fields.size();i++){
				if(this.fields.get(i).symbolName.equals( symbolName)){
					return this.fields.get(i);
				}
			}
		}
		return null;
	}
	
	public SEMANTIC_FunctionSymbolInfo searchMethod(String symbolName){
		if(this.methods!=null){
			for(int i=0;i<this.methods.size();i++){
				if(this.methods.get(i).symbolName.equals(symbolName))
					return this.methods.get(i);
			}
		}
		return null;
	}
	
}
