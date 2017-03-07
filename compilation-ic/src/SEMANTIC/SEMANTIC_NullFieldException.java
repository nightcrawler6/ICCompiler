package SEMANTIC;

public class SEMANTIC_NullFieldException extends SEMANTIC_SemanticErrorException 
{
	private static final long serialVersionUID = 1L;

	public SEMANTIC_NullFieldException(String fieldName){
		super("The field " + fieldName + " is null");
	}
}
