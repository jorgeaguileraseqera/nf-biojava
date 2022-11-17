package nextflow.biojava.functions;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.util.InputStreamProvider;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * An implementation of multiple sequence functions
 *
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class RNAFunctions {

    public static RNASequence createSequence(String sequence) throws CompoundNotFoundException {
        RNASequence seq = new RNASequence(sequence);
        return seq;
    }

    public static LinkedHashMap<String, RNASequence> readFastaSequence(Path path) throws Exception {
        InputStreamProvider isp = new InputStreamProvider();
        InputStream inStream = isp.getInputStream(path.toUri().toURL());
        return FastaReaderHelper.readFastaRNASequence(inStream);
    }

}
