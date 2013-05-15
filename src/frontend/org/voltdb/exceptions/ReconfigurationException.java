package org.voltdb.exceptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.voltdb.VoltType;
import org.voltdb.utils.VoltTypeUtil;

import edu.brown.hashing.ReconfigurationPlan.ReconfigurationRange;
import edu.brown.hstore.reconfiguration.ReconfigurationConstants.ReconfigurationProtocols;

/**
 * Exceptions thrown during the Reconfiguration process
 * 
 * @author vaibhav
 */
public class ReconfigurationException extends SerializableException {

    public static final long serialVersionUID = 0L;
    public ReconfigurationProtocols reconfigurationProtocols;

    public enum ExceptionTypes {
        TUPLES_MIGRATED_OUT, TUPLES_NOT_MIGRATED, BOTH, ALL_RANGES_MIGRATED_OUT, ALL_RANGES_MIGRATED_IN
    };

    public ExceptionTypes exceptionType;
    public List<ReconfigurationRange<? extends Comparable<?>>> dataMigratedOut = null;
    public List<ReconfigurationRange<? extends Comparable<?>>> dataNotYetMigrated = null;

    public ReconfigurationException(ReconfigurationProtocols reconfigurationProtocols) {
        this.reconfigurationProtocols = reconfigurationProtocols;
    }

    public ReconfigurationException() {
        // TODO Auto-generated constructor stub
    }
    
    

    public ReconfigurationException(ExceptionTypes exceptionType) {
        super();
        this.exceptionType = exceptionType;
    }

    public ReconfigurationException(List<ReconfigurationRange<? extends Comparable<?>>> dataMigratedOut, List<ReconfigurationRange<? extends Comparable<?>>> dataNotYetMigrated) {
        super();
        this.dataMigratedOut = dataMigratedOut;
        this.dataNotYetMigrated = dataNotYetMigrated;
        if (dataMigratedOut != null && dataMigratedOut.size() > 0 && dataNotYetMigrated != null && dataMigratedOut.size() > 0) {
            this.exceptionType = ExceptionTypes.BOTH;
        } else if (dataMigratedOut != null && dataMigratedOut.size() > 0) {
            this.exceptionType = ExceptionTypes.TUPLES_MIGRATED_OUT;
        } else if (dataNotYetMigrated != null && dataMigratedOut.size() > 0) {
            this.exceptionType = ExceptionTypes.TUPLES_NOT_MIGRATED;
        }

    }

    public ReconfigurationException(ExceptionTypes exceptionType, String table_name, int old_partition, int new_partition, Comparable key) {
        List<ReconfigurationRange<? extends Comparable<?>>> keys = new ArrayList<>();

        ReconfigurationRange<? extends Comparable<?>> range;

        range = new ReconfigurationRange(table_name, VoltType.typeFromObject(key), key, key, old_partition, new_partition);
        keys.add(range);
        this.exceptionType = exceptionType;
        if (exceptionType == ExceptionTypes.TUPLES_NOT_MIGRATED) {
            dataNotYetMigrated = keys;
            dataMigratedOut = new ArrayList<>();
        } else if (exceptionType == ExceptionTypes.TUPLES_MIGRATED_OUT) {
            dataMigratedOut = keys;
            dataNotYetMigrated = new ArrayList<>();
        } else
            throw new NotImplementedException("ExceptionType for single key not supported " + exceptionType);

    }

    public ReconfigurationProtocols getReconfigurationProtocols() {
        return this.reconfigurationProtocols;
    }

    @Override
    public String toString() {
        return "ReconfigurationException [reconfigurationProtocols=" + reconfigurationProtocols + ", exceptionType=" + exceptionType + ", dataMigratedOut=" + dataMigratedOut + ", dataNotYetMigrated="
                + dataNotYetMigrated + "]";
    }

}
