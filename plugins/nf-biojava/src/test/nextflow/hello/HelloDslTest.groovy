package nextflow.hello

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

import java.nio.file.Files
import java.nio.file.Path


/**
 * @author : jorge <jorge.aguilera@seqera.io>
 *
 */
@Timeout(10)
class HelloDslTest extends Dsl2Spec{

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

    def 'should create a sequence' () {
        when:
        def SCRIPT = '''
            include {createDNASequence} from 'plugin/nf-biojava'
            createDNASequence('GTAC') 
            '''
        and:
        DNASequence result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.getDNAType() == DNASequence.DNAType.UNKNOWN
    }

    def 'should create a sequence from a file' () {
        given:
        def file = Files.createTempFile("",".dna")
        file.text = "GTAC"*200
        when:
        def SCRIPT = """
            include {createDNASequence} from 'plugin/nf-biojava'
            createDNASequence( Path.of('${file.toAbsolutePath()}') ) 
            """
        and:
        DNASequence result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.getDNAType() == DNASequence.DNAType.UNKNOWN
    }

    def 'should get a sequence from a url' () {
        when:
        def SCRIPT = """
            include {getSequenceForId} from 'plugin/nf-biojava'
            getSequenceForId( 'Q21691' ) 
            """
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        println result.properties
        then:
        result.sequenceAsString.startsWith('MDLLDKVMGEMGSKPGSTAKKPATSASSTPRTNVWGTAKKPSSQQQPPKPLFTTP')
    }
}
