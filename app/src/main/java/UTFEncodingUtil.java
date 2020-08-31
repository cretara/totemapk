package com.lviiier.totem;
import java.io.UnsupportedEncodingException;


/**
 *
 * JFreeReport : a free Java reporting library
 *
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * UTFEncodingUtil.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */



    /**
     * Provides a method to encode any string into a URL-safe form. Non-ASCII characters are first encoded as sequences of
     * two or three bytes, using the UTF-8 algorithm, before being encoded as %HH escapes.
     * <p/>
     * Code is the public example given at http://www.w3.org/International/O-URL-code.html
     *
     * @author Bert Bos
     */
    public class UTFEncodingUtil
    {
        /**
         * Private Constructor prevents Object Creation.
         */
        private UTFEncodingUtil()
        {
        }

        /**
         * A lookup table.
         */
        private static final String[] hex = {
                "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
                "%08", "%09", "%0A", "%0B", "%0C", "%0D", "%0E", "%0F",
                "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
                "%18", "%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F",
                "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
                "%28", "%29", "%2A", "%2B", "%2C", "%2D", "%2E", "%2F",
                "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
                "%38", "%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F",
                "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
                "%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F",
                "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
                "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F",
                "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
                "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F",
                "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
                "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F",
                "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
                "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F",
                "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
                "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F",
                "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7",
                "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF",
                "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7",
                "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF",
                "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7",
                "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF",
                "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7",
                "%D8", "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF",
                "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7",
                "%E8", "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF",
                "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7",
                "%F8", "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF"
        };

        /**
         * Encode a string according to RFC 1738.
         * <p/>
         * <quote> "...Only alphanumerics [0-9a-zA-Z], the special characters "$-_.+!*'()," [not including the quotes - ed],
         * and reserved characters used for their reserved purposes may be used unencoded within a URL."</quote>
         * <p/>
         * <ul> <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same.
         * <p/>
         * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
         * <p/>
         * <li><p>All other ASCII characters are converted into the 3-character string "%xy", where xy is the two-digit
         * hexadecimal representation of the character code
         * <p/>
         * <li><p>All non-ASCII characters are encoded in two steps: first to a sequence of 2 or 3 bytes, using the UTF-8
         * algorithm; secondly each of these bytes is encoded as "%xx". </ul>
         *
         * @param s The string to be encoded
         * @return The encoded string
         */
        public static String encodeUTF8(final String s)
        {
            final StringBuffer sbuf = new StringBuffer();
            final char[] sChars = s.toCharArray();
            final int len = sChars.length;
            for (int i = 0; i < len; i++)
            {
                final int ch = sChars[i];
                if ('A' <= ch && ch <= 'Z')
                {    // 'A'..'Z'
                    sbuf.append((char) ch);
                }
                else if ('a' <= ch && ch <= 'z')
                {  // 'a'..'z'
                    sbuf.append((char) ch);
                }
                else if ('0' <= ch && ch <= '9')
                {  // '0'..'9'
                    sbuf.append((char) ch);
                }
                else if (ch == '-' || ch == '_'    // unreserved
                        || ch == '.' || ch == '!'
                        || ch == '~' || ch == '*'
                        || ch == '\'' || ch == '('
                        || ch == ')')
                {
                    sbuf.append((char) ch);
                }
                else if (ch <= 0x007f)
                {    // other ASCII
                    sbuf.append(hex[ch]);
                }
                else if (ch <= 0x07FF)
                {    // non-ASCII <= 0x7FF
                    sbuf.append(hex[0xc0 | (ch >> 6)]);
                    sbuf.append(hex[0x80 | (ch & 0x3F)]);
                }
                else
                {          // 0x7FF < ch <= 0xFFFF
                    sbuf.append(hex[0xe0 | (ch >> 12)]);
                    sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
                    sbuf.append(hex[0x80 | (ch & 0x3F)]);
                }
            }
            return sbuf.toString();
        }

        /**
         * Encodes a byte-array. The array is expected to contain ASCII characters, or the result may not be valid.
         *
         * @param s the byte array
         * @return the array as encoded string.
         */
        private static String encodeBytes(final byte[] s)
        {
            final StringBuffer sbuf = new StringBuffer();
            final int len = s.length;
            for (int i = 0; i < len; i++)
            {
                final int ch = (s[i] & 0xff);
                if ('A' <= ch && ch <= 'Z')
                {    // 'A'..'Z'
                    sbuf.append((char) ch);
                }
                else if ('a' <= ch && ch <= 'z')
                {  // 'a'..'z'
                    sbuf.append((char) ch);
                }
                else if ('0' <= ch && ch <= '9')
                {  // '0'..'9'
                    sbuf.append((char) ch);
                }
                else if (ch == '-' || ch == '_'    // unreserved
                        || ch == '.' || ch == '!'
                        || ch == '~' || ch == '*'
                        || ch == '\'' || ch == '('
                        || ch == ')')
                {
                    sbuf.append((char) ch);
                }
                else
                {    // other ASCII
                    sbuf.append(hex[ch]);
                }
            }
            return sbuf.toString();
        }

        /**
         * Encodes thh given string using the provided encoding. The encoding must be a valid Java-encoding.
         *
         * @param s        the string that should be encoded.
         * @param encoding the encoding to tranform the string into bytes.
         * @return the encoded string.
         * @throws UnsupportedEncodingException if the specified encoding is not recognized.
         */
        public static String encode(final String s, final String encoding)
                throws UnsupportedEncodingException
        {
            if ("utf-8".equalsIgnoreCase(encoding))
            {
                return encodeUTF8(s);
            }

            return encodeBytes(s.getBytes(encoding));
        }



        /**
         * Decodes the given string using the provided encoding. The encoding must be a valid
         * Java-encoding.
         *
         * @param s        the string that should be encoded.
         * @param encoding the encoding to tranform the bytes into a string.
         * @return the encoded string.
         * @throws UnsupportedEncodingException if the specified encoding is not recognized.
         */
        public static String decode(final String s, final String encoding)
                throws UnsupportedEncodingException
        {
            if ("utf-8".equalsIgnoreCase(encoding))
            {
                return decodeUTF(s);
            }
            // the resulting string will never be greater than the encoded string
            final byte[] result = new byte[s.length()];
            final char[] chars = s.toCharArray();
            int position = 0;

            for (int i = 0; i < chars.length; i++)
            {
                final char ch = chars[i];
                final int b;
                switch (ch)
                {
                    case'%':
                        final char lch = s.charAt(++i);
                        final int hb = (Character.isDigit(lch)
                                ? lch - '0'
                                : 10 + Character.toLowerCase(lch) - 'a') & 0xF;
                        final char hch = s.charAt(++i);
                        final int lb = (Character.isDigit(hch)
                                ? hch - '0'
                                : 10 + Character.toLowerCase(hch) - 'a') & 0xF;
                        b = (hb << 4) | lb;
                        break;
                    case'+':
                        b = ' ';
                        break;
                    default:
                        b = ch;
                }
                result[position] = (byte) b;
                position += 1;
            }
            return new String(result, 0, position, encoding);
        }

        /**
         * Decodes the given string using the encoding UTF-8.
         *
         * @param s        the string that should be encoded.
         * @return the encoded string.
         */
        public static String decodeUTF(final String s)
        {
            final StringBuffer sbuf = new StringBuffer();
            final char[] chars = s.toCharArray();
            final int l = chars.length;
            int sumb = 0;
            for (int i = 0, more = -1; i < l; i++)
            {
      /* Get next byte b from URL segment s */
                final int ch = chars[i];
                final int b;
                switch (ch)
                {
                    case'%':
                        final char lch = s.charAt(++i);
                        final int hb = (Character.isDigit(lch)
                                ? lch - '0'
                                : 10 + Character.toLowerCase(lch) - 'a') & 0xF;
                        final char hch = s.charAt(++i);
                        final int lb = (Character.isDigit(hch)
                                ? hch - '0'
                                : 10 + Character.toLowerCase(hch) - 'a') & 0xF;
                        b = (hb << 4) | lb;
                        break;
                    case'+':
                        b = ' ';
                        break;
                    default:
                        b = ch;
                }
      /* Decode byte b as UTF-8, sumb collects incomplete chars */
                if ((b & 0xc0) == 0x80)
                {      // 10xxxxxx (continuation byte)
                    sumb = (sumb << 6) | (b & 0x3f);  // Add 6 bits to sumb
                    if (--more == 0)
                    {
                        sbuf.append((char) sumb); // Add char to sbuf
                    }
                }
                else if ((b & 0x80) == 0x00)
                {    // 0xxxxxxx (yields 7 bits)
                    sbuf.append((char) b);      // Store in sbuf
                }
                else if ((b & 0xe0) == 0xc0)
                {    // 110xxxxx (yields 5 bits)
                    sumb = b & 0x1f;
                    more = 1;        // Expect 1 more byte
                }
                else if ((b & 0xf0) == 0xe0)
                {    // 1110xxxx (yields 4 bits)
                    sumb = b & 0x0f;
                    more = 2;        // Expect 2 more bytes
                }
                else if ((b & 0xf8) == 0xf0)
                {    // 11110xxx (yields 3 bits)
                    sumb = b & 0x07;
                    more = 3;        // Expect 3 more bytes
                }
                else if ((b & 0xfc) == 0xf8)
                {    // 111110xx (yields 2 bits)
                    sumb = b & 0x03;
                    more = 4;        // Expect 4 more bytes
                }
                else /*if ((b & 0xfe) == 0xfc)*/
                {  // 1111110x (yields 1 bit)
                    sumb = b & 0x01;
                    more = 5;        // Expect 5 more bytes
                }
      /* We don't test if the UTF-8 encoding is well-formed */
            }
            return sbuf.toString();
        }
    }



