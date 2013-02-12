package eu.silo.sil;

/**
 * Created with IntelliJ IDEA.
 * User: pgouvas
 * Date: 1/4/13
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Resources {
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:jaxws=\"http://cxf.apache.org/jaxws\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://cxf.apache.org/jaxws http://cxf.apache.org/schemas/jaxws.xsd\">\n" +
            "\n" +
            "  <import resource=\"classpath:META-INF/cxf/cxf.xml\"/>\n" +
            "  <import resource=\"classpath:META-INF/cxf/cxf-extension-soap.xml\"/>\n" +
            "  <import resource=\"classpath:META-INF/cxf/cxf-servlet.xml\"/>\n\n";


    public static final String DB_MYSQL  = "MySQL";
    public static final String DB_MSSQL  = "MSSQL";
    public static final String DB_ORACLE = "Oracle";


    public static final String BANNER = " _______  _______  _______  _______           _______  _______ \n" +
            "(  ____ \\(       )(  ____ )(  ___  )|\\     /|(  ____ \\(  ____ )\n" +
            "| (    \\/| () () || (    )|| (   ) || )   ( || (    \\/| (    )|\n" +
            "| (__    | || || || (____)|| |   | || | _ | || (__    | (____)|\n" +
            "|  __)   | |(_)| ||  _____)| |   | || |( )| ||  __)   |     __)\n" +
            "| (      | |   | || (      | |   | || || || || (      | (\\ (   \n" +
            "| (____/\\| )   ( || )      | (___) || () () || (____/\\| ) \\ \\__\n" +
            "(_______/|/     \\||/       (_______)(_______)(_______/|/   \\__/\n" +
            "                                                               \n" +
            " _______ _________ _                   __       _______ \n" +
            "(  ____ \\\\__   __/( \\        |\\     /|/  \\     (  __   )\n" +
            "| (    \\/   ) (   | (        | )   ( |\\/) )    | (  )  |\n" +
            "| (_____    | |   | |        | |   | |  | |    | | /   |\n" +
            "(_____  )   | |   | |        ( (   ) )  | |    | (/ /) |\n" +
            "      ) |   | |   | |         \\ \\_/ /   | |    |   / | |\n" +
            "/\\____) |___) (___| (____/\\    \\   /  __) (_ _ |  (__) |\n" +
            "\\_______)\\_______/(_______/     \\_/   \\____/(_)(_______)\n" +
            "                                                        ";

}
