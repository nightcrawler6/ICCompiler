package SEMANTIC;

public class SEMANTIC_NoSuchMethodInTableException extends SEMANTIC_SemanticErrorException 
{
	private static final long serialVersionUID = 1L;

	public SEMANTIC_NoSuchMethodInTableException(String className, String functionName){
		super(String.format("%s.%s"));
	}
}
