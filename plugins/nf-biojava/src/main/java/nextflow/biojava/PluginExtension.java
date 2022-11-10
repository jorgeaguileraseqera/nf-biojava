package nextflow.biojava;

import nextflow.Session;
import nextflow.plugin.extension.Function;
import nextflow.plugin.extension.PluginExtensionPoint;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class PluginExtension extends PluginExtensionPoint{

    /*
     * A session hold information about current execution of the script
     */
    private Session session;

    @Override
    protected void init(Session session) {
        this.session = session;
    }

    @Function
    public DNASequence createDNASequence(String sequence) throws CompoundNotFoundException{
        return BasicSequenceFunctions.createDNASequence(sequence);
    }

    @Function
    public DNASequence createDNASequence(Path sequence) throws CompoundNotFoundException, IOException {
        return BasicSequenceFunctions.createDNASequence(Files.readString(sequence));
    }

    @Function
    public ProteinSequence getSequenceForId(String uniProtId) throws Exception{
        return BasicSequenceFunctions.getSequenceForId(uniProtId);
    }

}
