package SEMANTIC;

import java.util.ArrayList;
import java.util.List;

import UTILS.DebugPrint;

public class SEMANTIC_FunctionSymbolInfo extends SEMANTIC_SymbolInfo{

	public SEMANTIC_ICTypeInfo returnType;
	public List<SEMANTIC_ICTypeInfo> argumentsTypes;
	public int frameSize = 0;
	public int offset=0;
	public int currentIfIndex = 0;
	public int currentWhileIndex = 0;
	public boolean isMainFunc = false;
	public boolean isPrintFunc = false;

	public String functionLabel;
	
	public SEMANTIC_FunctionSymbolInfo(String symbolName, SEMANTIC_ICTypeInfo returnType,List<SEMANTIC_ICTypeInfo> argumentsTypes){
		super(symbolName);
		this.argumentsTypes=argumentsTypes;
		this.returnType=returnType;
	}
	public SEMANTIC_FunctionSymbolInfo(String symbolName, SEMANTIC_ICTypeInfo returnType,List<SEMANTIC_ICTypeInfo> argumentsTypes, int frameSize){
		super(symbolName);
		this.argumentsTypes=argumentsTypes;
		this.returnType=returnType;
		this.frameSize=frameSize;
	}
	
	public SymbolType getSymbolType(){
		return SymbolType.SYMBOL_TYPE_FUNCTION;
	}
	
	public void addFormal(SEMANTIC_ICTypeInfo formal){
		if(this.argumentsTypes==null){
			this.argumentsTypes=new ArrayList<SEMANTIC_ICTypeInfo>();
		}
		this.argumentsTypes.add(formal);
	}
	public boolean equals(Object obj){
		SEMANTIC_FunctionSymbolInfo comparedSymbol=(SEMANTIC_FunctionSymbolInfo)obj;
		if((returnType!=null)&&(comparedSymbol.returnType!=null)&&(!returnType.equals(comparedSymbol.returnType))){
			return false;
		}
		if((this.argumentsTypes!=null) && (comparedSymbol.argumentsTypes==null)){
			return false;
		}
		if((comparedSymbol.argumentsTypes!=null) && (this.argumentsTypes==null)){
			return false;
		}
		
		if((argumentsTypes!=null)&&(comparedSymbol.argumentsTypes!=null)&&(!argumentsTypes.equals(comparedSymbol.argumentsTypes))){
			return false;
		}
		if((symbolName!=null)&&(comparedSymbol.symbolName!=null)&&(!symbolName.equals(comparedSymbol.symbolName))){
			return false;
		}
		return true;
	}
	
	public boolean validateMainIsValid() throws SEMANTIC_NotMainMethodException{
		if(!this.symbolName.equals(SEMANTIC_SymbolTable.MAIN_FUNC_SYMBOL_NAME)){
			throw new SEMANTIC_NotMainMethodException();
		}
		
		if((this.argumentsTypes == null) || (this.argumentsTypes.size()!=1)){
			DebugPrint.print("FunctionSymbolInfo.validateMainIsValid: The main method has a wrong number of arguments.");
			return false;
		}
		if(!(this.argumentsTypes.get(0).ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_STRING))){
			DebugPrint.print("FunctionSymbolInfo.validateMainIsValid: The main method's argument has an invalid argument");
			return false;
		}
		if(this.argumentsTypes.get(0).pointerDepth!=1){
			DebugPrint.print("FunctionSymbolInfo.validateMainIsValid: The main method's argument has an invalid argument");
			return false;
		}
		if(!this.returnType.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_VOID)){
			String debugMessage = String.format("FunctionSymbolInfo.validateMainIsValid: The main method's argument has an invalid return type: %s instead of void.", 
					returnType);
			DebugPrint.print(debugMessage);
			return false;
		}
		return true;
	}
	
	public int getNewIfIndex(){
		int newIfIndex = currentIfIndex;
		currentIfIndex++;
		return newIfIndex;
	}
	
	public int getNewWhileIndex(){
		int newWhileIndex = currentWhileIndex;
		currentWhileIndex++;
		return newWhileIndex;
	}
}
