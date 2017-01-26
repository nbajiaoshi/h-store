package edu.mit.benchmark.b2w_sku_key.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import edu.mit.benchmark.b2w_sku_key.B2WUtil;

@ProcInfo(
        partitionInfo = "STK_INVENTORY_STOCK_QUANTITY.partition_key: 0",
        singlePartition = true
    )
public class GetStockQuantity extends VoltProcedure {
//    private static final Logger LOG = Logger.getLogger(VoltProcedure.class);
//    private static final LoggerBoolean debug = new LoggerBoolean();
//    private static final LoggerBoolean trace = new LoggerBoolean();
//    static {
//        LoggerUtil.setupLogging();
//        LoggerUtil.attachObserver(LOG, debug, trace);
//    }
        
    public final SQLStmt getStockQtyStmt = new SQLStmt("SELECT * FROM STK_INVENTORY_STOCK_QUANTITY WHERE partition_key = ? AND id = ? ");
        
    public VoltTable[] run(int partition_key, String stock_id, long sleep_time){
        B2WUtil.sleep(sleep_time);
        
        voltQueueSQL(getStockQtyStmt, partition_key, stock_id);
        
        return voltExecuteSQL(true);
    }

}
