import groovy.transform.CompileDynamic

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * A collection of utilities to aggregate Javadocs
 */
@CompileDynamic
class JavaDocUtils {
    private static final String DOCS_DIR = 'javadoc'
    private static final String[] IGNORE_DIRS = [ 'main', 'java', 'groovy', 'html', 'src', 'buildSrc', 'resources' ]
    private static final String[] RESOURCES = [ 'index.html', 'javadoc-stylesheet.css' ]
    private static final String MODULE_PLACEHOLDER = '%modules%'
    private static final String MODULE_FORMAT_FILE = 'module-format.html'

    /**
     * Aggregates the javadoc of all the root and subprojects into a single directory
     *
     * @param output the output directory
     * @param ignoreDirs the directories (modules) to be ignore
     */
    static aggregateJavaDoc(String output, String name, String version, String... ignoreDirs) {
        def current = new File(System.getProperty('user.dir'))
        if (current.name == 'buildSrc') current = current.parentFile
        def outputDir = new File(current, output)

        if (outputDir.exists() && !outputDir.deleteDir())
            throw new IllegalStateException("Could not delete previous directory ${output}")
        if (!outputDir.mkdirs()) throw new IllegalStateException("Could not create directory ${output}")

        aggregateJavaDocRec(current, outputDir, ignoreDirs)
        generateModulesPage(name, version, outputDir)
    }

    /**
     * Copies the given file.
     *
     * @param src the source (copied) file
     * @param dst the destination (copy) file
     */
    static copyDirectory(File src, File dst) {
        if (src.directory) {
            def files = src.listFiles()
            dst.mkdirs()
            if (files != null)
                files*.name.each { copyDirectory(new File(src, it), new File(dst, it)) }
        } else Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Finds the most common path between the two given files
     *
     * @param file1 the first file
     * @param file2 the second file
     * @return the common path
     */
    static commonPath(File file1, File file2) {
        def path1 = file1.absolutePath
        def path2 = file2.absolutePath
        def result = ''

        for (i in 0..Math.min(path1.length(), path2.length())) {
            def c1 = path1[i]
            def c2 = path2[i]
            if (c1 != c2) break
            else result += c1
        }

        return result
    }

    /**
     * Gets the given resource.
     *
     * @param name the name
     * @return the resource
     */
    static getResource(String name) {
        while (name.startsWith('/')) name = name.substring(1)
        def resource = JavaDocUtils.getResourceAsStream(name)
        if (resource == null) resource = JavaDocUtils.getResourceAsStream("/${name}")
        if (resource == null) throw new IllegalArgumentException("Could not find resource '${name}'")
        return resource
    }

    private static aggregateJavaDocRec(File current, File output, String... ignoreDirs) {
        if (!current.directory) return

        def files = current.listFiles()
        if (files == null)
            throw new IllegalArgumentException("Could not list files of ${current.path}")

        files.findAll { f -> f.directory }
                .findAll { f -> !IGNORE_DIRS.any { d -> d == f.name } }
                .findAll { f -> !ignoreDirs.any { d -> d == f.name } }
                .each { f ->
                    if (f.name == DOCS_DIR) {
                        def dest = getDestinationFromModule(output, f)
                        def out = new File(output, dest)
                        copyDirectory(f, out)
                    } else aggregateJavaDocRec(f, output)
                }
    }

    private static generateModulesPage(String name, String version, File file) {
        if (!file.directory) return
        def files = file.listFiles()
        if (files == null) return
        if (files.any { it.name.contains('.html') }) return

        RESOURCES.each { parseResource(file, it, name, version, files) }

        files.each { generateModulesPage(it.name, version, it) }
    }

    private static parseResource(File parentFile, String resource, String name, String version, File[] files) {
        getResource("${resource}").withReader { reader ->
            new File(parentFile, resource).withWriter { writer ->
                String line
                while ((line = reader.readLine()) != null) {
                    line = line.replace('%module_name%', name)
                            .replace('%module_version%', version)
                    if (line.contains(MODULE_PLACEHOLDER))
                        line = parseModulesPlaceholder(line, files)
                    writer.write(line)
                    writer.write('\n')
                }
            }
        }
    }

    private static parseModulesPlaceholder(String line, File[] files) {
        String output = ''
        files*.name.each { n ->
            getResource("/${MODULE_FORMAT_FILE}").withReader { r ->
                String l
                while ((l = r.readLine()) != null)
                    output += l.replace('%submodule_name%', n)
                            .replace('%submodule_path%', "${n}${File.separator}index.html")
            }
        }
        line.replace(MODULE_PLACEHOLDER, output)
    }

    private static getDestinationFromModule(File output, File file) {
        def parent = new File(commonPath(output, file))
        def module = new File(file, '')
        while (module.parentFile.parentFile != parent)
            module = module.parentFile
        def moduleName = module.name

        def dst = "${module.parentFile.name}"
        if (moduleName != 'build') dst += "${File.separator}${module.name}"
        return dst
    }

}
