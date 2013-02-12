package eu.empower.sil;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.shell.converters.StaticFieldConverter;

import java.util.logging.Logger;

/**
 * Example of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colors on 
 * non-Windows systems).
 * 
 * @since 1.1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class SilCommands implements CommandMarker { // All command types must implement the CommandMarker interface
    
    /**
     * Get hold of a JDK Logger
     */
    private Logger log = Logger.getLogger(getClass().getName());

    /**
     * Get a reference to the SilOperations from the underlying OSGi container
     */
    @Reference private SilOperations operations;
    
    /**
     * Get a reference to the StaticFieldConverter from the underlying OSGi container;
     * this is useful for 'type save' command tab completions in the Roo shell
     */
    @Reference private StaticFieldConverter staticFieldConverter;

    /**
     * The activate method for this OSGi component, this will be called by the OSGi container upon bundle activation 
     * (result of the 'addon install' command) 
     * 
     * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
     */
    protected void activate(ComponentContext context) {
        staticFieldConverter.add(SilDatabasePropertyName.class);
    }

    /**
     * The deactivate method for this OSGi component, this will be called by the OSGi container upon bundle deactivation 
     * (result of the 'addon remove' command) 
     * 
     * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
     */
    protected void deactivate(ComponentContext context) {
        staticFieldConverter.remove(SilDatabasePropertyName.class);
    }
    
    // *************************************************************************
    //    Example 1 Printing colored messages to the shell
    // *************************************************************************
    
    /**
     * This method is optional. It allows automatic command hiding in situations when the command should not be visible.
     * For example the 'entity' command will not be made available before the user has defined his persistence settings 
     * in the Roo shell or directly in the project.
     * 
     * You can define multiple methods annotated with {@link CliAvailabilityIndicator} if your commands have differing
     * visibility requirements.
     * 
     * @return true (default) if the command should be visible at this stage, false otherwise
     */
    @CliAvailabilityIndicator("sil createproject")
    public boolean isCreateProjectAvailable() {
        return true; // This command is always available!
    }
    
    /**
     * This method creates a project including DAO Service layer etc...
     * 
     * @param projectname
     * @param packagename
     */
    @CliCommand(value = "sil createproject", help = "Creates a new SIL project")
    public void sayHello(
        @CliOption(key = "name", mandatory = true,    help = "The name of the project") String projectname  , // A mandatory command attribute
        @CliOption(key = "package", mandatory = true, help = "The package name") String packagename         ,
        @CliOption(key = "dbtype", mandatory = true,  help = "The Database Type" , optionContext = "MySQL,MSSQL,Oracle") String dbtype             ,
        @CliOption(key = "dbname", mandatory = true,  help = "The Database Name") String dbname             ,
        @CliOption(key = "dbusername", mandatory = true,  help = "The Database Username") String dbusername ,
        @CliOption(key = "dbpassword", mandatory = true,  help = "The Database Password") String dbpassword ,
        @CliOption(key = "hostname", mandatory = false,  help = "The Database Hostname", unspecifiedDefaultValue = "127.0.0.1") String hostname

    ) {
        
        log.info("Project skeleton for " + projectname + " will be created!");
        operations.createProject(projectname, packagename, dbtype , dbname, dbusername, dbpassword, hostname );
    }


    @CliCommand(value = "sil installprerequisites", help = "Will install libs for MySQL, Oracle, MSSQL, Gvnix etc")
    public void sayHello( ) {
        log.info("Project prerequisites will be installed!");
        operations.setupprerequisites();
    }

    @CliCommand(value = "sil stop", help = "Will stop GVNix")
    public void stopGvnix( ) {
        log.info("Stopping GVNix");
        //operations.setupprerequisites();
    }


} //EndOfClass