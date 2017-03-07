package AST;

import IR.IR_Statement;
import IR.IR_StatementList;
import SEMANTIC.SEMANTIC_ClassOrFunctionNamesNotInitializedExecption;
import SEMANTIC.SEMANTIC_ExpectedReturnTypeIsNotInitializedException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_TailWithNoHeadException;
import UTILS.DebugPrint;

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
	
	private SEMANTIC_ICTypeInfo validateListWithNoHead() throws SEMANTIC_TailWithNoHeadException{
		if (tail == null){
			return new SEMANTIC_ICTypeInfo();
		}
		else{
			throw new SEMANTIC_TailWithNoHeadException();
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
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		if (head == null){
			return validateListWithNoHead();
		}
		
		if (this.expectedReturnType == null){
			throw new SEMANTIC_ExpectedReturnTypeIsNotInitializedException();
		}
		
		head.expectedReturnType = this.expectedReturnType;
		if (head.validate(className) == null){
			DebugPrint.print("AST_STMT_LIST.validate: The head isn't valid.");
			return null;
		}
		
		if (tail != null){
			tail.expectedReturnType = this.expectedReturnType;
			if (tail.validate(className) == null){
				DebugPrint.print("AST_STMT_LIST.validate: The tail isn't valid.");
				return null;
			}
		}
		
		markIfAlwaysReturnValue();
		return new SEMANTIC_ICTypeInfo();
	}
	
	private void bequeathClassAndFunctionNamesToChildren() throws SEMANTIC_ClassOrFunctionNamesNotInitializedExecption{
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
	
	private IR_StatementList createTailIR() throws SEMANTIC_SemanticAnalysisException{
		if (tail != null){
			return tail.createIR();
		}
		else{
			return null;
		}
	}
	
	@Override
	public IR_StatementList createIR() throws SEMANTIC_SemanticAnalysisException{
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