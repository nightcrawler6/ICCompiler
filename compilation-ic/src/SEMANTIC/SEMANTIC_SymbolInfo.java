package SEMANTIC;

public abstract class SEMANTIC_SymbolInfo {
	public String symbolName;
	public enum SymbolType{
		SYMBOL_TYPE_CLASS,
		SYMBOL_TYPE_FUNCTION,
		SYMBOL_TYPE_VARIABLE
	}
	
	public SEMANTIC_SymbolInfo(String symbolName){
		this.symbolName=symbolName;
	}
	
	public abstract SymbolType getSymbolType();
}
