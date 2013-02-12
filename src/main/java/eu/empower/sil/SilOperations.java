package eu.empower.sil;

/**
 * Interface of commands that are available via the Roo shell.
 *
 * @since 1.1.1
 */
public interface SilOperations {

     void createProject(String projectname, String packagename, String dbtype, String dbname , String dbusername, String dbpassword, String hostname);

    void setupprerequisites();

}//EoInterface