package org.basex.test.cs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.basex.core.Main;

/**
 * Checks all project interfaces for potentially obsolete texts.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class ObsoleteTexts {
  /** Constants matcher. */
  private static final Pattern CONSTANTS =
    Pattern.compile("\\b([A-Z][A-Z0-9_]+)\\b");
  /** Classes to test. */
  private static final Class<?>[] CLASSES = {
    org.basex.core.Text.class,       org.basex.build.BuildText.class,
    org.basex.data.DataText.class,   org.basex.api.dom.BXText.class,
    org.basex.query.QueryText.class, org.basex.query.QueryTokens.class,
    org.basex.build.mediovis.MAB2.class, org.basex.gui.GUIProp.class,
    org.basex.core.Prop.class,
  };

  /** Private constructor, preventing instantiation. */
  private ObsoleteTexts() { }

  /**
   * Test method.
   * @param args ignored command-line arguments
   * @throws Exception exception
   */
  public static void main(final String[] args) throws Exception {
    final HashSet<String> set = new HashSet<String>();
    read(new File("src"), set);

    for(final Class<?> c : CLASSES) {
      int i = 0;
      for(final Field f : c.getDeclaredFields()) {
        final String name = f.getName();
        if(!set.contains(name)) {
          if(i == 0) Main.outln(c.getSimpleName() + ".java");
          if(++i % 8 == 0) Main.out("\n");
          Main.out(name + " ");
        }
      }
      if(i != 0) Main.outln("\n");
    }
  }

  /**
   * Parses all java classes.
   * @param file file reference
   * @param set hash set, containing all string constants
   * @throws Exception exception
   */
  static void read(final File file, final HashSet<String> set)
      throws Exception {

    for(final File f : file.listFiles()) {
      String name = f.getName();
      if(f.isDirectory()) {
        read(f, set);
      } else if(name.endsWith(".java")) {
        name = name.replaceAll("\\.java", "");
        boolean x = false;
        for(final Class<?> c : CLASSES) x = x || c.getSimpleName().equals(name);
        if(x) continue;

        final BufferedReader br = new BufferedReader(new FileReader(f));
        while(true) {
          final String l = br.readLine();
          if(l == null) break;
          final Matcher m = CONSTANTS.matcher(l);
          while(m.find()) set.add(m.group(1));
        }
        br.close();
      }
    }
  }
}