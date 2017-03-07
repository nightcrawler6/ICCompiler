package AST;

import SEMANTIC.SEMANTIC_NoSuchClassInTableException;
import SEMANTIC.SEMANTIC_NoInitForClassException;
import SEMANTIC.SEMANTIC_ClassSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;

public class AST_Field extends AST_FieldMethod{
	public AST_Type type;
	public String fieldName;
	public AST_IDList idsList;
	public SEMANTIC_ICTypeInfo icFieldType;
	public AST_Field(AST_Type type,String fieldName,AST_IDList idsList){
		this.type = type;
		this.fieldName=fieldName;
		this.idsList=idsList;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		icFieldType=type.validate(className);
		if(icFieldType==null){
			return null;
		}
		
		if(SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(className,fieldName) != null){
			return null;
		}
		
		SEMANTIC_VariableSymbolInfo fieldInfo = new SEMANTIC_VariableSymbolInfo(fieldName, icFieldType);
		SEMANTIC_SymbolTable.insertNewSymbol(fieldInfo);
		SEMANTIC_SymbolTable.addFieldToClass(className, fieldInfo);
		
		if((idsList != null)&&(!idsList.isEmpty())){
			this.idsList.type=icFieldType;
			if(this.idsList.validate(className)==null){
				return null;
			}
		}
		return new SEMANTIC_ICTypeInfo();
	}
	
	public void createIR() throws SEMANTIC_NoSuchClassInTableException, SEMANTIC_NoInitForClassException{
		assertClassNameInitialized();
		SEMANTIC_ClassSymbolInfo currentClassSymbolInfo=SEMANTIC_SymbolTable.getClassSymbolInfo(currentClassName);
		int fieldOffset=currentClassSymbolInfo.size;
		SEMANTIC_VariableSymbolInfo fieldInfo = new SEMANTIC_VariableSymbolInfo(fieldName, icFieldType,fieldOffset,true);
		SEMANTIC_SymbolTable.insertNewSymbol(fieldInfo);
		SEMANTIC_SymbolTable.addFieldToClass(currentClassName, fieldInfo);
		
		if ((idsList != null)&&(idsList.head!=null)){
			idsList.currentClassName = this.currentClassName;
			idsList.createIR();	
		}
	}
}