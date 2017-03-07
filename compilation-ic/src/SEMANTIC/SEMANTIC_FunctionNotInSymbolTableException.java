package SEMANTIC;

public class SEMANTIC_FunctionNotInSymbolTableException extends SEMANTIC_SemanticAnalysisException 
{
	private static final long serialVersionUID = 1L;

	public SEMANTIC_FunctionNotInSymbolTableException(String className, String functionName){
		super(String.format("%s.%s"));
	}
}
