package gui.components;

import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

public class JDoubleTextField extends JTextField{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JDoubleTextField(){

		PlainDocument doc = new PlainDocument();
		doc.setDocumentFilter(new NumberFilter());
		setDocument(doc);

	}

}
