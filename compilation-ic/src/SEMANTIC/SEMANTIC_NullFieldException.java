package SEMANTIC;

public class SEMANTIC_NullFieldException extends SEMANTIC_SemanticAnalysisException 
{
	private static final long serialVersionUID = 1L;

	public SEMANTIC_NullFieldException(String fieldName){
		super("The field " + fieldName + " is null");
	}
}
