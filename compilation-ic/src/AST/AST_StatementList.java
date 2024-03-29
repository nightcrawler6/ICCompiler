package AST;

import IR.IR_Statement;
import IR.IR_StatementList;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_NoInitForReturnTypeException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_NoHeadFoundException;

public class AST_StatementList extends AST_Statement{

	public AST_Statement head;
	public AST_StatementList tail; 
	
	public AST_StatementList(AST_Statement head,AST_StatementList tail){
		AST_StatementList iterator=tail;
		if((iterator!=null)&&(!iterator.isEmpty())){
			this.head=tail.head;
			while(iterator.tail!=null){
				iterator=iterator.tail;
			}
			iterator.tail=new AST_StatementList(head, null);
			this.tail=tail.tail;
		}
		else{
			this.head=head;
			this.tail=null;
		}
	}
	
	public AST_StatementList(AST_StatementList other){
		this.head = other.head;
		this.tail = other.tail;
	}
	
	public boolean isEmpty(){
		return ((this.tail==null)&&(this.head==null));
	}
	
	private SEMANTIC_ICTypeInfo validateListWithNoHead() throws SEMANTIC_NoHeadFoundException{
		if (tail == null){
			return new SEMANTIC_ICTypeInfo();
		}
		else{
			throw new SEMANTIC_NoHeadFoundException();
		}
	}
	
	private void markIfAlwaysReturnValue(){
		if (tail == null){
			doesAlwaysReturnValue = head.doesAlwaysReturnValue;	
		}
		else{
			doesAlwaysReturnValue = head.doesAlwaysReturnValue || tail.doesAlwaysReturnValue;	
		}
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		if (head == null){
			return validateListWithNoHead();
		}
		
		if (this.expectedReturnType == null){
			throw new SEMANTIC_NoInitForReturnTypeException();
		}
		
		head.expectedReturnType = this.expectedReturnType;
		if (head.validate(className) == null){
			return null;
		}
		
		if (tail != null){
			tail.expectedReturnType = this.expectedReturnType;
			if (tail.validate(className) == null){
				return null;
			}
		}
		
		markIfAlwaysReturnValue();
		return new SEMANTIC_ICTypeInfo();
	}
	
	private void bequeathClassAndFunctionNamesToChildren() throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		if (head != null){
			head.currentClassName = this.currentClassName;
			head.currentFunctionName = this.currentFunctionName;
			
			if (tail != null){
				tail.currentClassName = this.currentClassName;
				tail.currentFunctionName = this.currentFunctionName;
			}
		}
	}
	
	private IR_StatementList createTailIR() throws SEMANTIC_SemanticErrorException{
		if (tail != null){
			return tail.createIR();
		}
		else{
			return null;
		}
	}
	
	@Override
	public IR_StatementList createIR() throws SEMANTIC_SemanticErrorException{
		bequeathClassAndFunctionNamesToChildren();
		
		if (head == null){
			return null;
		}
		
		IR_Statement headIR = head.createIR();
		IR_StatementList tailIR = createTailIR();
		if (headIR != null){
			return new IR_StatementList(headIR, tailIR);
		}
		else{
			return tailIR;
		}
	}
}