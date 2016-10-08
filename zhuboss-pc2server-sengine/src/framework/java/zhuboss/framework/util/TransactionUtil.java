package zhuboss.framework.util;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionUtil {
	public static TransactionStatus getTransaction(JtaTransactionManager jtaTransactionManager){
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setTimeout(-1);
		TransactionStatus transactionStatus =jtaTransactionManager.getTransaction(definition);
		return transactionStatus;
	
	}
	
}
