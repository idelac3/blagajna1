package borg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * <B>IniSettings</B><BR> <BR> This class will allow you to read and write to
 * INI files settings for you application.<BR> <BR> It should also provide
 * compatibility to <I>Property</I> class which is limited comparing to
 * <B>IniSettings</B> class.<BR> <BR> <B>Usage</B><BR> <BR> This is small
 * example how to use it:<BR> <UL> IniSettings ini = new IniSettings(); </UL>
 * <UL> ini.read("testFile.ini"); </UL> <UL> ini.set("test1", "value1"); </UL>
 * <UL> ini.set("test2", "value2"); </UL> <UL> ini.set("test3", "value3"); </UL>
 * <UL> ini.write("testFile.ini"); </UL> <BR> <BR>
 *
 * @author eigorde
 *
 */
public class IniSettings {

    /**
     * Line by line settings loaded from file or added manually.
     */
    private List<String> settings;
    /**
     * Separator char for param/value line.<BR> Usually it is <I>=</I> char.
     */
    private char separator;

    /**
     * Default value to return if param is not found.
     */
    private String defaultValue;
    
    /**
     * Initialize empty structure.<BR> Default separator is <I>=</I> char.
     */
    public IniSettings() {
        settings = new ArrayList<String>();
        separator = '=';
    }

    /**
     * Initialize empty structure.<BR> Default separator is overriden by
     * <I>separator</I> value.
     *
     * @param separator new separator
     */
    public IniSettings(char separator) {
        settings = new ArrayList<String>();
        this.separator = separator;
    }

    /**
     * This will read settings from ini file.
     *
     * @param fileName path and file name for ini file
     */
    public void read(String fileName) throws IOException {
        settings.clear();
        BufferedReader br;

        br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            settings.add(line);
        }
        br.close();

    }

    /**
     * This will write settings to ini file.
     *
     * @param fileName path and file name for ini file
     */
    public void write(String fileName) throws FileNotFoundException {
        PrintWriter out = null;

        out = new PrintWriter(fileName);
        for (String line : settings) {
            out.println(line);
        }
        out.close();

    }

    /**
     * <B>Set default</B><BR>
     * Set default value, and if param is not found on <I>get()</I> call,
     * a default value will return.<BR>
     * If not defined, then empty string "" is default value to return.<BR>
     * @param value default value to return for <I>get()</I>
     */
    public void setDefault(String value) {
        defaultValue = value;
    }
    
    /**
     * <B>Get</B><BR> It will fetch setting value, eg. if ini file has
     * <I>param=value</I> then with: <BR> <I>get("param")</I><BR> you can get
     * <I>value</I>.<BR> <B>NOTE</B><BR> If <I>param</I> does not exist, an
     * empty string is returned.<BR> Parameters and values are case
     * sensitive.<BR> This works only in global part of ini structure before
     * first section.<BR>
     *
     * @param param is parameter name
     * @return value or empty string
     */
    public String get(String param) {
        String retVal = defaultValue;
        for (String line : settings) {
            if (line.startsWith(param + separator)) {
                retVal = line.substring(line.indexOf(separator) + 1);
                break;
            }
            if (line.startsWith("[")) {
                break;
            }
        }
        return retVal;
    }

    /**
     * <B>Set</B><BR> It will put new setting value, eg. if ini file has
     * <I>param=value</I> then with:<BR> <I>set("param", "value")</I><BR> you
     * can put <I>value</I>.<BR> <B>NOTE</B><BR> If <I>param</I> does not exist,
     * it will be added at the end.<BR> But it will happen before first
     * section.<BR> <BR> Parameters and values are case sensitive.<BR>
     *
     * @param param is parameter name
     * @param value is new value for parameter
     */
    public void set(String param, String value) {
        ListIterator<String> it = settings.listIterator();
        while (it.hasNext()) {
            String line = it.next();
            if (line.startsWith(param + separator)) {
                it.set(param + separator + value);
                break;
            }
            if (line.startsWith("[")) {
                if (it.hasPrevious()) {
                    it.previous();
                }
                it.add(param + separator + value);
                break;
            }
        }
        if (!settings.contains(param + separator + value)) {
            settings.add(param + separator + value);
        }

    }

    /**
     * <B>Get</B><BR> It will fetch setting value, eg. if ini file has
     * <I>param=value</I> then with:<BR> <I>get("section1", "param")</I><BR> you
     * can get <I>value</I> from <I>section1</I>.<BR> <BR> <UL> [section1] </UL>
     * <UL> param=value </UL> <BR> <B>NOTE</B><BR> If <I>param</I> does not
     * exist under section <I>section</I>, an empty string is returned.<BR>
     * Parameters and values are case sensitive while section name is not.<BR>
     *
     * @param section is name of the section in ini structure. It is
     * encapsulated in square brackets <I>[</I> and <I>]</I>
     *
     * @param param is parameter name
     *
     * @return value or empty string
     */
    public String get(String section, String param) {
        String retVal = defaultValue;
        boolean found = false;
        for (String line : settings) {
            if (line.equalsIgnoreCase("[" + section + "]")) {
                found = true;
            }
            if (line.startsWith(param + separator) && found) {
                retVal = line.substring(line.indexOf(separator) + 1);
                break;
            }
        }
        return retVal;
    }

    /**
     * <B>Set</B><BR> It will put new setting value under selected section, eg.
     * if ini file has <I>param=value</I> then with:<BR> <I>set("section1",
     * "param", "value")</I><BR> you can put <I>value</I> under:<BR> <BR> <UL>
     * [section1] </UL> <UL> param=value </UL> <B>NOTE</B><BR> If <I>param</I>
     * does not exist, it will be added at the end of the section.<BR> If
     * section does not exist, it will be added as last in ini structure.<BR>
     * <BR> Parameters and values are case sensitive.<BR>
     *
     * @param section section name
     * @param param is parameter name
     * @param value is new value for parameter
     */
    public void set(String section, String param, String value) {
        boolean found = false;
        ListIterator<String> it = settings.listIterator();
        if (settings.contains("[" + section + "]")) {
            while (it.hasNext()) {
                String line = it.next();
                if (line.startsWith("[") && found) {
                    if (it.hasPrevious()) {
                        it.previous();
                    }
                    it.add(param + separator + value);
                    break;
                }
                if (line.startsWith("[" + section + "]")) {
                    found = true;
                }
                if (line.startsWith(param + separator) && found) {
                    it.set(param + separator + value);
                    break;
                }
                if (!it.hasNext() && found) {
                    it.add(param + separator + value);
                }
            }

        } else {
            settings.add("[" + section + "]");
            settings.add(param + separator + value);
        }

    }

    /**
     * <B>Remove</B><BR> It will remove setting value under selected section,
     * eg. if ini file has <I>param=value</I> then with:<BR>
     * <I>remove("section1", "param")</I><BR> you can remove <I>param=value</I>
     * under <I> [section1] </I>.<BR> <B>NOTE</B><BR> If <I>param</I> does not
     * exist or section is not found, nothing will happen.<BR> <BR> Parameters
     * and values are case sensitive.<BR>
     *
     * @param section section name
     * @param param is parameter name
     */
    public void remove(String section, String param) {

        boolean found = false;
        ListIterator<String> it = settings.listIterator();
        if (settings.contains("[" + section + "]")) {
            while (it.hasNext()) {
                String line = it.next();

                if (line.startsWith("[" + section + "]")) {
                    found = true;
                }
                if (line.startsWith(param + separator) && found) {
                    it.remove();
                    break;
                }

            }

        }
    }

    /**
     * <B>Clean</B><BR> It will erase completely ini structure. This is more
     * efficient than making new instance of class.<BR> After cleaning, you
     * should populate it according to application settings specification.<BR>
     */
    public void clean() {
        settings.clear();
    }

    /**
     * Dump the entire ini structure into string.<BR> This could be useful to
     * debug this class.<BR>
     *
     * @return ini structure or empty string
     */
    public String dump() {
        String retVal = "";
        for (String line : settings) {
            retVal = retVal + line + "\n";
        }
        return retVal;
    }
}
