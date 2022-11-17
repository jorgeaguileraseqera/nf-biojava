package nextflow.biojava.functions;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
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
public class DNAFunctions {

    public static DNASequence createSequence(String sequence) throws CompoundNotFoundException {
        DNASequence seq = new DNASequence(sequence);
        return seq;
    }

    public static LinkedHashMap<String, DNASequence> readFastaSequence(Path path) throws Exception {
        InputStreamProvider isp = new InputStreamProvider();
        InputStream inStream = isp.getInputStream(path.toUri().toURL());
        return FastaReaderHelper.readFastaDNASequence(inStream);
    }


}
