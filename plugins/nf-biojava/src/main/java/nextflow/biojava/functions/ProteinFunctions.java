package nextflow.biojava.functions;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.util.InputStreamProvider;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * An implementation of multiple sequence functions
 *
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class ProteinFunctions {

    public static ProteinSequence getSequenceForId(String uniProtId) throws Exception {
        URL uniprotFasta = new URL(String.format("https://www.uniprot.org/uniprot/%s.fasta", uniProtId));
        ProteinSequence seq = FastaReaderHelper.readFastaProteinSequence(uniprotFasta.openStream()).get(uniProtId);
        return seq;
    }

    public static LinkedHashMap<String, ProteinSequence> readFastaSequence(Path path) throws Exception {
        InputStreamProvider isp = new InputStreamProvider();
        InputStream inStream = isp.getInputStream(path.toUri().toURL());
        return FastaReaderHelper.readFastaProteinSequence(inStream);
    }
}
