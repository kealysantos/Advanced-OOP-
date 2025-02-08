package Unknown;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ContactAppTestNG {

    private JFrame frame = new JFrame("Contact App");
    private JPanel mainPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private List<ContactApp.Contact> contacts = new ArrayList<>();
    private JTextField searchField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> groupSelector;
    private String selectedProfileImage;
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel emailLabel;
    private JLabel profilePicture;
    private JCheckBox favoriteCheckBox;

    public ContactAppTestNG() {
        mainPanel.setLayout(cardLayout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mainPanel);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
    }

    @Test
    public void testInitContactListView() {
        JPanel panel = new JPanel(new BorderLayout());
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchContacts());

        JButton addButton = new JButton("Add New Contact");
        JButton viewButton = new JButton("View Details");
        JButton deleteButton = new JButton("Delete Contact");

        addButton.addActionListener(e -> cardLayout.show(mainPanel, "CREATE"));
        viewButton.addActionListener(e -> showContactDetails());
        deleteButton.addActionListener(e -> deleteContact());

        JPanel topPanel = new JPanel();
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contactList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "LIST");
    }

    @Test
    public void testInitContactDetailsView() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        nameLabel = new JLabel();
        phoneLabel = new JLabel();
        emailLabel = new JLabel();
        profilePicture = new JLabel("No Image", SwingConstants.CENTER);
        favoriteCheckBox = new JCheckBox("Mark as Favorite");

        JButton callButton = new JButton("Call");
        JButton messageButton = new JButton("Message");
        JButton editButton = new JButton("Edit");
        JButton backButton = new JButton("Back to List");

        callButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Calling..."));
        messageButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Messaging..."));
        editButton.addActionListener(e -> editContact());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(callButton);
        buttonPanel.add(messageButton);
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        panel.add(profilePicture);
        panel.add(nameLabel);
        panel.add(phoneLabel);
        panel.add(emailLabel);
        panel.add(favoriteCheckBox);
        panel.add(buttonPanel);

        mainPanel.add(panel, "DETAILS");
    }

    @Test
    public void testInitContactCreationView() {
        JPanel panel = createInputPanel(200, 150);

        nameField = new JTextField(10);
        phoneField = new JTextField(10);
        emailField = new JTextField(10);
        groupSelector = new JComboBox<>(new String[]{"Family", "Friends", "Work"});

        JButton selectImageButton = new JButton("Select");
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        Dimension buttonSize = new Dimension(80, 25);
        selectImageButton.setPreferredSize(buttonSize);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);

        selectImageButton.addActionListener(e -> chooseProfileImage());
        saveButton.addActionListener(e -> saveContact());
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(selectImageButton);
        panel.add(saveButton);
        panel.add(cancelButton);

        mainPanel.add(panel, "CREATE");
    }
    public JPanel createInputPanel(int width, int height) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Compact layout
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    @Test
    public void testShowContactDetails() {
        int index = contactList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a contact.");
            return;
        }

        ContactApp.Contact contact = contacts.get(index);
        nameLabel.setText("Name: " + contact.name);
        phoneLabel.setText("Phone: " + contact.phone);
        emailLabel.setText("Email: " + contact.email);
        favoriteCheckBox.setSelected(contact.isFavorite);

        if (contact.imagePath != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(contact.imagePath)
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            profilePicture.setIcon(icon);
            profilePicture.setText("");
        } else {
            profilePicture.setIcon(null);
            profilePicture.setText("No Image");
        }

        cardLayout.show(mainPanel, "DETAILS");
    }

    @Test
    public void testSaveContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!");
            return;
        }

        contacts.add(new ContactApp.Contact(name, phone, email, "Friends", selectedProfileImage, false));
        contactListModel.addElement(name);

        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        selectedProfileImage = null;

        cardLayout.show(mainPanel, "LIST");
    }

    @Test
    public void testEditContact() {
        int index = contactList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a contact to edit.");
            return;
        }

        ContactApp.Contact contact = contacts.get(index);
        nameField.setText(contact.name);
        phoneField.setText(contact.phone);
        emailField.setText(contact.email);
        selectedProfileImage = contact.imagePath;

        cardLayout.show(mainPanel, "CREATE");

    }

    @Test
    public void testDeleteContact() {
        int index = contactList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a contact to delete.");
            return;
        }

        contacts.remove(index);
        contactListModel.remove(index);
        JOptionPane.showMessageDialog(frame, "Contact deleted successfully.");

    }

    @Test
    public void testSearchContacts() {
        String query = searchField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();

        List<ContactApp.Contact> filteredContacts = contacts.stream()
                .filter(c -> c.name.toLowerCase().contains(query))
                .collect(Collectors.toList());
        for (ContactApp.Contact contact : filteredContacts) {
            filteredModel.addElement(contact.name);
        }

        contactList.setModel(filteredModel);
    }

    @Test
    public void testChooseProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedProfileImage = fileChooser.getSelectedFile().getAbsolutePath();
            profilePicture.setIcon(new ImageIcon(new ImageIcon(selectedProfileImage)
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            profilePicture.setText("");
        }
    }

    private void searchContacts() {
        testSearchContacts();
    }

    private void showContactDetails() {
        testShowContactDetails();
    }

    private void deleteContact() {
        testDeleteContact();
    }

    private void editContact() {
        testEditContact();
    }

    private void saveContact() {
        testSaveContact();
    }

    private void chooseProfileImage() {
        testChooseProfileImage();
    }
}

class ContactApp {
    public static class Contact {
        public String name;
        public String phone;
        public String email;
        public String group;
        public String imagePath;
        public boolean isFavorite;

        public Contact(String name, String phone, String email, String group, String imagePath, boolean isFavorite) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.group = group;
            this.imagePath = imagePath;
            this.isFavorite = isFavorite;
        }
    }
}