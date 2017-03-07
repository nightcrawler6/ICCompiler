package SEMANTIC;

public class SEMANTIC_SymbolInfoNode{
	
	public SEMANTIC_SymbolInfo symbolInfo;
	public SEMANTIC_SymbolInfoNode hiddenSymbol;
	public SEMANTIC_SymbolInfoNode nextSymbolInScope;
	
	public SEMANTIC_SymbolInfoNode(SEMANTIC_SymbolInfo symbolInfo){
		this.symbolInfo=symbolInfo;
	}
	
	public SEMANTIC_SymbolInfoNode(SEMANTIC_SymbolInfo symbolInfo,SEMANTIC_SymbolInfoNode hiddenSymbol,SEMANTIC_SymbolInfoNode nextSymbolInScope){
		this.symbolInfo=symbolInfo;
		this.hiddenSymbol=hiddenSymbol;
		this.nextSymbolInScope=nextSymbolInScope;
	}
}
