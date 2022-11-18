package nextflow.biojava;

import java.util.Map;

/**
 * @author : jorge <jorge.aguilera@seqera.io>
 */
public class BioConfig {

    private String proteineRepoURL;

    public String getProteineRepoURL() {
        return proteineRepoURL;
    }

    protected static BioConfig fromMap(Map config){
        final BioConfig ret = new BioConfig();
        final Map biojava = (Map)config.getOrDefault("biojava", Map.of());
        ret.proteineRepoURL = biojava.getOrDefault("proteineRepoURL","https://www.uniprot.org/uniprot/%s.fasta").toString();
        return ret;
    }

}
