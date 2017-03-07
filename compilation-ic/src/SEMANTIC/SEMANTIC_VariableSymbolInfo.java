package SEMANTIC;

public class SEMANTIC_VariableSymbolInfo extends SEMANTIC_SymbolInfo
{	
	public SEMANTIC_ICTypeInfo variableType;
	public int offset=Integer.MAX_VALUE;
	public boolean isField=false;
	public SEMANTIC_VariableSymbolInfo(String symbolName,SEMANTIC_ICTypeInfo variableType){
		super(symbolName);
		this.variableType=variableType;
	}
	public SEMANTIC_VariableSymbolInfo(String symbolName,SEMANTIC_ICTypeInfo variableType, int offset, boolean isField){
		super(symbolName);
		this.variableType=variableType;
		this.offset=offset;
		this.isField=isField;
	}
	public SymbolType getSymbolType(){
		return SymbolType.SYMBOL_TYPE_VARIABLE;
	}

}
