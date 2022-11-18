package nextflow.biojava;

import groovy.lang.Closure;
import groovyx.gpars.dataflow.DataflowWriteChannel;
import nextflow.Channel;
import nextflow.Global;
import nextflow.NF;
import nextflow.Session;
import nextflow.biojava.functions.DNAFunctions;
import nextflow.biojava.functions.ProteinFunctions;
import nextflow.biojava.functions.RNAFunctions;
import nextflow.extension.CH;
import nextflow.plugin.extension.Factory;
import nextflow.plugin.extension.Function;
import nextflow.plugin.extension.PluginExtensionPoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SLF4JLogFactory;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class PluginExtension extends PluginExtensionPoint{

    static final Log log = SLF4JLogFactory.getLog(PluginExtension.class);

    /*
     * A session hold information about current execution of the script
     */
    private Session session;

    /*
    * A Map holding the configuration of the plugin
    */
    private BioConfig config;

    @Override
    protected void init(Session session) {
        this.session = session;
        this.config = BioConfig.fromMap(session.getConfig());
    }

    @Function
    public DNASequence createDNASequence(String sequence) throws CompoundNotFoundException{
        return DNAFunctions.createSequence(sequence);
    }

    @Function
    public DNASequence createDNASequence(Path sequence) throws CompoundNotFoundException, IOException {
        return DNAFunctions.createSequence(Files.readString(sequence));
    }

    @Function
    public RNASequence createRNASequence(String sequence) throws CompoundNotFoundException{
        return RNAFunctions.createSequence(sequence);
    }

    @Function
    public RNASequence createRNASequence(Path sequence) throws CompoundNotFoundException, IOException {
        return RNAFunctions.createSequence(Files.readString(sequence));
    }

    @Function
    public ProteinSequence getProteinSequenceForId(String uniProtId) throws Exception{
        return ProteinFunctions.getSequenceForId(uniProtId, this.config.getProteineRepoURL());
    }

    @Factory
    public DataflowWriteChannel fromRNAFasta(Path path){
        final DataflowWriteChannel channel = CH.create();
        if(NF.isDsl2()){
            Closure closure = new Closure(null) {
                public Object doCall() {
                    fromRNAFasta(channel,true, path);
                    return null;
                }
            };
            session.addIgniter(closure);
        }else{
            fromRNAFasta(channel, false, path);
        }
        return channel;
    }

    @Factory
    public DataflowWriteChannel fromDNAFasta(Path path){
        final DataflowWriteChannel channel = CH.create();
        if(NF.isDsl2()){
            Closure closure = new Closure(null) {
                public Object doCall() {
                    fromDNAFasta(channel,true, path);
                    return null;
                }
            };
            session.addIgniter(closure);
        }else{
            fromDNAFasta(channel, false, path);
        }
        return channel;
    }

    @Factory
    public DataflowWriteChannel fromProteinFasta(Path path){
        final DataflowWriteChannel channel = CH.create();
        if(NF.isDsl2()){
            Closure closure = new Closure(null) {
                public Object doCall() {
                    fromProteinFasta(channel,true, path);
                    return null;
                }
            };
            session.addIgniter(closure);
        }else{
            fromProteinFasta(channel, false, path);
        }
        return channel;
    }

    private void fromDNAFasta(DataflowWriteChannel channel, boolean async, Path path){
        try {
            Map<String,Object> map = (Map)DNAFunctions.readFastaSequence(path);
            emitList(map, channel, async);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fromRNAFasta(DataflowWriteChannel channel, boolean async, Path path){
        try {
            Map<String,Object> map = (Map)RNAFunctions.readFastaSequence(path);
            emitList(map, channel, async);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fromProteinFasta(DataflowWriteChannel channel, boolean async, Path path){
        try {
            Map<String,Object> map = (Map)ProteinFunctions.readFastaSequence(path);
            emitList(map, channel, async);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void emitList(Map<String, Object> map, DataflowWriteChannel channel, boolean async){
        if( async ){
            CompletableFuture.runAsync(()->{
                map.forEach((id,sequence)->{
                    channel.bind(List.of(id,sequence));
                });
                channel.bind(Channel.STOP);
            }).exceptionally(PluginExtension::handlerException);
        }else{
            map.forEach((id,sequence)->{
                channel.bind(List.of(id,sequence));
            });
            channel.bind(Channel.STOP);
        }
    }

    static private Void handlerException(Throwable e) {
        final Throwable error = e.getCause() != null ? e.getCause() : e;
        log.error(error.getMessage(), error);
        final Session session = (Session) Global.getSession();
        if(session != null)
            session.abort(error);
        return null;
    }
}
