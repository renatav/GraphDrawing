package gui.components;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberFilter extends DocumentFilter {

    private NumberFormat nf = NumberFormat.getInstance();
    private char[] chars; // allowed characters
    private char groupingSeparator;
    private char decimalPoint;
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    int maxSize;
    int fractionsDigits;

    // limit is the maximum number of characters allowed.
    public NumberFilter(int limit) {
        this(limit, 0);
        chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }

    public NumberFilter(int limit, int fractionsDigits) {
        this.fractionsDigits = fractionsDigits;
        maxSize = limit + (limit / 3 - 1); // add number of group separators
        nf.setMaximumFractionDigits(fractionsDigits);
        groupingSeparator = dfs.getGroupingSeparator();
        decimalPoint = dfs.getDecimalSeparator();
        chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', decimalPoint};
    }

    public NumberFilter() {
        this(100,100);
    }

    // This method is called when characters are inserted into the document
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String str,
                             AttributeSet attr) throws BadLocationException {
        System.out.println("insert");
        int len = fb.getDocument().getLength();
        replace(fb, 0, len, str, attr);    
    }



    // This method is called when characters in the document are replace with other characters
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                        String str, AttributeSet attrs) throws BadLocationException {
        if (!verify(str)) 
        	return;
        int len = fb.getDocument().getLength();
        int newLength = len - length + str.length();

        String s = fb.getDocument().getText(0, len);
        String toBeReplaced = s.substring(offset, len);
        s = s.replace(toBeReplaced, "");
        
        int i = s.indexOf(decimalPoint);
        boolean nDigits = false;

        if (i != -1) {
            // do not allow two decimal points
            if (str.equals(String.valueOf(decimalPoint))) {
                return;
            }
            // we have two fraction digits
            if ((i + fractionsDigits + 1) == s.length()) {
                nDigits = true;
            }
        }

        String text = s + str;
        
        if (!nDigits) {
            try {
                Double.parseDouble(getWithoutSeparator(text));
            } catch (NumberFormatException nfe) {
                // can appear if we enter multiple points!
                return;
            }
        } else {
            try {
                Double.parseDouble(getWithoutSeparator(s));
            } catch (NumberFormatException nfe) {
                // can appear if we enter multiple points!
                return;
            }
        }

        int index = str.indexOf(decimalPoint);
        boolean withFractionDigits = false;
        if (index != -1) {
            withFractionDigits = true;
        }

        // for fraction digits we allow another (fractionDigits+1) characters
        int totalSize = maxSize;
        if (withFractionDigits) {
            totalSize += (fractionsDigits + 1);
        }
        

        if (newLength <= totalSize) {
            // if we have fractionDigits digits after decimal point we do not allow to enter other digits
            // without this code, if we enter a digit after fractionDigits digits, the number will be rounded!
            if (withFractionDigits) {
                if (nDigits && (index + fractionsDigits + 1) == str.length()) {
                    throw new BadLocationException("New characters exceeds max size of document", offset);
                } else {
                }
            }

            fb.replace(0, len, text, attrs);
        } else {
            throw new BadLocationException("New characters exceeds max size of document", offset);
        }
    }

    private boolean verify(String s) {
        char[] sc = s.toCharArray();
        for (int i = 0, size = sc.length; i < size; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (sc[i] == chars[j]) {
                    return true;
                }
            }
        }
        return false;
    }
   
    public String getWithoutSeparator(String s) {
          StringBuffer sb = new StringBuffer();
          StringTokenizer st = new StringTokenizer(s,String.valueOf(groupingSeparator));
          while (st.hasMoreTokens()) {
              sb.append(st.nextToken());
          }
          return sb.toString();
    }
}
