<<<<<<< HEAD

=======
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9
package client;

import java.awt.BorderLayout;

<<<<<<< HEAD
import javax.swing.JEditorPane;
import javax.swing.JPanel;

public class User extends JPanel {
	private String name;
	private JEditorPane nameField;

	public User(String name) {
		this.name = name;
		nameField = new JEditorPane();
		nameField.setContentType("text/html");
		nameField.setText("<b>" + name + "</b>");
=======
import javax.swing.JPanel;
import javax.swing.JTextField;

public class User extends JPanel {
	private String name;
	private JTextField nameField;

	public User(String name) {
		this.name = name;
		nameField = new JTextField(name);
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9
		nameField.setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(nameField);
	}

	public String getName() {
		return name;
	}
}