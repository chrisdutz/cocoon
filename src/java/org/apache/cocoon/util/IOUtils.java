/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

import org.apache.log.Hierarchy;

/**
 * A collection of <code>File</code>, <code>URL</code> and filename
 * utility methods
 *
 * @author <a href="mailto:ricardo@apache.org">Ricardo Rocha</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @version CVS $Id: IOUtils.java,v 1.2 2004/03/01 03:50:58 antonio Exp $
 */
public class IOUtils {

    // **********************
    // Serialize Methods
    // **********************

    /**
     * Dump a <code>String</code> to a text file.
     *
     * @param file The output file
     * @param string The string to be dumped
     * @exception IOException IO Error
     */
    public static void serializeString(File file, String string)
    throws IOException {
        serializeString(file, string, null);
    }

    /**
     * Dump a <code>String</code> to a text file.
     *
     * @param file The output file
     * @param string The string to be dumped
     * @param encoding The encoding for the output file or null for default platform encoding
     * @exception IOException IO Error
     */
    public static void serializeString(File file, String string, String encoding)
    throws IOException {
        final Writer fw =
                (encoding == null)?
                new FileWriter(file):
                new OutputStreamWriter(new FileOutputStream(file), encoding);
        try {
            fw.write(string);
            fw.flush();
        } finally {
            if (fw != null) fw.close();
        }
    }

    /**
     * Load a text file contents as a <code>String<code>.
     * This method does not perform enconding conversions
     *
     * @param file The input file
     * @return The file contents as a <code>String</code>
     * @exception IOException IO Error
     */
    public static String deserializeString(File file)
    throws IOException {
        int len;
        char[] chr = new char[4096];
        final StringBuffer buffer = new StringBuffer();
        final FileReader reader = new FileReader(file);
        try {
            while ((len = reader.read(chr)) > 0) {
                buffer.append(chr, 0, len);
            }
        } finally {
            if (reader != null) reader.close();
        }
        return buffer.toString();
    }

    /**
     * This method serializes an object to an output stream.
     *
     * @param file The output file
     * @param object The object to be serialized
     * @exception IOException IOError
     */
    public static void serializeObject(File file, Object object)
    throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(object);
            oos.flush();
        } finally {
            if (fos != null) fos.close();
        }
    }

    /**
     * This method deserializes an object from an input stream.
     *
     * @param file The input file
     * @return The deserialized object
     * @exception IOException IOError
     */
    public static Object deserializeObject(File file)
    throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        Object object = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
            object = ois.readObject();
        } finally {
            if (fis != null) fis.close();
        }
        return object;
    }

  /**
   * These are java keywords as specified at the following URL (sorted alphabetically).
   * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
   */
  static final String keywords[] =
  {
      "abstract",  "boolean",     "break",    "byte",         "case",
      "catch",     "char",        "class",    "const",        "continue",
      "default",   "do",          "double",   "else",         "extends",
      "final",     "finally",     "float",    "for",          "goto",
      "if",        "implements",  "import",   "instanceof",   "int",
      "interface", "long",        "native",   "new",          "package",
      "private",   "protected",   "public",   "return",       "short",
      "static",    "strictfp",    "super",    "switch",       "synchronized",
      "this",      "throw",       "throws",   "transient",    "try",
      "void",      "volatile",    "while"
  };

  /** Collator for comparing the strings */
  static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);

  /** Use this character as suffix */
  static final char keywordSuffix = '_';

  /**
   * checks if the input string is a valid java keyword.
   * @return boolean true/false
   */
  private static boolean isJavaKeyword(String keyword) {
    return (Arrays.binarySearch(keywords, keyword, englishCollator) >= 0);
  }

  // **********************
  // File Methods
  // **********************

  /**
   * Return a modified filename suitable for replicating directory
   * structures below the store's base directory. The following
   * conversions are performed:
   * <ul>
   * <li>Path separators are converted to regular directory names</li>
   * <li>File path components are transliterated to make them valid (?)
   *     programming language identifiers. This transformation may well
   *     generate collisions for unusual filenames.</li>
   * </ul>
   * @return The transformed filename
   */
  public static String normalizedFilename(String filename) {
    if(File.separatorChar == '\\')
        filename = filename.replace('/','\\');
    else
        filename = filename.replace('\\','/');
    String[] path = StringUtils.split(filename, File.separator);
    int start = (path[0].length() == 0) ? 1 : 0;

    StringBuffer buffer = new StringBuffer();
    for (int i = start; i < path.length; i++) {

      if (i > start) {
        buffer.append(File.separator);
      }

      if(path[i].equals("..")) {
        int lio = buffer.length()-2;
        while (lio>=0) {
          if (buffer.substring(lio).startsWith(File.separator))
            break;

          lio--;
        }

        if (lio>=0)
          buffer.setLength(lio);

      } else {
        char[] chars = path[i].toCharArray();

        if (chars.length < 1 || !Character.isLetter(chars[0])) 
          buffer.append('_');

        for (int j = 0; j < chars.length; j++) {
          if (StringUtils.isAlphaNumeric(chars[j])) {
            buffer.append(chars[j]);
          } else {
            buffer.append('_');
          }
        }

        // Append the suffix if necessary.
        if(isJavaKeyword(path[i]))
          buffer.append(keywordSuffix);
      }

    }
    return buffer.toString();
  }

  /**
   * Remove file information from a filename returning only its path
   * component
   *
   * @param filename The filename
   * @return The path information
   */
  public static String pathComponent(String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i > -1) ? filename.substring(0, i) : filename;
  }

  /**
   * Remove path information from a filename returning only its file
   * component
   *
   * @param filename The filename
   * @return The filename sans path information
   */
  public static String fileComponent(String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i > -1) ? filename.substring(i + 1) : filename;
  }

  /**
   * Strip a filename of its <i>last</i> extension (the portion
   * immediately following the last dot character, if any)
   *
   * @param filename The filename
   * @return The filename sans extension
   */
  public static String baseName(String filename) {
    int i = filename.lastIndexOf('.');
    return (i > -1) ? filename.substring(0, i) : filename;
  }

  /**
   * Get the complete filename corresponding to a (typically relative)
   * <code>File</code>.
   * This method accounts for the possibility of an error in getting
   * the filename's <i>canonical</i> path, returning the io/error-safe
   * <i>absolute</i> form instead
   *
   * @param file The file
   * @return The file's absolute filename
   */
  public static String getFullFilename(File file) {
    try {
      return file.getCanonicalPath();
    } catch (Exception e) {
      Hierarchy.getDefaultHierarchy().getLoggerFor("cocoon").debug("IOUtils.getFullFilename", e);
      return file.getAbsolutePath();
    }
  }

  /**
   * Return the path within a base directory
   */
  public static String getContextFilePath(String directoryPath, String filePath) {
      try
      {
          File directory = new File(directoryPath);
          File file = new File(filePath);

          directoryPath = directory.getCanonicalPath();
          filePath = file.getCanonicalPath();

          // If the context directory does not have a File.separator
          // at the end then add one explicitly
          if(!directoryPath.endsWith(File.separator)){
            directoryPath += File.separator;
          }

          // If the context dir contains both kinds of separator
          // then standardize on using the File.separator
          if ((directoryPath.indexOf('/') !=-1) && (directoryPath.indexOf('\\') !=-1)) {
            directoryPath = directoryPath.replace('\\', File.separator.charAt(0));
            directoryPath = directoryPath.replace('/', File.separator.charAt(0));
          }

          // If the file path contains both kinds of separator
          // then standardize on using the File.separator
          if ((filePath.indexOf('/') !=-1) && (filePath.indexOf('\\') !=-1)) {
            filePath = filePath.replace('\\', File.separator.charAt(0));
            filePath = filePath.replace('/', File.separator.charAt(0));
          }

          if (filePath.startsWith(directoryPath)) {
              filePath = filePath.substring(directoryPath.length());
          }
      } catch (Exception e){
          Hierarchy.getDefaultHierarchy().getLoggerFor("cocoon").debug("IOUtils.getContextFilePath", e);
      }

      return filePath;
  }

  /**
   * Return a file with the given filename creating the necessary
   * directories if not present.
   *
   * @param filename The file
   * @return The created File instance
   */
  public static File createFile(File destDir, String filename) {
    File file = new File(destDir, filename);
    File parent = file.getParentFile();
    if (parent != null) parent.mkdirs();
    return file;
  }

  /**
   * Returns a byte array from the given object.
   *
   * @param object to convert
   * @return byte array from the object
   */
  public static byte[] objectToBytes(Object object) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(baos);
    os.writeObject(object);
    return baos.toByteArray();
  }

  /**
   * Returns a object from the given byte array.
   *
   * @param bytes array to convert
   * @return object
   */
  public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    ObjectInputStream is = new ObjectInputStream(bais);
    return is.readObject();
  }
}
