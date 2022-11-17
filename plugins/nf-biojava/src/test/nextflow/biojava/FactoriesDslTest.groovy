package nextflow.biojava

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.biojava.nbio.core.sequence.DNASequence
import org.pf4j.PluginDescriptorFinder
import spock.lang.Shared
import spock.lang.Timeout
import test.Dsl2Spec
import test.MockScriptRunner

import java.nio.file.Path

/**
 * @author : jorge <jorge.aguilera@seqera.io>
 *
 */
@Timeout(90)
class FactoriesDslTest extends Dsl2Spec{

    @Shared String pluginsMode

    def setup() {
        // reset previous instances
        PluginExtensionProvider.reset()
        // this need to be set *before* the plugin manager class is created
        pluginsMode = System.getProperty('pf4j.mode')
        System.setProperty('pf4j.mode', 'dev')
        // the plugin root should
        def root = Path.of('.').toAbsolutePath().normalize()
        def manager = new TestPluginManager(root){
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new TestPluginDescriptorFinder(){
                    @Override
                    protected Path getManifestPath(Path pluginPath) {
                        return pluginPath.resolve('build/resources/main/META-INF/MANIFEST.MF')
                    }
                }
            }
        }
        Plugins.init(root, 'dev', manager)
    }

    def cleanup() {
        Plugins.stop()
        PluginExtensionProvider.reset()
        pluginsMode ? System.setProperty('pf4j.mode',pluginsMode) : System.clearProperty('pf4j.mode')
    }

    def 'should emit a dna fasta' () {
        when:
        def SCRIPT = '''
            include {fromDNAFasta} from 'plugin/nf-biojava'
            
            channel
                .fromDNAFasta( file('https://raw.githubusercontent.com/nf-core/test-datasets/rnaseq/reference/genome.fasta') )                
        '''
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()

        then:
        def val = result.val
        val[0]=="I"
        val[1].getDNAType() == DNASequence.DNAType.UNKNOWN
        result.val == Channel.STOP
    }

    def 'should process a gz fasta' () {
        when:
        def SCRIPT = '''
            include {fromProteinFasta} from 'plugin/nf-biojava'
            
            process echo{
                input: tuple val(key), val(v)
                output: stdout
                script:
                """
                echo key = ${key}
                echo dna = ${v.toString()[0..20]}             
                """        
            }
            workflow{
                channel
                    .fromProteinFasta(file('https://raw.githubusercontent.com/nf-core/test-datasets/rnaseq/reference/genome.fasta.gz'))
                | echo
                | view
            }                
        '''
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()

        then:
        true
    }
}
