package edu.mit.hstore.callbacks;

import org.apache.log4j.Logger;

import com.google.protobuf.RpcCallback;

import edu.mit.dtxn.Dtxn;
import edu.mit.hstore.HStoreSite;

/**
 * Callback used to send the final output bytes to the client
 * @author pavlo
 */
public class ClientResponseFinalCallback extends AbstractTxnCallback implements RpcCallback<Dtxn.FinishResponse> {
    private static final Logger LOG = Logger.getLogger(ClientResponseFinalCallback.class);
    
    private final byte output[];
    private final Dtxn.FragmentResponse.Status status;
    
    public ClientResponseFinalCallback(HStoreSite hstore_coordinator, long txn_id, byte output[], Dtxn.FragmentResponse.Status status, RpcCallback<byte[]> done) {
        super(hstore_coordinator, txn_id, done);
        this.output = output;
        this.status = status;
    }
    
    public byte[] getOutput() {
        return output;
    }
    
    /**
     * We can finally send out the final answer to the client
     */
    @Override
    public void run(Dtxn.FinishResponse parameter) {
        final boolean debug = LOG.isDebugEnabled();
        
        if (debug) {
            LOG.debug("Invoking final response callback for txn #" + this.txn_id + " [" +
                      "status=" + this.status + ", " + 
                      "payload=" + parameter.hasPayload() + ", " +
                      "bytes=" + this.output.length + "]");
        }
        
        // Always clean-up the transaction with the HStoreSite
        this.hstore_site.completeTransaction(this.txn_id);
        
        // But only send the output to the client if this wasn't a mispredict
        if (this.status != Dtxn.FragmentResponse.Status.ABORT_MISPREDICT) {
            this.done.run(this.output);
        } else if (debug) {
            LOG.debug(String.format("Ignoring Dtxn.FinishResponse for ClientResponse because txn #%d was %s", this.txn_id, this.status));
        }
    }
} // END CLASS
