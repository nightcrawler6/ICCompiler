package AST;


import SEMANTIC.SEMANTIC_NoSuchClassInTableException;
import SEMANTIC.SEMANTIC_NoInitForClassException;
import SEMANTIC.SEMANTIC_ClassSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_NoInitForIDListException;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;

public class AST_IDList extends AST_Node{
	public String head;
	public AST_IDList tail;
	public SEMANTIC_ICTypeInfo type;
	public AST_IDList(String head, AST_IDList tail){
		AST_IDList iterator=tail;
		if((iterator!=null)&&(!iterator.isEmpty())){
			this.head=tail.head;
			this.type=tail.type;
			while(iterator.tail!=null){
				iterator=iterator.tail;
			}
			iterator.tail=new AST_IDList(head, null);
			this.tail=tail.tail;
		}
		else{
			this.head=head;
			this.tail=null;
			this.type=null;
		}
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		if(type==null){
			throw new SEMANTIC_NoInitForIDListException();
		}
		
		SEMANTIC_VariableSymbolInfo fieldInfo = new SEMANTIC_VariableSymbolInfo(head, type);
		if(SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(className, head)!=null){
			return null;
		}
		SEMANTIC_SymbolTable.insertNewSymbol(fieldInfo);
		
		SEMANTIC_SymbolTable.addFieldToClass(className, fieldInfo);
		if(tail!=null){
			this.tail.type=type;
			if(this.tail.validate(className)==null){
				return null;
			}
		}
	
		return new SEMANTIC_ICTypeInfo();
	}
	public boolean isEmpty(){
		return ((this.tail==null)&&(this.head==null));
	}
	
	public void createIR() throws SEMANTIC_NoSuchClassInTableException, SEMANTIC_NoInitForClassException{
		assertClassNameInitialized();
		SEMANTIC_ClassSymbolInfo currentClassSymbolInfo=SEMANTIC_SymbolTable.getClassSymbolInfo(currentClassName);
		int fieldOffset=currentClassSymbolInfo.size;
		SEMANTIC_VariableSymbolInfo fieldInfo = new SEMANTIC_VariableSymbolInfo(head, type, fieldOffset, true);
		SEMANTIC_SymbolTable.insertNewSymbol(fieldInfo);
		SEMANTIC_SymbolTable.addFieldToClass(currentClassName, fieldInfo);
		if(this.tail!=null){
			this.tail.currentClassName=this.currentClassName;
			this.tail.createIR();
		}
	}
}