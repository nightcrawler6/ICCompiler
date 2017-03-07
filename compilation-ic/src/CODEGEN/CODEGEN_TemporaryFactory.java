package CODEGEN;

import java.util.ArrayList;
import java.util.List;

import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class CODEGEN_TemporaryFactory{ 
	public static List<CODEGEN_Temporary> temps;
	public static final int MAX_TEMP_SIZE = 1000;
	
	public static void reset(){
		temps = new ArrayList<CODEGEN_Temporary>(); 
	}
	
	public static CODEGEN_Temporary getAndAddNewTemp() throws SEMANTIC_TempsPastLimitException{
		// TODO: Delete this exception before submitting.
		if (temps.size() == MAX_TEMP_SIZE){
			throw new SEMANTIC_TempsPastLimitException();
		}
		CODEGEN_Temporary newTemp=new CODEGEN_Temporary(temps.size());
		temps.add(newTemp);
		return newTemp;
	}
}