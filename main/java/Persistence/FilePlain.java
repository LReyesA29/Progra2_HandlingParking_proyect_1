package Persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import config.Config;
import constants.CommonConstants;

/**
 * Basic file read/write utilities. Uses absolute paths.
 */
public class FilePlain {

    protected Config config;

    public FilePlain() {
        this.config = Config.getInstance();
    }

    /** Read all lines from filePath (absolute or relative). Returns empty list if file missing. */
    protected java.util.List<String> reader(String filePath) {
        java.util.List<String> out = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) out.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /** Write lines to filePath (overwrite). */
    protected void writer(String filePath, java.util.List<String> lines) {
        File f = new File(filePath);
        // create parent folder if needed
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            for (String l : lines) {
                bw.write(l);
                bw.write(CommonConstants.BREAK_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
