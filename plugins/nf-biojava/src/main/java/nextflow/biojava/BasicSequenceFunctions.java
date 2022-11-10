package nextflow.biojava;

import nextflow.plugin.extension.Function;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.net.URL;

/**
 * An implementation of multiple sequence functions
 *
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class BasicSequenceFunctions {

    protected static DNASequence createDNASequence(String sequence) throws CompoundNotFoundException {
        DNASequence seq = new DNASequence(sequence);
        return seq;
    }

    protected static ProteinSequence getSequenceForId(String uniProtId) throws Exception {
        URL uniprotFasta = new URL(String.format("https://www.uniprot.org/uniprot/%s.fasta", uniProtId));
        ProteinSequence seq = FastaReaderHelper.readFastaProteinSequence(uniprotFasta.openStream()).get(uniProtId);
        return seq;
    }

}
