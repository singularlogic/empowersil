package eu.empower.sil;

import eu.empower.sil.SilOperations;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.LogicalPath;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.Shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Implementation of {@link eu.empower.sil.SilOperations} interface.
 *
 * @since 1.1.1
 */
@Component
@Service
public class SilOperationsImpl implements SilOperations {

    /**
     * Get hold of a JDK Logger
     */
    private Logger log = Logger.getLogger(getClass().getName());

    private static final char SEPARATOR = File.separatorChar;

    /**
     * Get a reference to the FileManager from the underlying OSGi container. Make sure you
     * are referencing the Roo bundle which contains this service in your add-on pom.xml.
     * 
     * Using the Roo file manager instead if java.io.File gives you automatic rollback in case
     * an Exception is thrown.
     */
    @Reference private FileManager fileManager;
    
    /**
     * Get a reference to the ProjectOperations from the underlying OSGi container. Make sure you
     * are referencing the Roo bundle which contains this service in your add-on pom.xml.
     */
    @Reference private ProjectOperations projectOperations;
    @Reference private Shell rooshell;
    @Reference private PathResolver pathresolver;


    public void setupprerequisites(){
        boolean success = rooshell.executeCommand("download accept terms of use");
        rooshell.executeCommand("pgp trust --keyId 0xC5FC814B");
        rooshell.executeCommand("addon install bundle --bundleSymbolicName org.gvnix.service.roo.addon ");
    }

    /** {@inheritDoc} */
    public void createProject(String projectname, String packagename, String dbtype, String dbname,  String dbusername, String dbpassword, String hostname){
        String jpaprovider = "HIBERNATE";
        boolean ignorePKs = true;
        boolean createJPA = true;
        boolean setupmvc  = true;
        boolean createservices = true;
        boolean createsoap = true;
        boolean updatecxf = true;

        log.info(Resources.BANNER+"\n");

        boolean success = rooshell.executeCommand("project --topLevelPackage "+packagename+" --projectName "+projectname);
        log.info("Project " + projectname + " created!");

        log.info("Creating DBConfiguration Layer....");
        if (success) success = rooshell.executeCommand("persistence setup --provider "+jpaprovider+" --database "+dbtype+" --databaseName "+dbname+"  --userName "+dbusername+" --password "+dbpassword+" --hostName "+hostname);

        log.info("DBConfigurator created...");
        log.info("Creating Entities Layer....");
        String schemastr = dbname;
        if (dbtype.equalsIgnoreCase(Resources.DB_MSSQL)) schemastr = "dbo";
        if (success) success = rooshell.executeCommand("database reverse engineer --schema "+schemastr+" --package ~.domain");
        log.info("Entities created....");


        LogicalPath logicalpath = LogicalPath.getInstance(Path.SRC_MAIN_JAVA,"");
        String resolvedlogical = pathresolver.getRoot(logicalpath);
        //log.info("domainlogical: "+domainlogical);
        String domainpath = resolvedlogical+SEPARATOR+packagename.replace('.',SEPARATOR)+SEPARATOR+"domain"+SEPARATOR;
        //log.info("Domain separator path: "+domainpath);
        String match = domainpath+"*.java";
        //log.info("AntMatchingPath: "+match);
        Set<FileDetails> files = fileManager.findMatchingAntPath( match );
        log.info("Domain Classes: "+files.size());
        ArrayList<String> entities = new ArrayList<String>();
        Iterator itr = files.iterator();
        while (itr.hasNext()) {
            FileDetails next = (FileDetails) itr.next();
            String entityname = next.getFile().getName().substring(0,next.getFile().getName().indexOf("."));
            if (ignorePKs){
                if (!entityname.endsWith("PK")){
                    entities.add(entityname);
                    log.info(entityname);
                } else log.info(entityname+" IGNORED....");
            } else { //do not ignore PKs
                entities.add(entityname);
                log.info(entityname);
            }
        }//while

        if (createJPA && success){
            log.info("Setup JPA layer");
            for (int i = 0; i < entities.size(); i++) {
                String entityname = entities.get(i);
                if (success) success = rooshell.executeCommand("repository jpa --entity ~.domain."+entityname+" --interface ~.repository."+entityname+"Repository");
            } //for
            log.info("JPA layer finished");
        }

        if (setupmvc && success){
            log.info("Setting up MVC artifacts ...");
            if (success) success = rooshell.executeCommand("web mvc setup");
            if (success) success = rooshell.executeCommand("web mvc all --package ~.web ");
            log.info("MVC artifacts created");
        }

        if (createservices && success){
            log.info("Creating services...");
            for (int i = 0; i < entities.size(); i++) {
                String entityname = entities.get(i);
                if (success) rooshell.executeCommand("service --interface ~.service."+entityname+"Service --entity ~.domain."+entityname);
            } //for
            log.info("Services created");
        }

        if ( createsoap && success) {
        log.info("Creating Web Services...");
            for (int i = 0; i < entities.size(); i++) {
                String entityname = entities.get(i);
                // 1 - we must leave one method
                // 2 - Problem in
                //if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method countAll"+entityname+"s   ");
                if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method delete"+entityname+"      ");
                if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method find"+entityname  +"      ");
                //if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method findAll"+entityname+"s    ");
                //we have to leave ONE
                //if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method find"+entityname+"Entries ");
                if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method save"+entityname+"        ");
                if (success) success = rooshell.executeCommand("remote service export operation --class ~.service."+entityname+"ServiceImpl --method update"+entityname+"      ");
            } //for
            log.info("Web Services created");
        }

        if (updatecxf && success){
            log.info("Cleaning up cxf ...");
            LogicalPath webapplogicalpath = LogicalPath.getInstance(Path.SRC_MAIN_WEBAPP,"");
            String resolvedwebapplogical = pathresolver.getRoot(webapplogicalpath);
            String fileName = "cxf-"+projectname+".xml";
            String targetFile = resolvedwebapplogical+SEPARATOR+"WEB-INF"+SEPARATOR+fileName;
            log.info("cxfpath: "+targetFile);
            //we have to stop the nx service here

            try {
                File mutableFile = new File(targetFile);
                FileOutputStream fos = new FileOutputStream(mutableFile);

                fos.write(Resources.XML_HEADER.getBytes());
                for (int i = 0; i < entities.size(); i++) {
                    String entityname = entities.get(i);
                    //make lower case
                    StringBuilder result = new StringBuilder(entityname.length());
                    result.append(Character.toLowerCase(entityname.charAt(0))).append(entityname.substring(1));
                    String inject="<jaxws:endpoint address=\"/"+entityname+"ServiceImpl\" id=\""+entityname+"ServiceImpl\" implementor=\"#"+result+"ServiceImpl\"/> \n";
                    fos.write(inject.getBytes());
                }
                fos.write("</beans>\n".getBytes());
                fos.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {

            }
            log.info("Finished cleaning up");
        }//success


        log.info("Enjoy...  mvn clean install tomcat:run    remember (export MAVEN_OPTS=\"-Xmx512m -XX:MaxPermSize=256m\") ");

    }//EoM createProject


}//EoClass